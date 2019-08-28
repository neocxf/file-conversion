package top.neospot.conversion.core.server;

import top.neospot.conversion.api.support.CacheFutureMemorizer;
import top.neospot.conversion.api.support.Computable;
import top.neospot.conversion.api.support.ServiceQueue;
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

    private CacheFutureMemorizer<String, PhantomjsClient.FileConverterKey, PhantomjsClient.ResponseEntity<PhantomjsClient.PhantomjsResponse>> memorizer;

    @Autowired private Gson gson;

    public PhantomjsClient.ResponseEntity<PhantomjsClient.PhantomjsResponse> execute(PhantomjsClient.ConverterConfig configVo, final PhantomjsClient.FileConverterKey converterKey) throws InterruptedException {

        String jsonStr = gson.toJson(configVo);

        logger.trace(" params: {} " + jsonStr);
        logger.trace(" key info: {} " + converterKey);

        logger.debug(" inspect the cache pool for Phantomjs converter ...");
        Map<PhantomjsClient.FileConverterKey, Future<PhantomjsClient.ResponseEntity<PhantomjsClient.PhantomjsResponse>>> cache = memorizer.getResourceCache();
        cache.forEach((key, future) -> {
            try {
                logger.debug(key + " --------> " + future.get().of(PhantomjsClient.PhantomjsResponse.class).getFileName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return memorizer.compute(jsonStr, converterKey);

    }


    @SuppressWarnings("all")
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug(" PhantomjsServiceExecutor initialized ...");

        Executor executorPool = applicationContext.getBean("executorPool", Executor.class);
        ServiceQueue<Computable<String, PhantomjsClient.FileConverterKey, PhantomjsClient.ResponseEntity<PhantomjsClient.PhantomjsResponse>>> serviceQueue = applicationContext.getBean("serviceQueue", ServiceQueue.class);
        ConcurrentMap<PhantomjsClient.FileConverterKey, Future<PhantomjsClient.ResponseEntity<PhantomjsClient.PhantomjsResponse>>> phantomjsClientCacheMap = applicationContext.getBean("phantomjsClientCacheMap", ConcurrentMap.class);
        this.memorizer = new CacheFutureMemorizer<>(phantomjsClientCacheMap, executorPool, serviceQueue);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
