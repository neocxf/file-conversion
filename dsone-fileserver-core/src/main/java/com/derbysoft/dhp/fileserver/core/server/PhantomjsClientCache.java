package com.derbysoft.dhp.fileserver.core.server;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  cache for all the phantomjs work
 *
 *  NOTE: the impl should be thread safe, as they may be retrieved from multi-threaded environment
 *
 *  the key should be the url of the resources(http url or file url) that gonna to be transformed,
 *  the value should be the full path of the already transformed resources.
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClientCache {
    private static final Map<String, TwoTuple<String, Integer>> resourceCache = new ConcurrentHashMap<>();
    public static final String DEFAULT_FILENAME = "_default_phantomjs_cache_file";
    private static final TwoTuple<String, Integer> DEFAULT_TUPLE = new TwoTuple<>(DEFAULT_FILENAME, 0);

    private static final ReentrantLock lock = new ReentrantLock(true);

    /**
     *  given the url(could be http url or file url), return the file name of the converting file
     * @param url the url
     * @return
     */
    public static String get(String url) {
        lock.lock();

        try {
            TwoTuple<String, Integer> cacheVal = resourceCache.get(url);
            if (cacheVal == null) {
                cacheVal = DEFAULT_TUPLE;
            }

            Integer hits = cacheVal.getT2();
            hits ++;
            cacheVal.setT2(hits );

            return cacheVal.getT1();
        } finally {
            lock.unlock();
        }
    }

    public static void store(String url, String fileName) {
        resourceCache.putIfAbsent(url, new TwoTuple<>(fileName, 0));
    }

    /**
     *  get the read-only version of the ResourceCache for latter analysis
     * @return read-only version of the resourceCache
     */
    public static Map<String, TwoTuple<String, Integer>> getResourceCache() {
        return Collections.unmodifiableMap(resourceCache);
    }

    public static class TwoTuple<T1, T2> {
        private T1 t1;
        private T2 t2;

        public TwoTuple(T1 t1, T2 t2) {
            this.t1 = t1;
            this.t2 = t2;
        }

        public T1 getT1() {
            return t1;
        }

        public void setT1(T1 t1) {
            this.t1 = t1;
        }

        public T2 getT2() {
            return t2;
        }

        public void setT2(T2 t2) {
            this.t2 = t2;
        }
    }
}
