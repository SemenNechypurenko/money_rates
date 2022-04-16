package com.example.demo.controller;

import com.example.demo.dto.request.UserRequestDto;
import com.example.demo.dto.request.UserUpdateRequestDto;
import com.example.demo.dto.response.UserResponseDto;
import com.example.demo.exception.RoleMoneyRateException;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")

public class UserController {

    private final UserService service;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDto dto) {
        try{
            return new ResponseEntity<>(serialize(service.create(dto)), HttpStatus.CREATED);
        } catch (UserMoneyRateException | RoleMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{login}")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateRequestDto dto,
                                    @PathVariable("login") String login) {
        try{
            return new ResponseEntity<>(serialize(service.update(dto, login)), HttpStatus.OK);
        } catch (UserMoneyRateException | RoleMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<User> users = service.list();
        if (CollectionUtils.isNotEmpty(users)) {
            return new ResponseEntity<>(users.stream().map(this::serialize).collect(Collectors.toSet())
                    , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpEntity.EMPTY, HttpStatus.NO_CONTENT);
    }
    @GetMapping("/username/{login}")
    public ResponseEntity<?> getByLogin(@PathVariable("login") String login) {
        try {
            User user = service.findByName(login);
            return new ResponseEntity<>(serialize(user), HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        try {
            User user = service.findById(id);
            return new ResponseEntity<>(serialize(user), HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private UserResponseDto serialize(User user) {
        return mapper.map(user, UserResponseDto.class);
    }
}
