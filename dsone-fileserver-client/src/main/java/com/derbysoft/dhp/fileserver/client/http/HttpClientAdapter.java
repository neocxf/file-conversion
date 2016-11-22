package com.derbysoft.dhp.fileserver.client.http;

import com.derbysoft.dhp.fileserver.api.support.ObjectFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component
public class HttpClientAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientAdapter.class);

    private CloseableHttpClient httpClient;

    @Autowired
    ObjectFactory<CloseableHttpClient> clientFactory;

    /**
     *  handle the post request, default use {@link UrlEncodedFormEntity}
     *
     * @param uri the post uri
     * @param paramPairs post params
     * @param responseHandler hook handler of the response
     * @throws UnsupportedEncodingException
     */
    public void handlePostRequest(final String uri ,final List<NameValuePair> paramPairs, AbstractResponseHandler<?> responseHandler) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(uri);
        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(paramPairs);
        request.setEntity(reqEntity);
        try {
            httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void initializeClient() {
        httpClient = HttpClientPool.getClient();
    }

    @PreDestroy
    private void closeClient() {
        try {
            HttpClientPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @PostConstruct
//    private void initializeClient() {
//        try {
//            httpClient = clientFactory.create();
//        } catch (IOException e) {
//            logger.error(" error when trying to create the HttpClient .");
//            e.printStackTrace();
//        }
//    }
//
//    @PreDestroy
//    private void closeClient() {
//        clientFactory.destroy(httpClient);
//    }
}
