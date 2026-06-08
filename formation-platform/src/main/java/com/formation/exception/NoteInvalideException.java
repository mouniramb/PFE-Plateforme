package com.formation.exception;

public class NoteInvalideException extends RuntimeException {
    public NoteInvalideException(String message) {
        super(message);
    }

    public NoteInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}
