package com.repository;

import com.model.Subscription;
import com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Set<Subscription> findAllByUser(User user);
    Optional<Subscription> findByUserAndCurrency(User user, String currency);
}
