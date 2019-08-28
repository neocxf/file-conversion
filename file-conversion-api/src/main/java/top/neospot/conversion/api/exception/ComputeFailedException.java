package top.neospot.conversion.api.exception;

import top.neospot.conversion.api.support.Computable;

/**
 *  used with {@link Computable}, when the future call failed,
 *  wrap the {@link java.util.concurrent.ExecutionException} as RuntimeException
 * @author neo.fei {neocxf@gmail.com}
 */
public class ComputeFailedException extends RuntimeException {

    public ComputeFailedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
