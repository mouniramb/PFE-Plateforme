package com.formation.exception;

public class CannotDeleteSeanceException extends RuntimeException {
    public CannotDeleteSeanceException(String message) {
        super(message);
    }

    public CannotDeleteSeanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
