package com.nexa.audit.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;

import java.util.UUID;

@Aspect
@Component
public class CorrelationIdAspect {
    private static final String HDR = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    // any public controller method
    @Before("within(com.nexa.audit.controller..*) && execution(public * *(..))")
    public void addCorrelationId(JoinPoint jp) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;
        HttpServletRequest req = attrs.getRequest();
        String cid = req.getHeader(HDR);
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, cid);
    }

    @After("within(com.nexa.audit.controller..*) && execution(public * *(..))")
    public void clear() {
        MDC.remove(MDC_KEY);
    }
}
