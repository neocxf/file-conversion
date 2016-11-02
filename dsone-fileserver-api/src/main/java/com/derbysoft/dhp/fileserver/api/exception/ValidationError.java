package com.derbysoft.dhp.fileserver.api.exception;

/**
 *  域验证错误
 * @author Xiaofei Chen <a href="mailto:neocxf@qq.com">Email the author</a>
 * @version 1.0 1/9/2016
 */
public class ValidationError {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
