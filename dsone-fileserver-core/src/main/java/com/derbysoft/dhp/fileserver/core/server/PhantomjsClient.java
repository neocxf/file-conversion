package com.derbysoft.dhp.fileserver.core.server;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClient {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsClient.class);

    private Process process;

    private int bindingPort = -1;

    public static class PhantomjsClientBuilder {
        private String exec;
        private String script;
        private String host;
        private int port;
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


        public PhantomjsClient create() throws IOException {
            return new PhantomjsClient(this);
        }

    }

    public PhantomjsClient(PhantomjsClientBuilder builder) throws IOException {
        final ArrayList<String> commands = new ArrayList<>();
        commands.add(builder.exec);
        commands.add(builder.script);
        commands.add("" + builder.port);
        Stream.of(commands).forEach(System.out::println);


        ProcessBuilder pb = new ProcessBuilder(commands);

        logger.debug(" ------ tmp dir folder: {} " + TempDir.getTmpDir().toFile());

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

    private PhantomjsClient(String exec, String script, String... options) {


        if (script.isEmpty()) {
//			script = TempDir.getPhantomJsDir().toAbsolutePath().toString() + separator + "highcharts-convert.js";
            script = "phantomjs/rasterize.js";
        }

        try {
            final ArrayList<String> commands = new ArrayList<>();
            commands.add(exec);
            commands.add(script);
            Stream.of(options).forEach(commands::add);

            logger.info("Thread.currentThread: {} " + Thread.currentThread().getName() + ", " + commands.toString());


            ProcessBuilder pb = new ProcessBuilder(commands);

            logger.debug(" ------ tmp dir folder: {} " + TempDir.getTmpDir().toFile());

            pb.directory(TempDir.getOutputDir().toFile());
//            pb.directory(new File("/home/fei/tmp"));
//            File log = new File("log");
//            pb.redirectErrorStream(true);
//            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
            process = pb.start();


//			process = new ProcessBuilder(commands).start();
//            final BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//            String readLine = bufferedReader.readLine();
//            if (readLine == null || !readLine.contains("ready")) {
//                logger.warn("Command starting Phantomjs failed");
//                process.destroy();
////                throw new RuntimeException("Error, PhantomJS couldnot start");
//            }
//
//            initialize();
//
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    if (process != null) {
//                        logger.warn( "Shutting down PhantomJS instance, kill process directly, {0}", this.toString());
//                        try {
//                            process.getErrorStream().close();
//                            process.getInputStream().close();
//                            process.getOutputStream().close();
//                        } catch (IOException e) {
//                            logger.warn("Error while shutting down process: {0}", e.getMessage());
//                        }
//                        process.destroy();
//                    }
//                }
//            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Process getProcess() {
        return this.process;
    }

    public int getBindingPort() {
        return bindingPort;
    }

    /**
     *  wait indefinitely for the subprocess to end
     * @return return value of the subprocess
     * @throws InterruptedException
     */
    public int await() throws InterruptedException {
        return process.waitFor();
    }

    /**
     *  wait for the given time for the subprocess to done
     * @param sec seconds that wait for
     * @throws InterruptedException
     */
    public boolean await(int sec) throws InterruptedException {
        return process.waitFor(sec, TimeUnit.SECONDS);
    }

    public ResponseEntity request(String params) throws SocketTimeoutException, TimeoutException {
        ResponseEntity entity = new ResponseEntity();
        String host = "127.0.0.1";
        int port = getBindingPort();
        int connectTimeout = 5000;
        int readTimeout = 10000;

        try {
            URL url = new URL("http://" + host + ":"
                    + port + "/");

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
            System.out.println(" the response is: " + connection.getResponseMessage() + ", " + connection.getResponseCode());
            in.close();
        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException(ste.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static class ResponseEntity {
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

    private void initialize() {

    }

    public static void main(String[] args) throws TimeoutException, SocketTimeoutException {
//        new PhantomjsClient("/usr/bin/phantomjs", "rasterize.js", "report_data_1.html", "report_data_2.pdf", "1920px");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/highcharts-export-server/tutorials/rasterize.js", "http://www.jd.com", "jd.pdf", "1920px");
//        new PhantomjsClient("ls", "-a", "-l");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/starter/neo4j-parent/neo4j-web-starter2/src/main/resources/static/hello.js");
        Gson gson = new Gson();
        ConverterConfig config = new ConverterConfig("http://www.baidu.com", "baidu.pdf");
//        ConverterConfig config = new ConverterConfig("http://www.csdn.net", "csdn.pdf");
//        ConverterConfig config = new ConverterConfig("http://www.sdsdsdsd/asas", "nosuchdomain1.pdf");
        String configStr = gson.toJson(config);
        System.out.println(configStr);
//        ResponseEntity entity = request(configStr);
//        System.out.println(responseCode);
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
