package technbolts.core.infrastructure.support;

import technbolts.core.infrastructure.CommitException;
import technbolts.core.infrastructure.Commitable;
import technbolts.core.infrastructure.RollbackException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcCommitables {
    public static Commitable toCommitable(final Connection connection, final boolean closeAtEnd) {
        return new Commitable() {
            @Override
            public void commit() {
                try {
                    connection.commit();
                } catch (SQLException e) {
                    throw new CommitException(e);
                } finally {
                    if (closeAtEnd)
                        JdbcUtils.closeQuietly(connection);
                }
            }

            @Override
            public void rollback() {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    throw new RollbackException(e);
                } finally {
                    if (closeAtEnd)
                        JdbcUtils.closeQuietly(connection);
                }
            }
        };
    }
}
