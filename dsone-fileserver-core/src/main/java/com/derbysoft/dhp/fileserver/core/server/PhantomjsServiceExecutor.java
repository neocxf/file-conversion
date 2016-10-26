package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component
public class PhantomjsServiceExecutor {

    @Autowired
    private AsyncTaskExecutor executorPool;

    @Autowired
    ServiceQueue<PhantomjsClient> serviceQueue;

    public Future<ResponseEntity> submit(final String params) {

        return executorPool.submit(new Callable<ResponseEntity>() {
            @Override
            public ResponseEntity call() throws Exception {
                ResponseEntity entity = null;
                PhantomjsClient client = null;
                try {
                    client = serviceQueue.borrowService();

                    entity = client.request(params);

                    return entity;

                } catch (InterruptedException | SocketTimeoutException | TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    serviceQueue.offerService(client);
                }

                return new ResponseEntity();
            }

        });

    }

}
