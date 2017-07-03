package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.api.support.ObjectFactory;
import com.derbysoft.dhp.fileserver.api.support.ServiceQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component("serviceQueue")
@PropertySource({"classpath:phantomjs.properties"})
public class PhantomjsServiceQueue<T> implements ServiceQueue<T>, InitializingBean, DisposableBean{
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsServiceQueue.class);

    private BlockingQueue<T> queue ;

    /**
     *  the default pool size is 3,
     *  you can alter the size by passing through environment variable or setting from phantomjs.properties
     *  the environment variable will take first place, then the phantomjs.properties
     */
    @Value("${phantomjs.pool.size:3}")
    private int size;

    @Autowired
    ObjectFactory<T> objectFactory;

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
        logger.debug(" ServiceQueue initializing the instance queue with size [" + size + "] ...");
        this.queue = new LinkedBlockingDeque<T>(size);
        initialize();
    }


    @Override
    public void destroy() throws Exception {
        logger.debug(" ServiceQueue destroying the instance queue with size [" + size + "] ...");

        for (T elem : queue) {
            objectFactory.destroy(elem);
        }
    }
}
