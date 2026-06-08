package com.formation.exception;

public class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(String message) {
        super(message);
    }

    public SeanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
