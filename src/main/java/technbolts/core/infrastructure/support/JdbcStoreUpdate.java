package technbolts.core.infrastructure.support;

import com.googlecode.flyway.core.Flyway;

import javax.sql.DataSource;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcStoreUpdate {

    private final String migrationLocation;
    private final DataSource dataSource;

    public JdbcStoreUpdate(String migrationLocation, DataSource dataSource) {
        this.migrationLocation = migrationLocation;
        this.dataSource = dataSource;
    }

    public int migrate() {
        Flyway flyway = new Flyway();
        flyway.setLocations(migrationLocation);
        flyway.setDataSource(dataSource);
        return flyway.migrate();
    }

}
