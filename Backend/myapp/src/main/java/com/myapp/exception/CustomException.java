package com.myapp.exception;

/**
 * Custom exception for business logic errors
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomException(String message) {
        super(message);
    }
}