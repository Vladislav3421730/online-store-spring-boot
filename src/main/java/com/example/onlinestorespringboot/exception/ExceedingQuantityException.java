package com.example.onlinestorespringboot.exception;

public class ExceedingQuantityException extends RuntimeException {
    public ExceedingQuantityException(String message) {
        super(message);
    }
}
