package com.example.onlinestorespringboot.exception;

public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
