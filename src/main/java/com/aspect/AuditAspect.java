package com.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class AuditAspect {

    @Around(value = "@annotation(com.aspect.annotations.LoggingRest)")
    public void auditMethod(ProceedingJoinPoint proceedingJoinPoint) {
        var method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        log.info("----------ASPECT METHOD IS CALLED------------");
    }
}
