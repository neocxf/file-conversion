package com.derbysoft.dhp.fileserver.core.util;

import com.derbysoft.dhp.fileserver.core.exception.FileIOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.time.LocalDate;

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
     * @param fileName the file's short name(with the extension)
     * @return
     * @throws IOException
     */
    public static String storeFile(String content, String fileName) throws IOException {
        File tempFile = createTempFile(content, fileName);
        return tempFile.getPath();
    }

    /**
     *  store the file, and return the file name for latter phantomjs usage
     *
     * @param content the content that gonna to be written to the file
     * @param rawName the file's short name(without extension)
     * @param extension the file's extension
     * @return
     * @throws IOException
     */
    public static String storeFile(String content, String rawName, String extension) throws IOException {
        File tempFile = createTempFile(content, rawName, extension);
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
     *  create a given temporary file, with the given raw filename and extension
     * @param content
     * @param rawName
     * @param extension
     * @return returning file
     * @throws IOException
     */
    public static File createTempFile(String content, String rawName, String extension) throws IOException {
        String tempFilePath = TempDir.getDownloadLink(rawName + "." + extension);
        File tempFile = new File(tempFilePath);
        FileUtils.writeStringToFile( tempFile , content, Charset.forName("utf-8"));
        return tempFile;
    }

    /**
     *  create a given temporary file, with the given filename
     * @param content
     * @param fileName
     * @return returning file
     * @throws IOException
     */
    public static File createTempFile(String content, String fileName) throws IOException {
        String tempFilePath = TempDir.getDownloadLink(fileName);
        File tempFile = new File(tempFilePath);
        FileUtils.writeStringToFile( tempFile , content, Charset.forName("utf-8"));
        return tempFile;
    }

    /**
     *  generate a eight digits random file name with the given extension
     * @return a 8 alpha-numberic character
     */
    public static String createRandomFileName() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public static String createPrefixDailyName(String prefix) {
        return prefix + "_" + DateUtils.getCurrentLocalDate("yyyyMMdd");
    }

    /**
     *  generate a eight digits random file name with the given extension
     * @param extension
     * @return
     */
    public static String createRandomFileNameWithExtension(String extension) {
        return RandomStringUtils.randomAlphanumeric(8) + "." + extension;
    }

    public static ByteArrayOutputStream writeFileToStream(String filename) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            String tmpFile = TempDir.getDownloadLink(filename);
            stream.write(FileUtils.readFileToByteArray(new File(tmpFile)));
        } catch (IOException ioex) {
            System.err.println("Tried to read file from filesystem: " + ioex.getMessage());
            throw new FileIOException("IOException: cannot find your file to download...");
        }

        return stream;
    }



}
