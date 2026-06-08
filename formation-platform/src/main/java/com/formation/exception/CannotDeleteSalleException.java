package com.formation.exception;

public class CannotDeleteSalleException extends RuntimeException {
    public CannotDeleteSalleException(String message) {
        super(message);
    }

    public CannotDeleteSalleException(String message, Throwable cause) {
        super(message, cause);
    }
}
