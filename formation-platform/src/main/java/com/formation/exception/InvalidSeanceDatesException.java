package com.formation.exception;

public class InvalidSeanceDatesException extends RuntimeException {
    public InvalidSeanceDatesException(String message) {
        super(message);
    }

    public InvalidSeanceDatesException(String message, Throwable cause) {
        super(message, cause);
    }
}
