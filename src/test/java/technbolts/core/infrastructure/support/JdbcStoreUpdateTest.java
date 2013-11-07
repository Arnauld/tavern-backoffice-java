package technbolts.core.infrastructure.support;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcStoreUpdateTest {

    private JdbcConnectionPool dataSource;
    private JdbcDisposables disposables;

    @Before
    public void setUp() {
        dataSource = JdbcConnectionPools.acquire();
        disposables = new JdbcDisposables();
    }

    @After
    public void tearDown() {
        disposables.dispose();
        dataSource.dispose();
    }

    @Test
    public void migrate() throws SQLException {
        JdbcStoreUpdate storeUpdate = new JdbcStoreUpdate("sql/h2", dataSource);
        int nbMigrations = storeUpdate.migrate();
        assertThat(nbMigrations).isEqualTo(2);

        String sql = "SELECT value FROM metadata WHERE name = ?";
        Connection connection = disposables.push(dataSource.getConnection());
        PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
        pStmt.setString(1, "db.version");
        ResultSet resultSet = disposables.push(pStmt.executeQuery());
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getString(1)).isEqualTo("0.1.1");
    }

}
