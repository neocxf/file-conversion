package com.derbysoft.dhp.fileserver.api.support;

import com.derbysoft.dhp.fileserver.api.exception.ComputeFailedException;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;


/**
 * cache impl for future calculation (time-consuming, expensive action)
 *
 * @param <A> the argument of the computation take
 * @param <K> the key to identify the computation process in case of cache
 * @param <V> the computation result
 * @author neo.fei {neocxf@gmail.com}
 */
public class CacheFutureMemorizer<A, K, V> implements Computable<A, K, V> {

    private final ConcurrentMap<K, Future<V>> cache;

    private final Executor executor;

    private Computable<A, K, V> c;

    private ServiceQueue<Computable<A, K, V>> serviceQueue;

    public CacheFutureMemorizer(ConcurrentMap<K, Future<V>> cache, Executor executor, ServiceQueue<Computable<A, K, V>> serviceQueue) {
        this.cache = cache;
        this.executor = executor;
        this.serviceQueue = serviceQueue;
    }

    public CacheFutureMemorizer(Executor executor, ServiceQueue<Computable<A, K, V>> serviceQueue) {
        this(new ConcurrentHashMap<K, Future<V>>(), executor, serviceQueue);
    }

    @Override
    public V compute(A arg, K key) throws InterruptedException {

        while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                final Computable<A, K, V> c1 = serviceQueue.borrowService();

                Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws InterruptedException, IOException, TimeoutException {
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
                    cache.remove(key);
                }
                catch  (ExecutionException e) {
                    f.cancel(true);
                    // the cache remove may trigger the iteration of the map which may cause another time ExecutionException
                    // but after this step, the stale and error task will be removed
                    cache.remove(key);
                    throw new ComputeFailedException(e.getMessage());
                }
                finally {
                    serviceQueue.offerService(c1); // release the resource to the queue
                }

            } else {

                try {
                    return f.get();
                }
                catch  (CancellationException e) {
                    cache.remove(key);
                }
                catch  (ExecutionException e) {
                    f.cancel(true);
                    cache.remove(key);
                    throw new ComputeFailedException(e.getMessage());
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
