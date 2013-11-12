package technbolts.core.infrastructure.support;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface JdbcRunnable<R> {
    R execute(Connection connection, JdbcDisposables disposables) throws SQLException;
}
