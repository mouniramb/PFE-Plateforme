package com.formation.exception;

public class FormateurNonResponsableException extends RuntimeException {
    public FormateurNonResponsableException(String message) {
        super(message);
    }

    public FormateurNonResponsableException(String message, Throwable cause) {
        super(message, cause);
    }
}
