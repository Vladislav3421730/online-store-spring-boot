package com.example.onlinestorespringboot.exception;

public class ProductNotFoundException extends EntityNotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
