package com.ccy.springv1.spring.annotation;

import java.lang.annotation.*;

/**
 * @author ccy
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default  "";

}
