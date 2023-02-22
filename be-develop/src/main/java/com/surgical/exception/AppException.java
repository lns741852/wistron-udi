package com.surgical.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppException extends RuntimeException{

    private static final long serialVersionUID = 6750083709904326822L;

    public AppException(String message){
        super(message);
    }

    public AppException(String message, Throwable cause){
        super(message, cause);
    }
}
