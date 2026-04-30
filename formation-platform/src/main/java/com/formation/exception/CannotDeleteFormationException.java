package com.formation.exception;

public class CannotDeleteFormationException extends RuntimeException {
    public CannotDeleteFormationException(String message) {
        super(message);
    }
}
