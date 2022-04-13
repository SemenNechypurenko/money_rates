package com.example.demo.controller;

import com.example.demo.dto.request.SubscriptionRequestDto;
import com.example.demo.exception.UserMoneyRateException;
import com.example.demo.model.Subscription;
import com.example.demo.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SubscriptionRequestDto dto) {
        try{
            return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public List<Subscription> list() {
        return service.list();
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> get(@PathVariable("login") String login) {
        try {
            Set<Subscription> subscriptions = service.findByName(login);
            return new ResponseEntity<>(subscriptions, HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
