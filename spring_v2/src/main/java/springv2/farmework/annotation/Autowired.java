package springv2.farmework.annotation;

import java.lang.annotation.*;

/**
 * @author ccy
 */
@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}
