package com.myapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.myapp.common.AppResponse;
import com.myapp.common.MyServiceMessage;

/**
 * Production-level global exception handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Business exceptions
     */
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

    /**
     * Validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Validation Error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                AppResponse.createBadRequestMessage(
                        MyServiceMessage.BAD_REQUEST,
                        ex.getMessage()
                )
        );
    }

    /**
     * Not Found
     */
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<AppResponse<Object>> handleNotFound(Exception ex) {

        log.warn("Not Found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                AppResponse.createNotFoundMessage(
                        MyServiceMessage.NOT_FOUND,
                        ex.getMessage()
                )
        );
    }

    /**
     * 🔥 FIXED: Access Denied (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppResponse<Object>> handleAccessDenied(AccessDeniedException ex) {

        log.warn("Access Denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                AppResponse.createForbiddenMessage(
                        MyServiceMessage.FORBIDDEN,
                        "You are not authorized to access this resource"
                )
        );
    }

    /**
     * Unauthorized (401)
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<AppResponse<Object>> handleUnauthorized(Exception ex) {

        log.warn("Unauthorized: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                AppResponse.createBadRequestMessage(
                        MyServiceMessage.UNAUTHORIZED,
                        "Authentication required"
                )
        );
    }

    /**
     * Generic fallback
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Object>> handleGenericException(Exception ex) {

        log.error("Unhandled Exception: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                AppResponse.createServerErrorMessage(
                        MyServiceMessage.INTERNAL_SERVER_ERROR,
                        "Something went wrong. Please try again later."
                )
        );
    }
}