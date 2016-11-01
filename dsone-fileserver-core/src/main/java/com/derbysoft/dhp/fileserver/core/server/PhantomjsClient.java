package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.api.cache.Computable;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.PhantomjsResponse;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import com.derbysoft.dhp.fileserver.core.util.TempDir;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 *  interact with the Phantomjs linux client stub
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClient implements Computable<String, String, ResponseEntity<PhantomjsResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsClient.class);

    private Process process;

    private String host = "127.0.0.1";
    private int bindingPort = -1;
    private int connectTimeout = 5000;
    private int readTimeout = 10000;


    public PhantomjsClient(PhantomjsClientBuilder builder) throws IOException {
        final ArrayList<String> commands = new ArrayList<>();
        commands.add(builder.exec);
        commands.add(builder.script);
        commands.add("" + builder.port);

        ProcessBuilder pb = new ProcessBuilder(commands);

        logger.debug(" ------ start a new Phantomjs Client, using port: {} " + builder.port +  ", using tmp dir folder: {} " + TempDir.getTmpDir().toFile());

        pb.directory(TempDir.getOutputDir().toFile());
        process = pb.start();

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String readLine = bufferedReader.readLine();
        if (readLine == null || !readLine.contains("READY")) {
            logger.warn("Command starting Phantomjs failed");
            process.destroy();
            throw new RuntimeException("Error, PhantomJS couldnot start");
        }

        bindingPort = builder.port;
    }

    public Process getProcess() {
        return this.process;
    }

    public String getHost() {
        return host;
    }

    public int getBindingPort() {
        return bindingPort;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void destory() {
        try {
			/* It's not enough to only destroy the process, this helps*/
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
        } catch (IOException e) {
            logger.warn("Error while shutting down process: {0}", e.getMessage());
        }

        process.destroy();
        process = null;
        logger.info(String.format("Destroyed phantomJS process running on port %d", getBindingPort()));
    }


    public ResponseEntity<PhantomjsResponse> compute(String params, String urlKey) throws SocketTimeoutException, TimeoutException {
        ResponseEntity<PhantomjsResponse> entity = new ResponseEntity<>();
        String host = "127.0.0.1";
        int port = getBindingPort();
        int connectTimeout = getConnectTimeout();
        int readTimeout = getReadTimeout();

        try {
            URL url = new URL("http://" + host + ":"
                    + port + "/");
            logger.debug("params: " + params + ", urlKey: " + urlKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setInstanceFollowRedirects(false);

            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes("utf-8"));
            out.close();
            InputStream in = connection.getInputStream();
            String response = IOUtils.toString(in, "utf-8");
            entity.setStatusCode(connection.getResponseCode());
            entity.setResponse(response);
            logger.info("Phantomjs Client serve at port{} " + port  + " give the response: " + connection.getResponseMessage() + ", " + connection.getResponseCode());
            in.close();
        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException(ste.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static class ResponseEntity<T> {
        private int statusCode = -1;
        private String response;

        public ResponseEntity() {
        }
        public ResponseEntity(int statusCode) {
            this.statusCode = statusCode;
        }

        public ResponseEntity(int statusCode, String response) {
            this.statusCode = statusCode;
            this.response = response;
        }

        public T of(Class<T> clazz) {
            assert response != null;
            return new Gson().fromJson(response, clazz);
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    public static class PhantomjsResponse {
        private String url;
        private String fileName;
        private String msg;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class PhantomjsClientBuilder {
        private String exec;
        private String script;
        private String host;
        private int port;
        private int connectTimeout;
        private int readTimeout;

        /**
         *  build the PhantomjsClient
         * @param exec Phantomjs execution context
         * @param script the js file
         */
        public PhantomjsClientBuilder(String exec, String script) {
            this.exec = exec;
            this.script = script;
            this.host = "127.0.0.1";
            this.port = SocketUtils.findAvailableTcpPort();
        }

        public PhantomjsClientBuilder(String exec, String script, String host, int port) {
            this.exec = exec;
            this.script = script;
            this.host = host;
            this.port = port;
        }

        public PhantomjsClientBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public PhantomjsClientBuilder withConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public PhantomjsClientBuilder withReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public PhantomjsClient create() throws IOException {
            return new PhantomjsClient(this);
        }

    }

    public static class ConverterConfig {
        private String url;
        private String fileName;
        private String outputSize = "1920px";
        private String zoom = "1";

        public ConverterConfig() {
        }

        public ConverterConfig(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        public ConverterConfig(String url, String fileName, String outputSize) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = outputSize;
        }

        public ConverterConfig(String url, String fileName, String outputSize, String zoom) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = outputSize;
            this.zoom = zoom;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getOutputSize() {
            return outputSize;
        }

        public void setOutputSize(String outputSize) {
            this.outputSize = outputSize;
        }

        public String getZoom() {
            return zoom;
        }

        public void setZoom(String zoom) {
            this.zoom = zoom;
        }
    }
}
