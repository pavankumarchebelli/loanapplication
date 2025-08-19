// com/nexa/audit/aop/ControllerLoggingAspect.java
package com.nexa.audit.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    // all public methods in controller pkg
    @Around("within(com.nexa.audit.controller..*) && execution(public * *(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long t0 = System.nanoTime();
        String sig = pjp.getSignature().toShortString();
        log.info("REQ -> {}", sig);
        try {
            Object out = pjp.proceed();
            long tookMs = (System.nanoTime() - t0)/1_000_000;
            log.info("RES <- {} ({} ms)", sig, tookMs);
            return out;
        } catch (Throwable ex) {
            long tookMs = (System.nanoTime() - t0)/1_000_000;
            log.error("ERR !! {} ({} ms): {}", sig, tookMs, ex.getMessage(), ex);
            throw ex;
        }
    }
}
