package com.example.demo.controller;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@RequestMapping("users")

public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDto dto) {
        try{
            return new ResponseEntity<>(service.create(dto), HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
