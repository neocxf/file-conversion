package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.cache.CacheFutureMemorizer;
import com.derbysoft.dhp.fileserver.core.cache.Computable;
import com.derbysoft.dhp.fileserver.core.cache.ServiceQueue;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ConverterConfig;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.PhantomjsResponse;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component("serviceExecutor")
public class PhantomjsServiceExecutor implements InitializingBean {
   private static final Logger logger = LoggerFactory.getLogger(PhantomjsServiceExecutor.class);

    private CacheFutureMemorizer<String, String, ResponseEntity<PhantomjsResponse>> memorizer;

    @Autowired
    public PhantomjsServiceExecutor(Executor executorPool, ServiceQueue<Computable<String, String, ResponseEntity<PhantomjsResponse>>> serviceQueue) {
        this.memorizer = new CacheFutureMemorizer<>(executorPool, serviceQueue);
    }


    public ResponseEntity<PhantomjsResponse> execute(ConverterConfig configVo, final String url) throws InterruptedException {

        Gson gson = new Gson();

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

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug(" PhantomjsServiceExecutor initialized ...");
    }
}
