package technbolts.core.infrastructure;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Command {
}
