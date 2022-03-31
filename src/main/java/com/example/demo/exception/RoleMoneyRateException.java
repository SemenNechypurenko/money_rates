package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleMoneyRateException extends Exception{
    public RoleMoneyRateException(String s) {
        super(s);
        log.error(s);
    }
}
