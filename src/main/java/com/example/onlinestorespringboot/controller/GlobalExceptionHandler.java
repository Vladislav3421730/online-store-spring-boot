package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<AppErrorDto> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        return new ResponseEntity<>(new AppErrorDto(entityNotFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongIndexException.class)
    public ResponseEntity<AppErrorDto> handleWrongIndexException(WrongIndexException wrongIndexException) {
        return new ResponseEntity<>(new AppErrorDto(wrongIndexException.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<AppErrorDto> handleRegistrationFailedException(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(new AppErrorDto(registrationFailedException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<AppErrorDto> handleLoginFailedException(LoginFailedException loginFailedException) {
        return new ResponseEntity<>(new AppErrorDto(loginFailedException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoQuantityProductException.class)
    public ResponseEntity<AppErrorDto> handleNoQuantityProductException(NoQuantityProductException noQuantityProductException) {
        return new ResponseEntity<>(new AppErrorDto(noQuantityProductException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExceedingQuantityException.class)
    public ResponseEntity<AppErrorDto> handleExceedingQuantityException(ExceedingQuantityException exceedingQuantityException) {
        return new ResponseEntity<>(new AppErrorDto(exceedingQuantityException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUpdatingException.class)
    public ResponseEntity<AppErrorDto> handleUserUpdatingException(UserUpdatingException userUpdatingException) {
        return new ResponseEntity<>(new AppErrorDto(userUpdatingException.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
