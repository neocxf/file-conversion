package top.neospot.conversion.api.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;

/**
 *  wrapper for the commons.io utilities, and jdk utils
 *
 * @author neo.fei {neocxf@gmail.com}
 */
public class FileUtils2 {

    /**
     * interface, that provides the callback
     *
     * @param <T>
     */
    public interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    /**
     *  process the given resource, both file or jar
     *
     * @param uri given resource's uri
     * @param action the hook for handling the specific url
     * @throws IOException
     */
    public static void processResource(URI uri, IOConsumer<Path> action) throws IOException {
        try {
            Path p=Paths.get(uri);
            action.accept(p);
        }
        catch(FileSystemNotFoundException ex) {
            try(FileSystem fs = FileSystems.newFileSystem(
                    uri, Collections.<String,Object>emptyMap())) {
                Path p = fs.provider().getPath(uri);
                System.err.println(" catch FileSystemNotFoundException, with path p : " + p);
                action.accept(p);
            }
        }
    }

    /**
     *  get the Path of the given resourceName
     *  <strong>NOTE:</strong> the solution won't work for the file inside jar
     * @param resourceName the name of the given resource
     * @return Path of the given resource
     * @throws URISyntaxException
     */
    public static Path getPath(String resourceName) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resourceName).toURI());
    }

}
