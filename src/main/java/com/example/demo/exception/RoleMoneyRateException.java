package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleMoneyRateException extends Exception{
    public RoleMoneyRateException(String russian, String english, String code, Long number) {
        super(english);
        log.error("An error {}-{} occurred: {}", code, number, english);
    }
}
