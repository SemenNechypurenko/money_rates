package com.example.demo.repository;

import com.example.demo.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<Coin, String> {
    List<Coin> findCoinByDateAfter(Date after);
    List<Coin> findCoinByDateBefore(Date after);
    List<Coin> findCoinByDateBetween(Date after, Date before);

}
