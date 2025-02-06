package com.example.onlinestorespringboot.exception;

public class NoQuantityProductException extends RuntimeException {
    public NoQuantityProductException(String message) {
        super(message);
    }
}
