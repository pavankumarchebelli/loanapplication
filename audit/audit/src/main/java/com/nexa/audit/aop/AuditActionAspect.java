// com/nexa/audit/aop/AuditActionAspect.java
package com.nexa.audit.aop;

import com.nexa.audit.aop.annotations.AuditAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditActionAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditActionAspect.class);

    @Value("${app.env:dev}")
    private String env;

    // Before executing any method annotated with @AuditAction
    @Before("@annotation(auditAction)")
    public void beforeAction(JoinPoint jp, AuditAction auditAction) {
        log.info("AUDIT_ACTION start={} method={}", auditAction.value(), jp.getSignature().toShortString());
        // Example policy: block DELETE in prod
        if ("prod".equalsIgnoreCase(env) && "DELETE".equalsIgnoreCase(auditAction.value())) {
            throw new IllegalStateException("Delete operation is disabled in prod");
        }
    }

    @AfterReturning(pointcut = "@annotation(auditAction)", returning = "result")
    public void afterAction(JoinPoint jp, AuditAction auditAction, Object result) {
        log.info("AUDIT_ACTION success={} method={}", auditAction.value(), jp.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "@annotation(auditAction)", throwing = "ex")
    public void afterThrow(JoinPoint jp, AuditAction auditAction, Throwable ex) {
        log.warn("AUDIT_ACTION failed={} method={} msg={}",
                auditAction.value(), jp.getSignature().toShortString(), ex.getMessage());
    }
}
