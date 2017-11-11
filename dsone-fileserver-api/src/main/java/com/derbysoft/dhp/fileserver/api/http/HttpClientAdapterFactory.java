package com.derbysoft.dhp.fileserver.api.http;

/**
 *  produce <i>one and only one instance</i> of {@link HttpClientAdapter} for global use
 *
 *  lifecycle of HttpClient has already been taken care of
 *
 * By neo.chen{neocxf@gmail.com} on 2017/9/29.
 */
public enum HttpClientAdapterFactory {
    DEFAULT;

    private HttpClientAdapter clientAdapter;

    HttpClientAdapterFactory() {
        this.clientAdapter = new HttpClientAdapter();

        this.clientAdapter.initializeClient();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                HttpClientAdapterFactory.this.clientAdapter.closeClient();
            }
        });
    }

    public HttpClientAdapter getClientAdapter() {
        return this.clientAdapter;
    }
}
