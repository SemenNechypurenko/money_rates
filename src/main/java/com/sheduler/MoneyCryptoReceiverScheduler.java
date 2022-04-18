package com.sheduler;

import com.service.MoneyRatesService;
import com.model.Coin;
import com.service.CoinService;
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
        log.info("Saved fallowing coin rates: {}", coinService.createFromList(coinRates));
    }
}
