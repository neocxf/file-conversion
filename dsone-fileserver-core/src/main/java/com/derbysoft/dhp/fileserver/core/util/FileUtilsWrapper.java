package com.derbysoft.dhp.fileserver.core.util;

import com.derbysoft.dhp.fileserver.core.exception.FileIOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *  wrapper for the commons.io utilities, and jdk utils
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class FileUtilsWrapper {
    /**
     *  store the file, and return the file name for latter phantomjs usage
     *  TODO: may be refined for the latter use
     * @param content
     * @param extension
     * @return
     * @throws IOException
     */
    public static String storeFile(String content, String extension) throws IOException {
        String fileName = createRandomFileName(extension);
        File tempFile = createTempFile(content, fileName, extension);
        return tempFile.getPath();
    }

    /**
     *  read the given file's content into string (Default the charset is utf-8)
     * @param file
     * @return
     * @throws IOException
     */
    public static String read(String file) throws IOException {
        return FileUtils.readFileToString(new File(file), Charset.forName("utf-8"));
    }

    /**
     *  create a given temporary file, with the given filename and extension
     * @param content
     * @param fileName
     * @param extension
     * @return returning file
     * @throws IOException
     */
    public static File createTempFile(String content, String fileName, String extension) throws IOException {
        String tempFilePath = TempDir.getOutputDir() + File.separator + fileName;
        File tempFile = new File(tempFilePath);
        FileUtils.writeStringToFile(  tempFile , content, Charset.forName("utf-8"));
        return tempFile;
    }

    /**
     *  generate a eight digits random file name with the given extension
     * @param extension
     * @return
     */
    public static String createRandomFileName(String extension) {
        return RandomStringUtils.randomAlphanumeric(8) + "." + extension;
    }

    public static ByteArrayOutputStream writeFileToStream(String filename) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            String tmpFile = TempDir.outputDir + String.valueOf(File.separatorChar) + filename;
            stream.write(FileUtils.readFileToByteArray(new File(tmpFile)));
        } catch (IOException ioex) {
            System.err.println("Tried to read file from filesystem: " + ioex.getMessage());
            throw new FileIOException("IOException: cannot find your file to download...");
        }

        return stream;
    }



}
