package com.example.demo.service;

import com.example.demo.model.Coin;
import com.example.demo.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository repository;

    @Transactional
    public Coin create(Coin coin) {
        return repository.save(coin);
    }

    @Transactional
    public List<Coin> createFromList(List<Coin> coins) {
        return repository.saveAll(coins);
    }

    public List<Coin> list(Date after, Date before) {
        List<Coin> coins;
        if (Objects.isNull(before) && Objects.isNull(after)) {
            coins = repository.findAll();
        } else if (Objects.nonNull(after) && Objects.isNull(before)) {
            coins = repository.findCoinByDateAfter(after);
        } else if (Objects.isNull(after)) {
            coins = repository.findCoinByDateBefore(before);
        } else {
            coins = repository.findCoinByDateBetween(after, before);
        }
        return coins.stream().sorted(Comparator.comparing(Coin::getDate)).collect(Collectors.toList());
    }
}
