package com.example.urlshortener.exception;

public class ShortCodeCollisionException extends RuntimeException {

    public ShortCodeCollisionException() {
        super("Unable to generate unique code after 10 attempts");
    }

    public ShortCodeCollisionException(String message) {
        super(message);
    }
}
