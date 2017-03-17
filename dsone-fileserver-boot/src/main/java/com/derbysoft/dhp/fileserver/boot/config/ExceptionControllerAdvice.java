package com.derbysoft.dhp.fileserver.boot.config;

import com.derbysoft.dhp.fileserver.api.exception.ComputeFailedException;
import com.derbysoft.dhp.fileserver.api.exception.ErrorDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice(annotations = Controller.class)
class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    /**
     *  handle the ExecutionException for the transformation
     * @param ex render exception
     */
    @ExceptionHandler(ComputeFailedException.class)
    public ResponseEntity<?>  handlePhantomRenderException(ComputeFailedException ex) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setTitle("Compute failed for some reason, maybe try again later ...");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        return new ResponseEntity<Object>(errorDetail, HttpStatus.BAD_REQUEST);
    }

}
