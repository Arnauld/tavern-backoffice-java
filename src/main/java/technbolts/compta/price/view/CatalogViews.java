package technbolts.compta.price.view;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technbolts.compta.price.CatalogCreatedEvent;
import technbolts.compta.price.CatalogOnEntryAddedEvent;
import technbolts.compta.price.CatalogEvent;
import technbolts.core.infrastructure.*;
import technbolts.core.infrastructure.support.JdbcDisposables;
import technbolts.core.infrastructure.support.JdbcRunnable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogViews {

    private Logger logger = LoggerFactory.getLogger(CatalogViews.class);

    private final DataSource dataSource;

    public CatalogViews(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    private void onEvent(final VersionedDomainEvent vEvent, final CatalogCreatedEvent event) throws DataAccessException {
        executeWithinTransaction(new JdbcRunnable<Void>() {
            @Override
            public Void execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "INSERT INTO catalog_views (catalog_id, version, label) VALUES (?,?,?)";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setString(1, event.entityId().asString());
                pStmt.setLong(2, vEvent.version());
                pStmt.setString(3, event.getLabel());
                pStmt.executeUpdate();
                return null;
            }
        });
    }

    private void onEvent(final VersionedDomainEvent vEvent, final CatalogOnEntryAddedEvent event) throws DataAccessException {
        executeWithinTransaction(new JdbcRunnable<Void>() {
            @Override
            public Void execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "UPDATE catalog_views SET version = ? where catalog_id = ?";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setLong(1, vEvent.version());
                pStmt.setString(2, event.entityId().asString());
                int nbRow = pStmt.executeUpdate();
                if(nbRow != 1)
                    throw new SQLException(
                            String.format("Unable to update catalog with id %s (nb rows %d)", event.entityId(), nbRow));
                return null;
            }
        });
    }

    private <R> R executeWithinTransaction(JdbcRunnable<R> jdbcRunnable) throws DataAccessException {
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

    public List<CatalogView> findCatalogsByLabel(final String searchLabel) throws DataAccessException {
        return executeWithinTransaction(new JdbcRunnable<List<CatalogView>>() {
            @Override
            public List<CatalogView> execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "SELECT catalog_id, version, label FROM catalog_views WHERE label LIKE ?";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setString(1, searchLabel.replace("*", "%"));
                ResultSet resultSet = disposables.push(pStmt.executeQuery());

                List<CatalogView> views = Lists.newArrayList();
                while (resultSet.next()) {
                    Id id = Id.create(resultSet.getString(1));
                    long version = resultSet.getLong(2);
                    String label = resultSet.getString(3);
                    views.add(new CatalogView(id, version, label));
                }
                return views;
            }
        });
    }

    private void dispatch(VersionedDomainEvent vEvent) {
        DomainEvent event = vEvent.domainEvent();
        try {
            if (event instanceof CatalogCreatedEvent) {
                onEvent(vEvent, (CatalogCreatedEvent) event);
            } else if (event instanceof CatalogOnEntryAddedEvent) {
                onEvent(vEvent, (CatalogOnEntryAddedEvent) event);
            }
        } catch (DataAccessException e) {
            logger.error("Fail to handle event {}", vEvent, e);
        }
    }

    public Listener<VersionedDomainEvent> asListener() {
        return new Listener<VersionedDomainEvent>() {

            @Override
            public void notifyEvent(VersionedDomainEvent vEvent) {
                DomainEvent event = vEvent.domainEvent();
                if (!(event instanceof CatalogEvent)) {
                    return;
                }

                dispatch(vEvent);
            }
        };
    }
}
