package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component
public class PhantomjsServiceExecutor implements InitializingBean {

    @Autowired
    private Executor executorPool;

    @Autowired
    ServiceQueue<PhantomjsClient> serviceQueue;

    @Autowired
    Memorizer<String, ResponseEntity> memorizer;


    public ResponseEntity submit(final String params) throws InterruptedException {

        return memorizer.compute(params);

//        return executorPool.submit(new Callable<ResponseEntity>() {
//            @Override
//            public ResponseEntity call() throws Exception {
//                ResponseEntity entity = null;
//                PhantomjsClient client = null;
//                try {
//                    client = serviceQueue.borrowService();
//
//                    entity = client.compute(params);
//
//                    return entity;
//
//                } catch (InterruptedException | SocketTimeoutException | TimeoutException e) {
//                    e.printStackTrace();
//                } finally {
//                    serviceQueue.offerService(client);
//                }
//
//                return new ResponseEntity();
//            }
//
//        });

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
