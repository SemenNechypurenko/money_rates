package com.example.demo.service;

import com.example.demo.coinCryptoMoneyRateClient.api.ApiCoingecko;
import com.example.demo.model.Coin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoneyRatesService {
    private final String USD = "usd";
    private final String BITCOIN = "bitcoin";
    private final ApiCoingecko apiCoingecko;

    public List<Coin> getCoinRates() {
        List<Coin> coins = new ArrayList<>();
        try {
            Coin coin = apiCoingecko.getRate(BITCOIN, USD);
            coins.add(coin);
        } catch (IOException e) {
            log.error("} catch (IOException e) {");
        }
        return coins;
    }
}
