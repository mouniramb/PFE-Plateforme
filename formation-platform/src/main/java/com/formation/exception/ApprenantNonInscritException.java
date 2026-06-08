package com.formation.exception;

public class ApprenantNonInscritException extends RuntimeException {
    public ApprenantNonInscritException(String message) {
        super(message);
    }

    public ApprenantNonInscritException(String message, Throwable cause) {
        super(message, cause);
    }
}
