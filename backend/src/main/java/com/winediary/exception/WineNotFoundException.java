package com.winediary.exception;

public class WineNotFoundException extends RuntimeException {
    public WineNotFoundException(String message) {
        super(message);
    }

    public WineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}