package com.derbysoft.dhp.fileserver.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClient {
    private static final Logger logger = LoggerFactory.getLogger(PhantomjsClient.class);

    private Process process;

    public PhantomjsClient(String exec, String script, String... options) {


        if (script.isEmpty()) {
//			script = TempDir.getPhantomJsDir().toAbsolutePath().toString() + separator + "highcharts-convert.js";
            script = "rasterize.js";
        }

        try {
            final ArrayList<String> commands = new ArrayList<>();
            commands.add(exec);
            commands.add(script);
            Stream.of(options).forEach(commands::add);

            logger.info("Thread.currentThread: {} " + Thread.currentThread().getName() + ", " + commands.toString());


            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File("/home/fei/workspace/highcharts-export-server/tutorials"));
            File log = new File("log");
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

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

//    public String request(String params) throws SocketTimeoutException, SVGConverterException, TimeoutException {
//        String response = "";
//        String host = "localhost";
//        int port = 7777;
//        int connectTimeout = 5000;
//        int readTimeout = 5000;
//
//        try {
//            URL url = new URL("http://" + host + ":"
//                    + port + "/");
//
//            // TEST with running a local phantom instance
//            // url = new URL("http://" + host + ":7777/");
//            // logger.log(Level.INFO, "requesting url: " + url.toString());
//            // logger.log(Level.INFO, "parameters: " +  params);
//
//
//            URLConnection connection = url.openConnection();
//            connection.setDoOutput(true);
//            connection.setConnectTimeout(connectTimeout);
//            connection.setReadTimeout(readTimeout);
//
//            OutputStream out = connection.getOutputStream();
//            out.write(params.getBytes("utf-8"));
//            out.close();
//            InputStream in = connection.getInputStream();
//            response = IOUtils.toString(in, "utf-8");
//
//            in.close();
//        } catch (SocketTimeoutException ste) {
//            throw new SocketTimeoutException(ste.getMessage());
//        } catch (Exception e) {
//            throw new SVGConverterException(e.getMessage());
//        }
//        return response;
//    }

    private void initialize() {

    }

    public static void main(String[] args) {
//        new PhantomjsClient("/usr/bin/phantomjs", "rasterize.js", "report_data_1.html", "report_data_2.pdf", "1920px");
        new PhantomjsClient("/usr/bin/phantomjs", "rasterize.js", "http://www.youku.com", "youku.pdf", "1920px");
//        new PhantomjsClient("ls", "-a", "-l");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/starter/neo4j-parent/neo4j-web-starter2/src/main/resources/static/hello.js");



    }
}
