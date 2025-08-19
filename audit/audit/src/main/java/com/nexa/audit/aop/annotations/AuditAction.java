package com.nexa.audit.aop.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditAction {
    String value(); // e.g., "CREATE", "UPDATE", "DELETE"
}
