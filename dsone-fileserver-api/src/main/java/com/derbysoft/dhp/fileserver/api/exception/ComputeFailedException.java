package com.derbysoft.dhp.fileserver.api.exception;

/**
 *  used with {@link com.derbysoft.dhp.fileserver.api.support.Computable}, when the future call failed,
 *  wrap the {@link java.util.concurrent.ExecutionException} as RuntimeException
 * @author neo.fei {neocxf@gmail.com}
 */
public class ComputeFailedException extends RuntimeException {

    public ComputeFailedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Compute call failed, the reason is: " +  super.getMessage();
    }
}
