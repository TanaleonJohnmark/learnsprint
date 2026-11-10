package com.csit321g5.learnsprint.exception;

/** Thrown when a required value is missing. The handler turns it into HTTP 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
