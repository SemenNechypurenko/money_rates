package com.sheduler;

import com.model.Coin;
import com.model.Subscription;
import com.service.CoinService;
import com.service.EmailService;
import com.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ClientNotifyingScheduler {
    private final CoinService coinService;
    private final SubscriptionService subscriptionService;
    private final EmailService emailService;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    // subscription check occurs every 30 seconds
    @Scheduled(cron = "*/30 * * * * *")
    public void checkSubscriptions() {
        // all subscriptions
        Set<Subscription> subscriptions = new HashSet<>(subscriptionService.list());
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
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
                        "and the rate currently have %s on %s percents since %s .";
                if ((lastCoin.getPrice() - firstCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {
                    log.info("{} rate increased on {} percents", subscription.getCurrency(),
                            (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100);

                    delta = (lastCoin.getPrice() - firstCoin.getPrice()) / firstCoin.getPrice() * 100;

                    message = String.format(message, subscription.getUser().getName(), subscription.getCurrency(),
                            "increased", decimalFormat.format(delta),
                            DateUtils.truncate(subscription.getDateOfLastEmail(), Calendar.MINUTE));

                    emailService.sendEmail(subscription.getUser().getEmail(),
                            "Money rate has been changed", message);

                    subscription.setDateOfLastEmail(new Date());
                }
                if ((firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100
                        >= subscription.getDelta()) {

                    delta = (firstCoin.getPrice() - lastCoin.getPrice())/firstCoin.getPrice() * 100;

                    log.info("{} rate reduced on {} percents", subscription.getCurrency(), decimalFormat.format(delta));

                    message = String.format(message, subscription.getUser().getName(), subscription.getCurrency(),
                            "reduced", decimalFormat.format(delta),
                            DateUtils.truncate(subscription.getDateOfLastEmail(), Calendar.MINUTE));

                    emailService.sendEmail(subscription.getUser().getEmail(), "Money rate has been changed",
                            message);

                    subscription.setDateOfLastEmail(new Date());
                }
                subscriptionService.update(subscription);
            }
        }
    }
}
