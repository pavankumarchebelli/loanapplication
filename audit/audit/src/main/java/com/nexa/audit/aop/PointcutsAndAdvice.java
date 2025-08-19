package com.nexa.audit.aop;

import com.nexa.audit.aop.annotations.AuditAction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PointcutsAndAdvice {

    private static final Logger log = LoggerFactory.getLogger(PointcutsAndAdvice.class);

    // -------------------- Pointcuts --------------------

    // by method signature
    @Pointcut("execution(* com.nexa.audit.service.LoanAuditLogService.create(..))")
    void createAuditLog(){}

    // by package (all controllers)
    @Pointcut("within(com.nexa.audit.controller..*)")
    void anyController(){}

    // by bean name pattern
    @Pointcut("bean(*Controller)")
    void anyControllerBean(){}

    // by annotation on method
    @Pointcut("@annotation(com.nexa.audit.aop.annotations.AuditAction)")
    void anyAuditAction(){}

    // -------------------- Advice using those pointcuts --------------------

    // Example: surround every public controller method for simple req/res logging
    @Around("anyController() && execution(public * *(..))")
    public Object logEveryControllerCall(ProceedingJoinPoint pjp) throws Throwable {
        String sig = pjp.getSignature().toShortString();
        long t0 = System.nanoTime();
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

    // Example: special logging around the specific create() method
    @Around("createAuditLog()")
    public Object aroundCreate(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("Around createAuditLog(): {}", pjp.getSignature().toShortString());
        return pjp.proceed();
    }

    // Example: react to methods annotated with @AuditAction
    @Before("anyAuditAction() && @annotation(auditAction)")
    public void beforeAuditAction(AuditAction auditAction) {
        log.info("AUDIT_ACTION start={}", auditAction.value());
    }

    @AfterReturning("anyAuditAction() && @annotation(auditAction)")
    public void afterAuditAction(AuditAction auditAction) {
        log.info("AUDIT_ACTION success={}", auditAction.value());
    }

    @AfterThrowing(pointcut = "anyAuditAction() && @annotation(auditAction)", throwing = "ex")
    public void afterAuditActionError(AuditAction auditAction, Throwable ex) {
        log.warn("AUDIT_ACTION failed={} msg={}", auditAction.value(), ex.getMessage());
    }
}
