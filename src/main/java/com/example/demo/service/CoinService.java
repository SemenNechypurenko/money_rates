package com.example.demo.service;

import com.example.demo.coinCryptoMoneyRateClient.MoneyRatesService;
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
    private final MoneyRatesService moneyRatesService;

    @Transactional
    public Coin create(Coin coin) {
        return repository.save(coin);
    }

    public List<Coin> list(Date before, Date after) {
        // тестовое сохранение курса битка
        // сделать шедулер
        repository.saveAll(moneyRatesService.getCoinRates());

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
