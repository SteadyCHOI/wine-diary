package com.winediary.exception;

public class TastingNoteNotFoundException extends RuntimeException {
    public TastingNoteNotFoundException(String message) {
        super(message);
    }

    public TastingNoteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}