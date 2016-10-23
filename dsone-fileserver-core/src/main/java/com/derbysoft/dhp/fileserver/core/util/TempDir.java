/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.derbysoft.dhp.fileserver.core.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * temporary directory configuration for the whole project
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

    /**
     *  copy the rasterize.js file from the jar to the temp directory.
     *
     *  It seems that Tomcat7 has a different way of loading the file inside the jar file. Using the getResource(String file) method at web environment
     *  actually cause a lot of troubles. For the details, you can view the following stackoverflow topic:
     *  1, http://stackoverflow.com/questions/676250/different-ways-of-loading-a-file-as-an-inputstream/20069749#20069749
     *  2, http://stackoverflow.com/questions/6608795/what-is-the-difference-between-class-getresource-and-classloader-getresource?noredirect=1&lq=1
     *  3, http://stackoverflow.com/questions/2161054/where-to-place-and-how-to-read-configuration-resource-files-in-servlet-based-app?noredirect=1&lq=1
     *  http://stackoverflow.com/questions/14559158/not-able-to-read-file-from-jar-in-tomcat
     *
     * @throws IOException
     */
//    @PostConstruct
//    private void copyResources() throws IOException  {
//        logger.debug("------------------------- copying phantomjs related file to TEMP subfolder --------------------------");
//
//        // using getResource() may cause the WebApplicationClassLoader not finding the resource.
//        // Idealy, the following two ways should both work fine. But at Tomcat 7, the following will not work.
////        String url = Thread.currentThread().getContextClassLoader().getResource("rasterize.js").getFile();
////        File file = new File(url);
////        FileUtils.copyFileToDirectory(file, getPhantomJsDir().toFile() );
//
//
//        // it seems that at web environment, when we want to load the file inside the jar, we could only use getResourceAsStream method
//        // otherwise, error occurs.
//        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("phantomjs/rasterize.js");
//        FileUtils.copyInputStreamToFile(is, new File(getLongScriptName("phantomjs/rasterize.js")) );
//    }

    @PostConstruct
    private void copyResources() {

        URL u = getClass().getProtectionDomain().getCodeSource().getLocation();
        URLClassLoader jarLoader = new URLClassLoader(new URL[]{u}, Thread.currentThread().getContextClassLoader());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(jarLoader);
        try {
            Resource[] resources = resolver.getResources("classpath*:/phantomjs/*.js*");
            for (Resource resource : resources) {
                logger.debug("Copying " + resource.getFilename() + " to " + TempDir.getPhantomJsDir());
                Path path = Paths.get(TempDir.getPhantomJsDir().toString(), resource.getFilename());
                File f = Files.createFile(path).toFile();
                f.deleteOnExit();

                try (InputStream in = resource.getInputStream();
                     OutputStream out=new FileOutputStream(f))
                {
                    IOUtils.copy(in, out);
                }
            }
        } catch (IOException ioex) {
            logger.error("Error while setting up phantomjs environment: " + ioex.getMessage());
            ioex.printStackTrace();
        }
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

    /**
     *  return the full path of the rasterize.js
     *  @param scriptName the script's short file name.  aka, the file's name without the directory included
     * @return the full file's name with the directory included
     */
    public String getLongScriptName(String scriptName) {
        return getPhantomJsDir().toString() + File.separator + scriptName;
    }

    /**
     *  get the full path of the file's link
     * @param filenameWithExtension the short file name. aka, the file's name without the directory included
     * @return the full file's name with the directory included
     */
    public static String getDownloadLink(String filenameWithExtension) {
        return getOutputDir().toAbsolutePath().toString() + File.separator +  filenameWithExtension;
    }

    public static void main(String[] args) throws MalformedURLException {
        String fileUrl = "file:/home/fei/.m2/repository/com/derbysoft/dhp/dsone-fileserver-core/1.0-SNAPSHOT/dsone-fileserver-core-1.0-SNAPSHOT.jar!/rasterize.js";

        URL url = Thread.currentThread().getContextClassLoader().getResource("phantomjs/rasterize.js");
        System.out.println(url.getPath());
    }

}
