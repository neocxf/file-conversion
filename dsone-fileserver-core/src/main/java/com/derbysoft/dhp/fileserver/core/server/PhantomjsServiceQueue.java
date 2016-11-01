package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.api.cache.ObjectFactory;
import com.derbysoft.dhp.fileserver.api.cache.ServiceQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component("serviceQueue")
public class PhantomjsServiceQueue<T> implements ServiceQueue<T>, InitializingBean, DisposableBean{
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsServiceQueue.class);

    private static final int DEFAULT_QUEUE_SIZE = 3;
    private final BlockingQueue<T> queue ;
    private final int size;

    @Autowired
    ObjectFactory<T> objectFactory;

    public PhantomjsServiceQueue() throws IOException, InterruptedException {
        this(DEFAULT_QUEUE_SIZE);
    }

    public PhantomjsServiceQueue(int size) throws IOException, InterruptedException {
        this.queue = new LinkedBlockingDeque<T>(size);
        this.size = size;
    }

    /**
     *  blocking to borrow the service
     *
     * @return the client
     * @throws InterruptedException
     */
    public T borrowService() throws InterruptedException {
        return queue.take();
    }

    /**
     *  added the service
     * @param t client
     */
    public void offerService(T t) {
        queue.offer(t);
    }

    private void initialize() throws IOException, InterruptedException {
        for (int i = 0; i < size; i++) {
            offerService(objectFactory.create());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug(" ServiceQueue initializing the instance queue ... ");
        initialize();
    }


    @Override
    public void destroy() throws Exception {
        logger.debug(" ServiceQueue destroying the instance queue ... ");

        for (T elem : queue) {
            objectFactory.destroy(elem);
        }
    }
}
