package top.neospot.conversion.core.server;

import top.neospot.conversion.api.support.ObjectFactory;
import top.neospot.conversion.core.util.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

import java.io.IOException;

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
    @Value("${phantomjs.connecttimeout}")
    private int connectTimeout;
    @Value("${phantomjs.readtimeout}")
    private int readTimeout;
    @Value("${phantomjs.outputsize:A4}")
    private String size;

    @Autowired
    private TempDir tempDir;

    @Override
    public PhantomjsClient create() throws IOException {
        String longScript = tempDir.getLongScriptName(script);

        int generatedPort = SocketUtils.findAvailableTcpPort(port);;
        logger.debug("in make PhantomjsClient, exec: " + exec + ", script: " +  longScript + ", host: " +  host + ", port: " + generatedPort + ", outputsize: " + size);
        return new PhantomjsClient.PhantomjsClientBuilder(exec, longScript, host, generatedPort).withOutputSize(size).create();
    }

    @Override
    public boolean validate(PhantomjsClient client) {
        return false;
    }

    @Override
    public void destroy(PhantomjsClient client) {
        client.destory();
    }

    @Override
    public void activate(PhantomjsClient client) {
        // NOOP
    }

    @Override
    public void passivate(PhantomjsClient client) {
        // NOOP
    }
}
