/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package top.neospot.conversion.core.util;

import top.neospot.conversion.api.util.FileUtils2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * temporary directory configuration for the whole project
 *
 * @author neo.fei {neoxf@gmail.com}
 */
@Component
public class TempDir {
    private static Logger logger = LoggerFactory.getLogger(TempDir.class);

    private static Path tmpDir;
    private static Path outputDir;
    private static Path phantomJsDir;


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

        logger.debug("Dsone File Server using " +TempDir.getTmpDir() + " as TEMP folder.");
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
    private void copyResources() throws IOException, URISyntaxException {
        logger.debug("------------------------- copying phantomjs related file to TEMP subfolder --------------------------");

        // using getResource() may cause the WebApplicationClassLoader not finding the resource.
        // Idealy, the following two ways should both work fine. But at Tomcat 7, the following will not work.
//        String url = Thread.currentThread().getContextClassLoader().getResource("rasterize.js").getFile();
//        File file = new File(url);
//        FileUtils.copyFileToDirectory(file, getPhantomJsDir().toFile() );


        // it seems that at web environment, when we want to load the file inside the jar, we could only use getResourceAsStream method
        // otherwise, error occurs.
//        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("phantomjs/rasterize.js");
//        FileUtils.copyInputStreamToFile(is, new File(getLongScriptName("phantomjs/rasterize.js")) );

        // JAVA8 consumer way
        URL url = Thread.currentThread().getContextClassLoader().getResource("phantomjs/rasterize.js");

        assert url != null;

        logger.debug(" @PostConstruct method in TempDir called to copy all valid js file to tmp dir ...");

        FileUtils2.processResource(url.toURI(), path -> Files.list(path.getParent())
                .filter(p -> p.toString().endsWith(".js"))
                .forEach(p -> {
                    try {
                        logger.debug("Copying " + p.getFileName() + " to " + TempDir.getPhantomJsDir());
                        Path jsPath = Paths.get(TempDir.getPhantomJsDir().toString(),  p.getFileName().toString());
                        Files.copy(p, jsPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ioex) {
                        logger.error("Error while setting up phantomjs environment: " + ioex.getMessage());
                        ioex.printStackTrace();
                    }
                }));

    }

    /**
     *  spring way of handling the copying of jar file to the dest dir.
     */
    @PostConstruct
    private void copyResources2() {
        logger.debug(" @PostConstruct method in TempDir called to copy all valid js file to tmp dir ...");
        URL u = getClass().getProtectionDomain().getCodeSource().getLocation();
        URLClassLoader jarLoader = new URLClassLoader(new URL[]{u}, Thread.currentThread().getContextClassLoader());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(jarLoader);
        try {
            Resource[] resources = resolver.getResources("classpath*:/phantomjs/*.js*");
            logger.debug(String.format(" found %d js files at /phantomjs directory", resources.length));
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

    @PreDestroy
    public void destroy() throws Exception {
        logger.debug(" deleting all temp file in " + tmpDir.toString());
        FileUtils.deleteQuietly(tmpDir.toFile());
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

}
