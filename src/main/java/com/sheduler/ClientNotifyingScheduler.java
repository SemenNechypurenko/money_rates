package com.sheduler;

import com.model.Coin;
import com.model.Subscription;
import com.service.CoinService;
import com.service.EmailService;
import com.service.SubscriptionService;
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
    private final EmailService emailService;

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
                double delta;
                String message = "Dear %s You are subscribed on crypto %s change, " +
                        "and the rate currently have %s on %s percents for the period since " +
                        "%s till %s.";
                if ((lastCoin.getPrice() - firstCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {
                    log.info("{} rate increased on {} percents", subscription.getCurrency(),
                            (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100);

                    delta = (lastCoin.getPrice() - firstCoin.getPrice()) / firstCoin.getPrice() * 100;

                    message = String.format(message, subscription.getUser().getName(), subscription.getCurrency(),
                            "increased", delta, subscription.getDateOfLastEmail(), new Date());

                    emailService.sendEmail(subscription.getUser().getEmail(),
                            "Money rate has been changed", message);

                    subscription.setDateOfLastEmail(new Date());
                }
                if ((firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {
                    log.info("{} rate reduced on {} percents", subscription.getCurrency(),
                            (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100);

                    delta = (firstCoin.getPrice() - lastCoin.getPrice()) / firstCoin.getPrice() * 100;

                    message = String.format(message, subscription.getUser().getName(), subscription.getCurrency(),
                            "reduced", delta, subscription.getDateOfLastEmail(), new Date());

                    emailService.sendEmail(subscription.getUser().getEmail(), "Money rate has been changed",
                            message);

                    subscription.setDateOfLastEmail(new Date());
                }
                subscriptionService.update(subscription);
            }
        }
    }
}
