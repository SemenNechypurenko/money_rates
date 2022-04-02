package com.example.demo.exception.controllerExceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorModel {
    String field;
    String message;
}
