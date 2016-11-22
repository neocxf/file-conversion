package com.derbysoft.dhp.fileserver.client.http;

import com.derbysoft.dhp.fileserver.api.support.ObjectFactory;
import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Component("clientFactory")
public class HttpClientFactory implements ObjectFactory<CloseableHttpClient> {

    @Override
    public CloseableHttpClient create() throws IOException {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(5000)
                .setTcpNoDelay(true)
                .build();
        return HttpClients.custom()
                .setDefaultSocketConfig(socketConfig)
                .build();
    }

    @Override
    public boolean validate(CloseableHttpClient closeableHttpClient) {
        return false;
    }

    @Override
    public void destroy(CloseableHttpClient closeableHttpClient) {
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activate(CloseableHttpClient closeableHttpClient) {
    }

    @Override
    public void passivate(CloseableHttpClient closeableHttpClient) {

    }

    // in favor of failure of session
    private class SessionAdder implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context)
                throws HttpException, IOException {
            if ( session_id != null ) {
                request.setHeader("Cookie", session_id);
            }
        }

    }

    private class SessionKeeper implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            Header[] headers = response.getHeaders("Set-Cookie");
            if ( headers != null && headers.length == 1 ){
                session_id = headers[0].getValue();
            }
        }

    }

    private String session_id;

}
