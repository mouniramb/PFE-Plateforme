package com.formation.exception;

public class PresenceNotFoundException extends RuntimeException {
    public PresenceNotFoundException(String message) {
        super(message);
    }

    public PresenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
