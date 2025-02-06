package com.example.onlinestorespringboot.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
