package technbolts.compta.price.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technbolts.compta.price.CatalogCreatedEvent;
import technbolts.compta.price.CatalogEvent;
import technbolts.compta.price.CatalogOnEntryAddedEvent;
import technbolts.core.infrastructure.DataAccessException;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.Listener;
import technbolts.core.infrastructure.VersionedDomainEvent;
import technbolts.core.infrastructure.support.JdbcDisposables;
import technbolts.core.infrastructure.support.JdbcExecutor;
import technbolts.core.infrastructure.support.JdbcRunnable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogViewsUpdater {

    private Logger logger = LoggerFactory.getLogger(CatalogViewsUpdater.class);

    private final DataSource dataSource;
    private final JdbcExecutor executor;

    public CatalogViewsUpdater(DataSource dataSource) {
        this.dataSource = dataSource;
        this.executor = new JdbcExecutor(dataSource);
    }


    private void onEvent(final VersionedDomainEvent vEvent, final CatalogCreatedEvent event) throws DataAccessException {
        executor.executeWithinTransaction(new JdbcRunnable<Void>() {
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
        executor.executeWithinTransaction(new JdbcRunnable<Void>() {
            @Override
            public Void execute(Connection connection, JdbcDisposables disposables) throws SQLException {
                String sql = "UPDATE catalog_views SET version = ? WHERE catalog_id = ?";
                PreparedStatement pStmt = disposables.push(connection.prepareStatement(sql));
                pStmt.setLong(1, vEvent.version());
                pStmt.setString(2, event.entityId().asString());
                int nbRow = pStmt.executeUpdate();
                if (nbRow != 1)
                    throw new SQLException(
                            String.format("Unable to update catalog with id %s (nb rows %d)", event.entityId(), nbRow));
                return null;
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
