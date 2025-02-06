package com.example.onlinestorespringboot.exception;

public class ImageNotFoundException extends EntityNotFoundException {

    public ImageNotFoundException(String message) {
        super(message);
    }
}
