package technbolts.compta.price;

import com.google.common.collect.Lists;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import technbolts.compta.price.view.CatalogEntryView;
import technbolts.compta.price.view.CatalogView;
import technbolts.compta.price.view.CatalogViews;
import technbolts.compta.price.view.CatalogViewsUpdater;
import technbolts.core.infrastructure.*;
import technbolts.core.infrastructure.support.JdbcConnectionPools;
import technbolts.core.infrastructure.support.JdbcEventStore;
import technbolts.core.infrastructure.support.JdbcStoreUpdate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogUsecasesTest {

    private List<Disposable> disposables = Lists.newArrayList();
    private DefaultEventBus<VersionedDomainEvent> eventBus;
    private EventStore eventStore;
    private CatalogViews catalogViews;

    @Before
    public void setUp() {
        JdbcConnectionPool dataSource = JdbcConnectionPools.acquire();
        disposables.add(JdbcConnectionPools.toDisposable(dataSource));
        eventStore = new JdbcEventStore(dataSource);
        new JdbcStoreUpdate("sql/h2", dataSource).migrate();

        catalogViews = new CatalogViews(dataSource);
        CatalogViewsUpdater updater = new CatalogViewsUpdater(dataSource);
        eventBus = new DefaultEventBus<VersionedDomainEvent>();
        eventBus.addListener(updater.asListener());
    }

    private UnitOfWork newUnitOfWork() {
        return new UnitOfWork(eventBus, eventStore);
    }

    @After
    public void tearDown() {
        for(Disposable disposable: disposables)
            disposable.dispose();
    }

    @Test
    public void create_entries_and_list_them() throws SQLException, DataAccessException {
        UnitOfWork uow = newUnitOfWork();
        Catalog catalog = Catalog.create(uow, "Drinking", eventStore);
        catalog.registerNewEntry("Leffe Blond", new BigDecimal("5.5"));
        catalog.registerNewEntry("Leffe Triple", new BigDecimal("6.5"));
        catalog.registerNewEntry("Coreff", new BigDecimal("5.5"));
        catalog.registerNewEntry("Desperados", new BigDecimal("5"));
        catalog.registerNewEntry("1664", new BigDecimal("4.5"));
        catalog.registerNewEntry("1664 Blanche", new BigDecimal("5.0"));
        catalog.registerNewEntry("Grimbergen", new BigDecimal("5.5"));
        catalog.registerNewEntry("Pelforth", new BigDecimal("3.5"));
        catalog.registerNewEntry("33 Export", new BigDecimal("3.5"));
        uow.commit();

        Id catalogId = catalog.entityId();

        // Event store Part
        uow = newUnitOfWork();
        Stream<VersionedDomainEvent> stream = eventStore.openStream(catalogId);
        catalog = Catalog.loadFromHistory(uow, eventStore, stream);
        assertThat(catalog.entryIds()).hasSize(9);

        // View Part
        List<CatalogView> catalogs = catalogViews.findCatalogsByLabel("Drinking");
        assertThat(catalogs).contains(new CatalogView(catalogId, 10, "Drinking"));

        List<CatalogEntryView> entryViews = catalogViews.getCatalogEntriesForCatalog(catalogId);
        assertThat(entryViews).hasSize(9);
    }
}
