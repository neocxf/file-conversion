package com.derbysoft.dhp.fileserver.core.cache;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *  represent a long-time consuming job
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public interface Computable<A, K, V> {

    /**
     *  execute the long-time consuming job synchronously. and, return the value
     *
     *  specially, the impl of this interface {@link CacheFutureMemorizer} are mean to serve as an cache with {@link java.util.concurrent.Future} to
     *  avoid the repeatedly processing of long-time consuming job.
     *
     * @param arg the argument that the Computable take
     * @param key the key which may be used for the cache
     * @return V the result after a long-time job done.
     * @throws InterruptedException if the asynchronous job is interrupted
     * @throws IOException I/O error
     * @throws TimeoutException the asynchronous job times out
     */
    V compute(A arg, K key) throws InterruptedException, IOException, TimeoutException;
}
