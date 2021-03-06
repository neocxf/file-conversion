package top.neospot.conversion.core.server;

import top.neospot.conversion.api.exception.ComputeFailedException;
import top.neospot.conversion.api.support.Computable;
import top.neospot.conversion.api.util.OutputSize;
import top.neospot.conversion.core.exception.PhantomRenderException;
import top.neospot.conversion.core.server.PhantomjsClient.FileConverterKey;
import top.neospot.conversion.core.server.PhantomjsClient.PhantomjsResponse;
import top.neospot.conversion.core.server.PhantomjsClient.ResponseEntity;
import top.neospot.conversion.core.util.TempDir;
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
public class PhantomjsClient implements Computable<String, FileConverterKey, ResponseEntity<PhantomjsResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsClient.class);

    private Process process;

    private String host = "127.0.0.1";
    private int bindingPort = -1;
    private int connectTimeout = 5000;
    private int readTimeout = 10000;
    private static String outputSize = "1024px*768px";


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

        if (builder.outputSize != null) {
            outputSize = builder.outputSize;
        }
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


    public ResponseEntity<PhantomjsResponse> compute(String params, FileConverterKey converterKey) throws SocketTimeoutException, TimeoutException, ComputeFailedException {
        ResponseEntity<PhantomjsResponse> entity = new ResponseEntity<>();
        String host = "127.0.0.1";
        int port = getBindingPort();
        int connectTimeout = getConnectTimeout();
        int readTimeout = getReadTimeout();

        try {
            URL url = new URL("http://" + host + ":"
                    + port + "/");
            logger.info("params: " + params + ", converterKey: " + converterKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setInstanceFollowRedirects(false);

            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes("utf-8"));
            out.close();

            if (connection.getResponseCode() > 400) {
                logger.error("Phantomjs Client serve at port{} " + port  + " failed to render the page " + converterKey.getUrl() + ", give the response: {} "
                        + connection.getResponseMessage() + ", and response code: {} " + connection.getResponseCode());

                throw new PhantomRenderException(String.format("Compute call failed, may be the url %s you provided is invalid!" +
                        " usually, there are possibly serval reason for such failure. " +
                        " 1. the url you provided can be accessed through the browser !!" +
                        " 2. Please ensure that the url starts with the standard http:// or https// url", converterKey.getUrl()), converterKey);
            }

            InputStream in = connection.getInputStream(); // if the responseCode is 404, then it may cause a FileNotFoundException, and this clause will not be executed
            String response = IOUtils.toString(in, "utf-8");

            entity.setStatusCode(connection.getResponseCode());
            entity.setResponse(response);

            logger.info("Phantomjs Client serve at port{} " + port  + " give the response: " + connection.getResponseMessage() + ", " + connection.getResponseCode());
            in.close();
        } catch (SocketTimeoutException ste) {
            logger.error("Phantomjs Client serve at port{} " + port  + " failed to render the page " + converterKey.getUrl() + ", give the response: {} readTimeout");
            throw new SocketTimeoutException(ste.getMessage());
        } catch (IOException e) {
            // here we throw the customized exception in case of 404 response, note that the throw here may be not intercepted by the ExceptionHandler
            // since, all the flow here were encapsulated at multi-threaded environment for ExecutionException
            // So, if you want to actually throw the exception and let the ExceptionHandler method catch the exception, you have to throw again
            throw new PhantomRenderException(e.getMessage(),  converterKey);
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

    public static PhantomjsClientBuilder custom() {
        return new PhantomjsClientBuilder();
    }

    public static class PhantomjsClientBuilder {
        private String exec;
        private String script;
        private String host;
        private int port;
        private int connectTimeout;
        private int readTimeout;
        private String outputSize;

        protected PhantomjsClientBuilder() {

        }

        /**
         *  build the PhantomjsClient
         * @param exec Phantomjs execution context
         * @param script the js file
         */
        protected PhantomjsClientBuilder(String exec, String script) {
            this.exec = exec;
            this.script = script;
            this.host = "127.0.0.1";
            this.port = SocketUtils.findAvailableTcpPort();
        }

        protected PhantomjsClientBuilder(String exec, String script, String host, int port) {
            this.exec = exec;
            this.script = script;
            this.host = host;
            this.port = port;
        }

        protected PhantomjsClientBuilder withHost(String host) {
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

        public PhantomjsClientBuilder withOutputSize(String outputSize) {
            this.outputSize = outputSize;
            return this;
        }

        public PhantomjsClient create() throws IOException {
            return new PhantomjsClient(this);
        }

    }

    /**
     *  the {@link FileConverterKey} was used to identified whether the coming-converting request has already been cached
     */
    public static class FileConverterKey {
        private String url;
        private String fileType;
        private int resolveTime = 200;
        private String outputSize = "A4";

        public FileConverterKey() {}

        public FileConverterKey(String url, String fileType, int resolveTime, String outputSize) {
            withUrl(url).withFileType(fileType).withResolveTime(resolveTime).withOutputSize(outputSize);
        }

        public FileConverterKey withUrl(String url) {
            this.url = url;
            return this;
        }

        public FileConverterKey withFileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public FileConverterKey withResolveTime(int resolveTime) {
            if (resolveTime < 100 || resolveTime > 5000) {
                logger.error("invalid resovleTime, the max resolve time is 5000, min resolve time is 100");
                return this;
            }

            this.resolveTime = resolveTime;
            return this;
        }

        public FileConverterKey withOutputSize(String outputSize) {
            if (!OutputSize.assertValid(outputSize)) {
                logger.error("invalid outputsize: {} ", outputSize);
            }

            this.outputSize = outputSize;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public String getFileType() {
            return fileType;
        }

        public int getResolveTime() {
            return resolveTime;
        }

        public String getOutputSize() {
            return this.outputSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileConverterKey that = (FileConverterKey) o;

            if (resolveTime != that.resolveTime) return false;
            if (!url.equals(that.url)) return false;
            if (!fileType.equals(that.fileType)) return false;
            return outputSize.equals(that.outputSize);
        }

        @Override
        public int hashCode() {
            int result = url.hashCode();
            result = 31 * result + fileType.hashCode();
            result = 31 * result + resolveTime;
            result = 31 * result + outputSize.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "FileConverterKey{" +
                    "url='" + url + '\'' +
                    ", fileType='" + fileType + '\'' +
                    ", resolveTime=" + resolveTime +
                    ", outputSize='" + outputSize + '\'' +
                    '}';
        }
    }

    public static class ConverterConfig {
        private String url;
        private String fileName;
        private String outputSize;
        private float zoomFactor = 1;
        private int resolveTime = 200;

        public ConverterConfig(String url, String targetFileName, int resolveTime, String outputSize, float zoomFactor) {
            this(url, targetFileName, resolveTime);
            setOutputSize(outputSize);
            setZoomFactor(zoomFactor);
        }

        public ConverterConfig(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = PhantomjsClient.outputSize;
        }

        public ConverterConfig(String url, String fileName, int resolveTime) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = PhantomjsClient.outputSize;
            setResolveTime(resolveTime);
        }

        public ConverterConfig(String url, String fileName, String outputSize) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = outputSize;
        }

        public ConverterConfig(String url, String fileName, String outputSize, float zoomFactor) {
            this.url = url;
            this.fileName = fileName;
            this.outputSize = outputSize;
            this.zoomFactor = zoomFactor;
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

        public float getZoomFactor() {
            return zoomFactor;
        }

        public void setZoomFactor(float zoomFactor) {
            this.zoomFactor = zoomFactor;
        }

        public int getResolveTime() {
            return resolveTime;
        }

        public void setResolveTime(int resolveTime) {
            if (resolveTime < 100 || resolveTime > 5000) {
                logger.debug("invalid resovleTime, the max resolve time is 5000, min resolve time is 100");
                return;
            }

            this.resolveTime = resolveTime;
        }
    }
}
