package com.myapp.parking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.myapp.parking.common.AppResponse;
import com.myapp.parking.common.MyServiceMessage;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<AppResponse<Object>> handleCustomException(CustomException ex) {

        log.warn("Business Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                AppResponse.createBadRequestMessage(
                        MyServiceMessage.BAD_REQUEST,
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppResponse<Object>> handleAccessDenied(Exception ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                AppResponse.createBadRequestMessage(
                        new MyServiceMessage("403", "Forbidden"),
                        "Access denied"
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Object>> handleGeneric(Exception ex) {

        log.error("Unhandled Exception:", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                AppResponse.createServerErrorMessage(
                        MyServiceMessage.INTERNAL_SERVER_ERROR,
                        "Something went wrong"
                )
        );
    }
}