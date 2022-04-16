package com.example.demo.sheduler;

import com.example.demo.model.Coin;
import com.example.demo.model.Subscription;
import com.example.demo.model.User;
import com.example.demo.service.CoinService;
import com.example.demo.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ClientNotifyingScheduler {
    private final CoinService coinService;
    private final SubscriptionService subscriptionService;

    // проверка подписок каждые пол минуты
    @Scheduled(cron = "*/30 * * * * *")
    public void checkSubscriptions() {
        // all subscriptions
        Set<Subscription> subscriptions = new HashSet<>(subscriptionService.list());
        for (var subscription: subscriptions) {
            User user = subscription.getUser();
            // get rate time frame
            List<Coin> coinsPerPeriodOfTime = coinService.list(subscription.getDateOfLastEmail(), new Date());
        }
    }
}
