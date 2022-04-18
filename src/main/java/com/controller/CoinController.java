package com.controller;

import com.model.Coin;
import com.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/coins")
public class CoinController {
    private final CoinService service;

    @GetMapping
    public List<Coin> list(@RequestAttribute(required = false) Date before,
                           @RequestAttribute(required = false) Date after) {
        return service.list(before, after);
    }
}
