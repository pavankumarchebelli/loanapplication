// com/nexa/audit/aop/TimingAspect.java
package com.nexa.audit.aop;

import com.nexa.audit.aop.annotations.TimedOp;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimingAspect {
    private static final Logger log = LoggerFactory.getLogger(TimingAspect.class);

    // Either target by annotation...
    @Around("@annotation(timedOp)")
    public Object timeAnnotated(ProceedingJoinPoint pjp, TimedOp timedOp) throws Throwable {
        long t0 = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long tookMs = (System.nanoTime() - t0)/1_000_000;
            String name = timedOp.value().isBlank() ? pjp.getSignature().toShortString() : timedOp.value();
            log.info("TIMED [{}] {} ms", name, tookMs);
            // If you use Micrometer, replace log with a Timer.record(...)
        }
    }

    // ...or target all service methods:
    @Around("within(com.nexa.audit.service..*) && execution(public * *(..))")
    public Object timeService(ProceedingJoinPoint pjp) throws Throwable {
        long t0 = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long tookMs = (System.nanoTime() - t0)/1_000_000;
            log.debug("svc {} took {} ms", pjp.getSignature().toShortString(), tookMs);
        }
    }
}
