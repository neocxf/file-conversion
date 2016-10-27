package com.derbysoft.dhp.fileserver.core.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public class TempDirTest {
    public static void main(String[] args) throws MalformedURLException {
        String fileUrl = "file:/home/fei/.m2/repository/com/derbysoft/dhp/dsone-fileserver-core/1.0-SNAPSHOT/dsone-fileserver-core-1.0-SNAPSHOT.jar!/rasterize.js";

        URL url = Thread.currentThread().getContextClassLoader().getResource("phantomjs/rasterize.js");
        System.out.println(url.getPath());
    }

}
