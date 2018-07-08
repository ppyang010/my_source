package springv2.farmework.annotation;

import java.lang.annotation.*;

/**
 * @author ccy
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default  "";
}
