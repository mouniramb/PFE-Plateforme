package com.formation.exception;

public class SalleNotFoundException extends RuntimeException {
    public SalleNotFoundException(String message) {
        super(message);
    }

    public SalleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
