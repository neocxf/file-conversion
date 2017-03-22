package com.derbysoft.dhp.fileserver.api.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

/**
 *  Adapter for dealing with remote http request.
 *
 *  As developing in spring world, expose this class as a bean, and reuse it as mush as possible. All the lifecycle of the instance
 *  has already been taken care of.
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class HttpClientAdapter {
    private static final Logger logger = Logger.getLogger(InnerLoggerSetting.HTTP_LOGGER_NAME);

    private static final String CHARSET = "UTF-8";

    private CloseableHttpClient httpClient;

    /**
     *  handle the post request, default use {@link UrlEncodedFormEntity}
     *
     * @param uri the post uri
     * @param paramPairs post params
     * @param responseHandler hook handler of the response
     * @throws IOException if the http post request fail for some reason
     */
    public void handlePostRequest(final String uri ,final List<NameValuePair> paramPairs, AbstractResponseHandler<?> responseHandler) throws IOException {
        HttpPost request = new HttpPost(uri);
        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(paramPairs, CHARSET);
        request.setEntity(reqEntity);

        httpClient.execute(request, responseHandler);

    }

    /**
     *  handle the get request
     * @param uri the get uri
     * @param responseHandler hook callback handler of the response
     * @throws IOException if the http get request fail for some reason
     */
    public void handleGetRequest(final String uri, AbstractResponseHandler<?> responseHandler) throws IOException {
        HttpGet request = new HttpGet(uri);

        httpClient.execute(request, responseHandler);
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

}
