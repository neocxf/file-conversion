package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.api.support.CacheFutureMemorizer;
import com.derbysoft.dhp.fileserver.api.support.Computable;
import com.derbysoft.dhp.fileserver.api.support.ServiceQueue;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ConverterConfig;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.PhantomjsResponse;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component("serviceExecutor")
public class PhantomjsServiceExecutor implements InitializingBean, ApplicationContextAware {
   private static final Logger logger = LoggerFactory.getLogger(PhantomjsServiceExecutor.class);

    private ApplicationContext applicationContext;

    private CacheFutureMemorizer<String, String, ResponseEntity<PhantomjsResponse>> memorizer;

    @Autowired private Gson gson;

    public ResponseEntity<PhantomjsResponse> execute(ConverterConfig configVo, final String url) throws InterruptedException {

        String jsonStr = gson.toJson(configVo);

        logger.trace(" params: {} " + jsonStr);
        logger.trace(" url: {} " + url);

        logger.debug(" inspect the cache pool for Phantomjs converter ...");
        Map<String, Future<ResponseEntity<PhantomjsResponse>>> cache = memorizer.getResourceCache();
        cache.forEach((key, future) -> {
            try {
                logger.debug(key + " --------> " + future.get().of(PhantomjsResponse.class).getFileName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return memorizer.compute(jsonStr, url);

    }


    @SuppressWarnings("all")
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug(" PhantomjsServiceExecutor initialized ...");

        Executor executorPool = applicationContext.getBean("executorPool", Executor.class);
        ServiceQueue<Computable<String, String, ResponseEntity<PhantomjsResponse>>> serviceQueue = applicationContext.getBean("serviceQueue", ServiceQueue.class);
        ConcurrentMap<String, Future<ResponseEntity<PhantomjsResponse>>> phantomjsClientCacheMap = applicationContext.getBean("phantomjsClientCacheMap", ConcurrentMap.class);
        this.memorizer = new CacheFutureMemorizer<>(phantomjsClientCacheMap, executorPool, serviceQueue);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
