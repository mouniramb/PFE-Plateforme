package com.formation.exception;

public class InscriptionAlreadyExistsException extends RuntimeException {
    public InscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
