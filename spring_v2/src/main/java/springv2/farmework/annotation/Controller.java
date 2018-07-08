package springv2.farmework.annotation;

import java.lang.annotation.*;

/**
 * @author ccy 
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
