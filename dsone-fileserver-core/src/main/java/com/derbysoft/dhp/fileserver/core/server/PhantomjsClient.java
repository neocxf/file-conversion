package com.derbysoft.dhp.fileserver.core.server;

import com.derbysoft.dhp.fileserver.core.util.TempDir;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClient {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsClient.class);

    private Process process;

    public static class PhantomjsClientBuilder {
        private String exec;
        private String script;
        private String url;
        private String output;
        private String size;

        /**
         *  build the PhantomjsClient
         * @param exec Phantomjs execution context
         * @param script the js file
         * @param url the url of html page (both http url or file url are supported)
         * @param output the output file name
         */
        public PhantomjsClientBuilder(String exec, String script, String url, String output) {
            this.exec = exec;
            this.script = script;
            this.url = url;
            this.output = output;
        }

        public PhantomjsClientBuilder(String url, String output) {
            this.url = url;
            this.output = output;
        }

        /**
         *  additional configuration element for the build of the PhantomjsClient
         * @param size the output file's pixels
         * @return the builder
         */
        PhantomjsClientBuilder withSize(String size) {
            this.size = size;
            return this;
        }

        public PhantomjsClient create() throws IOException {
            return new PhantomjsClient(this);
        }

    }

    public PhantomjsClient(PhantomjsClientBuilder builder) throws IOException {
        final ArrayList<String> commands = new ArrayList<>();
        commands.add(builder.exec);
        commands.add(builder.script);
        commands.add(builder.url);
        commands.add(builder.output);
        commands.add(builder.size);


        ProcessBuilder pb = new ProcessBuilder(commands);

        logger.debug(" ------ tmp dir folder: {} " + TempDir.getTmpDir().toFile());

        pb.directory(TempDir.getOutputDir().toFile());
        process = pb.start();
    }

    public PhantomjsClient(String exec, String script, String... options) {


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

    public static String request(String params) throws SocketTimeoutException, TimeoutException {
        String response = "";
        String host = "localhost";
        int port = 8088;
        int connectTimeout = 5000;
        int readTimeout = 5000;

        try {
            URL url = new URL("http://" + host + ":"
                    + port + "/");

            // TEST with running a local phantom instance
            // url = new URL("http://" + host + ":7777/");
            // logger.log(Level.INFO, "requesting url: " + url.toString());
            // logger.log(Level.INFO, "parameters: " +  params);


            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes("utf-8"));
            out.close();
            InputStream in = connection.getInputStream();
            response = IOUtils.toString(in, "utf-8");

            in.close();
        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException(ste.getMessage());
        } catch (Exception e) {
        }
        return response;
    }

    private void initialize() {

    }

    public static void main(String[] args) throws TimeoutException, SocketTimeoutException {
//        new PhantomjsClient("/usr/bin/phantomjs", "rasterize.js", "report_data_1.html", "report_data_2.pdf", "1920px");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/highcharts-export-server/tutorials/rasterize.js", "http://www.jd.com", "jd.pdf", "1920px");
//        new PhantomjsClient("ls", "-a", "-l");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/starter/neo4j-parent/neo4j-web-starter2/src/main/resources/static/hello.js");
        Gson gson = new Gson();
        ConverterConfig config = new ConverterConfig("http://www.baidu.com", "pdf");
        String configStr = gson.toJson(config);
        System.out.println(configStr);
        request(configStr);
    }

    private static class ConverterConfig {
        private String url;
        private String type;

        public ConverterConfig() {
        }

        public ConverterConfig(String url, String type) {
            this.url = url;
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
