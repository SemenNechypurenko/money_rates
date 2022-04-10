package com.example.demo.controller;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.dto.request.UserUpdateRequestDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")

public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDto dto) {
        try{
            return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
        } catch (UserMoneyRateException | RoleMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{login}")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateRequestDto dto,
                                    @PathVariable("login") String login) {
        try{
            return new ResponseEntity<>(service.update(dto, login), HttpStatus.OK);
        } catch (UserMoneyRateException | RoleMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public List<User> list() {
        return service.list();
    }
    @GetMapping("/{login}")
    public ResponseEntity<?> get(@PathVariable("login") String login) {
        try {
            User user = service.findByName(login);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
