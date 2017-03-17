package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.FileConverterKey;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.PhantomjsResponse;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import com.derbysoft.dhp.fileserver.core.util.TempDir;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *  cache configuration using guava
 *
 * @author neo.fei {neocxf@gmail.com}
 */
@Configuration
public class PhantomjsCacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsCacheConfig.class);

    private Cache<FileConverterKey, Future<ResponseEntity<PhantomjsResponse>>> cache;

    /**
     *  guava cache config, provide basic features as follows:
     *      1. expire cache config
     *      2. removal handling config
     * @return the map which is governed by the guava cache
     */
    @Bean(name = "phantomjsClientCacheMap")
    public ConcurrentMap<FileConverterKey, Future<ResponseEntity<PhantomjsResponse>>> concurrentMap() {
        cache =  CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .removalListener((RemovalListener<FileConverterKey, Future<ResponseEntity<PhantomjsResponse>>>) notification -> {
                    Future<ResponseEntity<PhantomjsResponse>> future = notification.getValue();
                    assert future != null;
                    assert future.isDone();

                    try {
                        ResponseEntity<PhantomjsResponse> responseEntity = future.get();

                        PhantomjsResponse response = responseEntity.of(PhantomjsResponse.class);

                        String longFileName = TempDir.getDownloadLink(response.getFileName());

                        logger.debug(" removing [" + response.getFileName() + "] from the cache" );

                        boolean deleteSuccess = Paths.get(longFileName).toFile().delete();
                    } catch (ExecutionException | InterruptedException e) {
                        // we can now ignore the ExecutionException, since we may call the remove when we find the task failed for some reason
                        // so, when system calls removal for failure ops, we should just silent swoop the exception
                    }

                })
                .build(new CacheLoader<FileConverterKey, Future<ResponseEntity<PhantomjsResponse>>>() {
                    @Override
                    public  Future<ResponseEntity<PhantomjsResponse>> load(FileConverterKey key) throws Exception {
                        return null;
                    }
                });

        return cache.asMap();
    }

    /**
     *  every one hour clean the expiry cache file item
     *
     *  we should manually clear the cache in our thread. Since the guava framework doesn't provide the expiring monitor thread.
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(cron = "0 0 2,4,6 * * MON-FRI")
    public void cleanCache() {
        logger.debug(" clean the cache ...");
        cache.cleanUp();
    }
}
