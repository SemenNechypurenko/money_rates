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
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ClientNotifyingScheduler {
    private final CoinService coinService;
    private final SubscriptionService subscriptionService;

    // subscription check occurs every 30 seconds
    @Scheduled(cron = "*/30 * * * * *")
    public void checkSubscriptions() {
        // all subscriptions
        Set<Subscription> subscriptions = new HashSet<>(subscriptionService.list());
        for (var subscription: subscriptions) {
            // get rate time frame
            List<Coin> coinsPerPeriodOfTime = coinService.list(subscription.getDateOfLastEmail(), new Date())
                    .stream().filter(coin -> coin.getTitle().equals(subscription.getCurrency()))
                    .collect(Collectors.toList());
            if (coinsPerPeriodOfTime.size() > 1) {
                Coin firstCoin = coinsPerPeriodOfTime.get(0);
                Coin lastCoin = coinsPerPeriodOfTime.get(coinsPerPeriodOfTime.size() - 1);
                User user = subscription.getUser();
                if ((lastCoin.getPrice() - firstCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {
                    log.warn("{} rate increased on {} percents", subscription.getCurrency(),
                            (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100);
                    subscription.setDateOfLastEmail(new Date());
                }
                if ((firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {
                    log.warn("{} rate reduced on {} percents", subscription.getCurrency(),
                            (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100);
                    subscription.setDateOfLastEmail(new Date());
                }
                subscriptionService.update(subscription);
            }
        }
    }
}
