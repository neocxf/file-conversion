package top.neospot.conversion.core.exception;

import top.neospot.conversion.api.exception.ComputeFailedException;
import top.neospot.conversion.core.server.PhantomjsClient.FileConverterKey;

/**
 *  the phantomjs render has failed for some reason
 *      the reason includes:
 *          1. the page doesn't exist
 *          2. the url need to authenticate
 *          3. ..etc
 * @author neo.fei {neocxf@gmail.com}
 * @version 3/16/17
 */
public class PhantomRenderException extends ComputeFailedException {
    private FileConverterKey converterKey;

    public PhantomRenderException(String message, FileConverterKey converterKey) {
        super(message);
        this.converterKey = converterKey;
    }

    public FileConverterKey getConverterKey() {
        return converterKey;
    }
}

