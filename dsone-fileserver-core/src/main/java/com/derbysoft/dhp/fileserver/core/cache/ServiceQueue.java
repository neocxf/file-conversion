package com.derbysoft.dhp.fileserver.core.cache;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public interface ServiceQueue<T> {
    /**
     *  retrieve one service T from the queue
     * @return the service
     * @throws InterruptedException
     */
    public T borrowService() throws InterruptedException;

    /**
     *  added the service to the queue
     * @param t service
     * @throws InterruptedException
     */
    public void offerService(T t) ;
}
