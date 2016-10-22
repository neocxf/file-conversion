/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derbysoft.dhp.fileserver.core.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author neo.fei {neoxf@gmail.com}
 */
@Component
public class TempDir {
    protected static Logger logger = LoggerFactory.getLogger(TempDir.class);

    public static Path tmpDir;
    public static Path outputDir;
    public static Path phantomJsDir;
    public static TempDir tempDir;

    static {
        try {
            tempDir = new TempDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public TempDir() throws IOException {
        System.out.println("temp dir initialized ...");

        tmpDir = Files.createTempDirectory("export");

        // Delete this directory on deletion of the JVM
        tmpDir.toFile().deleteOnExit();

        outputDir = Files.createDirectory(Paths.get(tmpDir.toString(), "output"));
        outputDir.toFile().deleteOnExit();

        phantomJsDir = Files.createDirectory(Paths.get(tmpDir.toString(), "phantomjs"));
        phantomJsDir.toFile().deleteOnExit();

        File dir = phantomJsDir.toFile();
        if (dir != null) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                FileUtils.deleteQuietly(tmpDir.toFile());
            }
        });

        logger.debug("Highcharts Export Server using " +TempDir.getTmpDir() + " as TEMP folder.");
    }

    @PostConstruct
    public void copyResources() throws IOException  {
        logger.info("------------------------- copying phantomjs related file to TEMP subfolder");

//            URL jsUrl = getClass().getClassLoader().getResource("rasterize.js");

//            URL jsUrl = TempDir.class.getClassLoader().getResource("rasterize.js");
//            String url = Thread.currentThread().getContextClassLoader().getResource("rasterize.js").getFile();
//            File file = new File(url);
//            FileUtils.copyFileToDirectory(file, getPhantomJsDir().toFile() );


        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/derbysoft/dhp/fileserver/core/util/rasterize.js");
        FileUtils.copyInputStreamToFile(is, new File(getRasterizeJs()) );

    }

    public static Path getTmpDir() {
        return tmpDir;
    }

    public static Path getOutputDir() {
        return outputDir;
    }

    public static Path getPhantomJsDir() {
        return phantomJsDir;
    }

    public String getRasterizeJs() {
        return getPhantomJsDir().toString() + File.separator + "rasterize.js";
    }

    public static String getDownloadLink(String filename) {
        String link = "files/" + filename;
        return link;
    }

    public static void main(String[] args) throws MalformedURLException {
        String fileUrl = "file:/home/fei/.m2/repository/com/derbysoft/dhp/dsone-fileserver-core/1.0-SNAPSHOT/dsone-fileserver-core-1.0-SNAPSHOT.jar!/rasterize.js";
//        String fileUrl = "file:/home/fei/.m2/repository/com/derbysoft/dhp/dsone-fileserver-core/1.0-SNAPSHOT/dsone-fileserver-core-1.0-SNAPSHOT.jar!/META-INF/MANIFEST.MF";

//        UrlResource resource = new UrlResource(fileUrl);
//
//        System.out.println(resource.getFilename());
//        System.out.println(resource.exists());

        URL url = Thread.currentThread().getContextClassLoader().getResource("com/derbysoft/dhp/fileserver/core/util/rasterize.js");
        System.out.println(url.getPath());
    }

}
