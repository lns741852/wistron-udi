package com.surgical.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalDeviceIdException extends RuntimeException{

    private static final long serialVersionUID = 380517957316903462L;

    public IllegalDeviceIdException(String message){
        super(message);
    }

    public IllegalDeviceIdException(String message, Throwable cause){
        super(message, cause);
    }
}
