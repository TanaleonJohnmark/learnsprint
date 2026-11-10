package com.csit321g5.learnsprint.exception;

/** Thrown when an id does not exist. The handler turns it into HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " with id " + id + " was not found.");
    }
}
