package technbolts.compta.infrastructure.support;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcUtilsTest {

    @Test
    public void closeQuietly__connection__exception_is_ignored() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        doThrow(new SQLException()).when(connection).close();
        JdbcUtils.closeQuietly(connection);
        verify(connection).close();
    }

    @Test
    public void closeQuietly__connection__null_is_ignored() throws SQLException {
        JdbcUtils.closeQuietly((Connection) null);
    }

    @Test
    public void closeQuietly__statement__exception_is_ignored() throws SQLException {
        Statement statement = Mockito.mock(Statement.class);
        doThrow(new SQLException()).when(statement).close();
        JdbcUtils.closeQuietly(statement);
        verify(statement).close();
    }

    @Test
    public void closeQuietly__statement__null_is_ignored() throws SQLException {
        JdbcUtils.closeQuietly((Statement) null);
    }

    @Test
    public void closeQuietly__resultSet__exception_is_ignored() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        doThrow(new SQLException()).when(resultSet).close();
        JdbcUtils.closeQuietly(resultSet);
        verify(resultSet).close();
    }

    @Test
    public void closeQuietly__resultSet__null_is_ignored() throws SQLException {
        JdbcUtils.closeQuietly((ResultSet) null);
    }
}
