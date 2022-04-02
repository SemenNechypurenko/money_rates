package com.example.demo.exception.controllerExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException (MethodArgumentNotValidException methodArgumentNotValidException) {
        ValidatorErrorModel validatorErrorModel = new ValidatorErrorModel();
        methodArgumentNotValidException.getFieldErrors().forEach(
                error -> validatorErrorModel.addFieldError(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(validatorErrorModel, HttpStatus.BAD_REQUEST);
    }
}
