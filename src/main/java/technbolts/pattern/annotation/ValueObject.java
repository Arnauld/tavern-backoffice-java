package technbolts.pattern.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a class is a value object.
 *
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface ValueObject {
}
