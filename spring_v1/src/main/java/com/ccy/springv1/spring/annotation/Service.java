package com.ccy.springv1.spring.annotation;

import java.lang.annotation.*;

/**
 * @author ccy
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default  "";
}
