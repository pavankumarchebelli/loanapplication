package com.nexa.audit.aop.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimedOp {
    String value() default ""; // optional operation name
}
