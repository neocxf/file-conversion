/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derbysoft.dhp.fileserver.core.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

    public static Path getTmpDir() {
        return tmpDir;
    }

    public static Path getOutputDir() {
        return outputDir;
    }

    public static Path getPhantomJsDir() {
        return phantomJsDir;
    }

    public static String getDownloadLink(String filename) {
        String link = "files/" + filename;
        return link;
    }



}
