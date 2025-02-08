package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.exception.*;
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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<AppErrorDto> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(new AppErrorDto("Endpoint not found: " + ex.getRequestURL(),404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<AppErrorDto> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("Method not allowed: {}", ex.getMessage());
        return new ResponseEntity<>(new AppErrorDto(ex.getMessage(), 405), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
    )
    public ResponseEntity<AppErrorDto> handleException(Exception exception) {
        log.error("Unexpected error: {}", exception.getMessage());
        return new ResponseEntity<>(new AppErrorDto(exception.getMessage(),500), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
