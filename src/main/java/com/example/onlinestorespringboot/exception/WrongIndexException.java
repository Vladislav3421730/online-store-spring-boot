package com.example.onlinestorespringboot.exception;

public class WrongIndexException extends RuntimeException {
    public WrongIndexException(String message) {
        super(message);
    }
}
