package com.csit321g5.learnsprint.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * One place that turns exceptions into clean JSON answers, so no controller
 * needs its own try/catch and the client never receives a stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** The id in the URL (or a linked id in the body) does not exist. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** A required value is missing. */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** The request body could not be read as JSON. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "The request body is not valid JSON.");
    }

    /** The database refused the change: a duplicate value or a row that is still in use. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException ex) {
        return error(HttpStatus.CONFLICT,
                "The database refused this change. A value that must be unique is already used, "
                        + "or this record is still linked to other records.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return new ResponseEntity<>(Map.of("error", message), status);
    }
}
