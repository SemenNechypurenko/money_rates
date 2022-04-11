package com.example.demo.coinCryptoMoneyRateClient.sheduler;

import com.example.demo.coinCryptoMoneyRateClient.MoneyRatesService;
import com.example.demo.model.Coin;
import com.example.demo.service.CoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MoneyCryptoReceiverScheduler {

    private final CoinService coinService;
    private final MoneyRatesService moneyRatesService;

    // Save money rates every 30 seconds
    @Scheduled(cron = "*/30 * * * * *")
    public void receiveCryptoRates() {
        List<Coin> coinRates = moneyRatesService.getCoinRates();
        coinService.createFromList(coinRates);
        log.info("Saved fallowing coin rates: {}", coinRates);
    }
}
