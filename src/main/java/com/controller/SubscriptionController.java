package com.controller;

import com.aspect.annotations.LoggingRest;
import com.dto.request.SubscriptionRequestDto;
import com.dto.response.SubscriptionResponseDto;
import com.exception.UserMoneyRateException;
import com.model.Subscription;
import com.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;
    private final ModelMapper mapper;

    @PostMapping
    @LoggingRest(executor = "USER", method = "CREATE", model = "SUBSCRIPTION")
    public ResponseEntity<?> create(@Valid @RequestBody SubscriptionRequestDto dto) {
        try{
            return new ResponseEntity<>(serialize(service.create(dto)), HttpStatus.CREATED);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<Subscription> subscriptions = service.list();
        if (CollectionUtils.isNotEmpty(subscriptions)) {
            return new ResponseEntity<>(
                subscriptions.stream().map(this::serialize).collect(Collectors.toSet()),
                HttpStatus.OK);
            }
        return new ResponseEntity<>(HttpEntity.EMPTY, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> getByLogin(@PathVariable("login") String login) {
        try {
            Set<Subscription> subscriptions = service.findByName(login);
            return new ResponseEntity<>(
                    subscriptions.stream()
                            .map(this::serialize)
                            .collect(Collectors.toSet())
                    , HttpStatus.OK);
        } catch (UserMoneyRateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private  SubscriptionResponseDto serialize(Subscription subscription) {
        return mapper.map(subscription, SubscriptionResponseDto.class);
    }
}
