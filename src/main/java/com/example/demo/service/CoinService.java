package com.example.demo.service;

import com.example.demo.model.Coin;
import com.example.demo.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    public List<Coin> list(Date before, Date after) {
        if (Objects.isNull(before) && Objects.isNull(after)) {
            return repository.findAll();
        }
        if (Objects.nonNull(after) && Objects.isNull(before)) {
            return repository.findCoinByDateAfter(after);
        }
        if (Objects.isNull(after)) {
            return repository.findCoinByDateBefore(before);
        }
        return repository.findCoinByDateBetween(after, before);
    }
}
