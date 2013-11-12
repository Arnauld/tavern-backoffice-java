package technbolts.core.infrastructure.support;

import technbolts.core.infrastructure.DataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcExecutor {

    private final DataSource dataSource;

    public JdbcExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <R> R executeWithinTransaction(JdbcRunnable<R> jdbcRunnable) throws DataAccessException {
        JdbcDisposables disposables = new JdbcDisposables();
        try {
            Connection connection = disposables.push(dataSource.getConnection());
            return jdbcRunnable.execute(connection, disposables);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            disposables.dispose();
        }
    }
}
