package technbolts.core.infrastructure;

import java.sql.SQLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RollbackException extends RuntimeException {
    public RollbackException(Throwable cause) {
        super(cause);
    }
}
