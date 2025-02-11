package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.exception.*;
import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
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
        return new ResponseEntity<>(new AppErrorDto(entityNotFoundException.getMessage(),404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongIndexException.class)
    public ResponseEntity<AppErrorDto> handleWrongIndexException(WrongIndexException wrongIndexException) {
        return new ResponseEntity<>(new AppErrorDto(wrongIndexException.getMessage(),404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<AppErrorDto> handleRegistrationFailedException(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(new AppErrorDto(registrationFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<AppErrorDto> handleLoginFailedException(LoginFailedException loginFailedException) {
        return new ResponseEntity<>(new AppErrorDto(loginFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoQuantityProductException.class)
    public ResponseEntity<AppErrorDto> handleNoQuantityProductException(NoQuantityProductException noQuantityProductException) {
        return new ResponseEntity<>(new AppErrorDto(noQuantityProductException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExceedingQuantityException.class)
    public ResponseEntity<AppErrorDto> handleExceedingQuantityException(ExceedingQuantityException exceedingQuantityException) {
        return new ResponseEntity<>(new AppErrorDto(exceedingQuantityException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUpdatingException.class)
    public ResponseEntity<AppErrorDto> handleUserUpdatingException(UserUpdatingException userUpdatingException) {
        return new ResponseEntity<>(new AppErrorDto(userUpdatingException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<AppErrorDto> handleRefreshTokenException(RefreshTokenException refreshTokenException) {
        return new ResponseEntity<>(new AppErrorDto(refreshTokenException.getMessage(),403), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JsonSyntaxException.class)
    public ResponseEntity<AppErrorDto> handleJsonSyntaxException(JsonSyntaxException jsonSyntaxException) {
        return new ResponseEntity<>(new AppErrorDto(jsonSyntaxException.getMessage(),400), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(FileSavingException.class)
    public ResponseEntity<AppErrorDto> handleFileSavingException(FileSavingException fileSavingException) {
        return new ResponseEntity<>(new AppErrorDto(fileSavingException.getMessage(),400), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<AppErrorDto> handlePaymentFailedException(PaymentFailedException paymentFailedException) {
        return new ResponseEntity<>(new AppErrorDto(paymentFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<AppErrorDto> handleEmptyCartException(EmptyCartException emptyCartException) {
        return new ResponseEntity<>(new AppErrorDto(emptyCartException.getMessage(),400), HttpStatus.BAD_REQUEST);

    }

}
