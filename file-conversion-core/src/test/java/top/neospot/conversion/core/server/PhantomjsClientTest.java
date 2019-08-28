package top.neospot.conversion.core.server;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class PhantomjsClientTest {

    @Test
    public void testRasterizeJs() throws TimeoutException, SocketTimeoutException {
        //        new PhantomjsClient("/usr/bin/phantomjs", "rasterize.js", "report_data_1.html", "report_data_2.pdf", "1920px");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/highcharts-export-server/tutorials/rasterize.js", "http://www.jd.com", "jd.pdf", "1920px");
//        new PhantomjsClient("ls", "-a", "-l");
//        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/starter/neo4j-parent/neo4j-web-starter2/src/main/resources/static/hello.js");
        Gson gson = new Gson();
        PhantomjsClient.ConverterConfig config = new PhantomjsClient.ConverterConfig("http://www.baidu.com", "baidu.pdf");
//        ConverterConfig config = new ConverterConfig("http://www.csdn.net", "csdn.pdf");
//        ConverterConfig config = new ConverterConfig("http://www.sdsdsdsd/asas", "nosuchdomain1.pdf");
        String configStr = gson.toJson(config);
        System.out.println(configStr);
        PhantomjsClient.ResponseEntity entity = compute(configStr, "http://www.baidu.com");
        System.out.println(entity.getResponse());
        System.out.println(entity.getStatusCode());

        PhantomjsClient.PhantomjsResponse response;
        response = gson.fromJson(entity.getResponse(), PhantomjsClient.PhantomjsResponse.class);

        System.out.println(response.getMsg());
        System.out.println(response.getUrl());
        System.out.println(response.getFileName());

    }


    public static PhantomjsClient.ResponseEntity compute(String params, String urlKey) throws SocketTimeoutException, TimeoutException {
        PhantomjsClient.ResponseEntity entity = new PhantomjsClient.ResponseEntity();
        String host = "127.0.0.1";
        int port = 8088;
        int connectTimeout = 5000;
        int readTimeout = 5000;

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
            System.out.println("Phantomjs Client serve at port{} " + port  + " give the response: " + connection.getResponseMessage() + ", " + connection.getResponseCode());
            in.close();
        } catch (SocketTimeoutException ste) {
            throw new SocketTimeoutException(ste.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
