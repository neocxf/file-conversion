package com.derbysoft.dhp.fileserver.api.cache;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

/**
 * cache impl for
 * @author neo.fei {neocxf@gmail.com}
 */
public class CacheFutureMemorizer<A, K, V> implements Computable<A, K, V> {

    private final ConcurrentHashMap<K, Future<V>> cache = new ConcurrentHashMap<>();

    private final Executor executor;

    private Computable<A, K, V> c;

    private ServiceQueue<Computable<A, K, V>> serviceQueue;

    public CacheFutureMemorizer(Executor executor, ServiceQueue<Computable<A, K, V>> serviceQueue) {
        this.executor = executor;
        this.serviceQueue = serviceQueue;
    }

    @Override
    public V compute(A arg, K key) throws InterruptedException {

        while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                final Computable<A, K, V> c1 = serviceQueue.borrowService();

                Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return c1.compute(arg, key);
                    }
                };

                RunnableFuture<V> ft = new FutureTask<>(eval); // inspired by AbstractExecutorService#execute(Callable<T> task)

                f = cache.putIfAbsent(key, ft);

                if (f == null) {
                    f = ft;
                    executor.execute(ft);
                }

                try {
                    return f.get();
                }
                catch  (CancellationException e) {
                    cache.remove(arg, f);
                }
                catch  (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    serviceQueue.offerService(c1); // release the resource to the queue
                }

            } else {

                try {
                    return f.get();
                }
                catch  (CancellationException e) {
                    cache.remove(arg, f);
                }
                catch  (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     *  get the read-only version of the ResourceCache for latter analysis
     * @return read-only version of the resourceCache
     */
    public Map<K, Future<V>> getResourceCache() {
        return Collections.unmodifiableMap(cache);
    }
}
