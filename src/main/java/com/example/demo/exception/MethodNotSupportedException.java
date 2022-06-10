package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MethodNotSupportedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

	public MethodNotSupportedException(String message) {
        super(message);
    }
}
