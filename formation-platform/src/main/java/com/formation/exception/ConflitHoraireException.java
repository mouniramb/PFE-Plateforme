package com.formation.exception;

public class ConflitHoraireException extends RuntimeException {
    public ConflitHoraireException(String message) {
        super(message);
    }

    public ConflitHoraireException(String message, Throwable cause) {
        super(message, cause);
    }
}
