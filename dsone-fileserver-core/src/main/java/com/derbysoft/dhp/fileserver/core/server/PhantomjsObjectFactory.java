package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.util.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  maintain the lifecycle of the PhantomjsClient
 *
 * Created by fei on 10/22/16.
 */
@PropertySource({"classpath:phantomjs.properties"})
@Component("objectFactory")
public class PhantomjsObjectFactory implements ObjectFactory<PhantomjsClient> {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsObjectFactory.class);

    @Value("${phantomjs.exec}")
    private String exec;
    @Value("${phantomjs.script}")
    private String script;
    @Value("${phantomjs.host}")
    private String host;
    @Value("${phantomjs.port}")
    private int port;
    @Value("${phantomjs.outputsize}")
    private String size;

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private TempDir tempDir;

    @Override
    public PhantomjsClient create() throws IOException {
        String longScript = tempDir.getLongScriptName(script);

//        logger.debug("in makeObject, exec: " + exec + ", script: " +  script + ", url: " +  url + ", destFile: " + destFile + ", outputsize: " + size);
        int generatedPort = port + counter.getAndIncrement();
        PhantomjsClient client = new PhantomjsClient.PhantomjsClientBuilder(exec, longScript, host, generatedPort).create();
        return client;
    }

    @Override
    public boolean validate(PhantomjsClient object) {
        return false;
    }

    @Override
    public void destroy(PhantomjsClient object) {

    }

    @Override
    public void activate(PhantomjsClient object) {

    }

    @Override
    public void passivate(PhantomjsClient Object) {

    }
}
