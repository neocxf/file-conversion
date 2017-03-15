package com.derbysoft.dhp.fileserver.boot.config;

import com.derbysoft.dhp.fileserver.api.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice(annotations = Controller.class)
class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Autowired
    private MessageSource messageSource;

    protected <E extends Exception> ResponseEntity<ErrorDetail> errorsResponseEntity (E e, HttpStatus statusCode, String  logref) {
        MediaType mediaType = MediaType.parseMediaType("application/vnd.errors");

        String errorMessage = StringUtils.hasText(e.getMessage()) ? e.getMessage() : e.getClass().getSimpleName();

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        errorDetail.setTitle(" Resource already existed ");
        errorDetail.setDetail(e.getMessage());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);

        return new ResponseEntity<ErrorDetail>(errorDetail, httpHeaders, statusCode);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<?> entityExistsExceptionHandler(EntityExistsException ex) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        errorDetail.setTitle(" Resource already existed ");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());


        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> entityNotFoundExceptionHandler(EntityNotFoundException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle(" Resource not found ");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());


        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> IllegalArgumentExceptionHandler(Throwable ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        // Populate errorDetail instance
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setTitle("Illegal argument founded");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        return new ResponseEntity<Object>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ComputeFailedException.class)
    public ResponseEntity<?> IllegalArgumentExceptionHandler(ComputeFailedException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        // Populate errorDetail instance
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setTitle("Compute failed for some reason, maybe try agin later ...");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        return new ResponseEntity<Object>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    /**
     *  用于统一处理 jsr validator 验证处理方式，结合 hibernate validator 使用
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        // Populate errorDetail instance
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("Validation Failed");
        errorDetail.setDetail("Input validation failed");
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        // Create ValidationError instances
        List<FieldError> fieldErrors =  ex.getBindingResult().getFieldErrors();
        for(FieldError fe : fieldErrors) {

            List<ValidationError> validationErrorList = errorDetail.getErrors().get(fe.getField());
            if(validationErrorList == null) {
                validationErrorList = new ArrayList<ValidationError>();
                errorDetail.getErrors().put(fe.getField(), validationErrorList);
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode(fe.getCode());
            validationError.setMessage(messageSource.getMessage(fe, null));
            validationErrorList.add(validationError);
        }

        return handleExceptionInternal(ex, errorDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(status.value());
        errorDetail.setTitle(" Message not readable ");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        ex.printStackTrace();

        return handleExceptionInternal(ex, errorDetail, headers, status, request);
    }


    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
        LOGGER.debug("Responding with a status of {}", status);
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }

    /**
     *  用于统一处理 jsr validator 验证处理方式，结合 hibernate validator 使用
     * @param ex
     * @param req
     * @return
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationError(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,  HttpServletRequest req) {
        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());

        String requestPath = (String) req.getAttribute("javax.servlet.error.request_uri");
        if (requestPath == null ) {
            requestPath = req.getRequestURI();
        }

        errorDetail.setTitle("Validation failed");
        errorDetail.setDetail("input validation failed");
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        // create ValidationError instance
        List<FieldError>fieldErrors=ex.getBindingResult().getFieldErrors();

        fieldErrors.stream().forEach(fieldError -> {
            ValidationError validationError = new ValidationError();
            validationError.setCode(fieldError.getCode());
//            validationError.setMessage(fieldError.getDefaultMessage());
            System.out.println("########## messageSource " + messageSource.getMessage(fieldError, null));
            validationError.setMessage(messageSource.getMessage(fieldError, null));
            errorDetail.getErrors().computeIfAbsent(fieldError.getField(), s -> new ArrayList<>()).add(validationError);
        });

        return new ResponseEntity<Object>(errorDetail, null, HttpStatus.BAD_REQUEST);
    }
}
