package com.formation.exception;

public class PaiementNotFoundException extends RuntimeException {
    public PaiementNotFoundException(String message) {
        super(message);
    }
}
