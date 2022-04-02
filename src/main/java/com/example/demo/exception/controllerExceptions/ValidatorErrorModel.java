package com.example.demo.exception.controllerExceptions;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidatorErrorModel {
    private List<ErrorModel> errors = new ArrayList<>();

    public List<ErrorModel> addFieldError(String field, String message) {
        errors.add(new ErrorModel(field, message));
        return errors;
    }
}
