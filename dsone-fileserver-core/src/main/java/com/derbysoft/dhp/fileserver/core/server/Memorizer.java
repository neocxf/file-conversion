package com.derbysoft.dhp.fileserver.core.server;

import java.util.concurrent.*;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class Memorizer<A, V> implements Computable<A, V> {

    private final ConcurrentHashMap<A, Future<? extends V>> cache = new ConcurrentHashMap<>();

    private final Computable<A, V> c;
    private final Executor pool = Executors.newFixedThreadPool(10);

    private ServiceQueue<Computable<A, V>> serviceQueue;

    public Memorizer(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {

        while (true) {
            Future<? extends V> f = cache.get(arg);
            if (f == null) {
                final Computable<A, V> c1 = serviceQueue.borrowService();

                Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return c1.compute(arg);
                    }
                };
//                f = cache.putIfAbsent(arg, ft);
//
//                if (f == null) {
//                    f = ft;
//                    ft.run();
//                }

                RunnableFuture<V> ft = new FutureTask<V>(eval); // inspired by AbstractExecutorService#submit(Callable<T> task)

                f = cache.putIfAbsent(arg, ft);

                if (f == null) {
                    f = ft;
                    pool.execute(ft);
                }

                try {
                    return blockForResponse(f, arg);
                } finally {
                    serviceQueue.offerService(c1);
                }
            } else {
                return blockForResponse(f, arg);
            }

        }
    }

    private V blockForResponse( Future<? extends V> f, A arg) throws InterruptedException {

        try {
            return f.get();
        }
        catch  (CancellationException e) {
            cache.remove(arg, f);
        }
        catch  (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
