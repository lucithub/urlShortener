package com.example.urlshortener.exception;

public class DuplicateAliasException extends RuntimeException {

    public DuplicateAliasException(String alias) {
        super("Custom alias '" + alias + "' is already in use");
    }
}
