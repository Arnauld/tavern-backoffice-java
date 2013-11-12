package technbolts.compta.price;

import com.google.common.collect.Lists;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import technbolts.compta.price.view.CatalogView;
import technbolts.compta.price.view.CatalogViews;
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
    private JdbcConnectionPool dataSource;
    private EventStore eventStore;
    private CatalogViews catalogViews;

    @Before
    public void setUp() {
        dataSource = JdbcConnectionPools.acquire();
        disposables.add(JdbcConnectionPools.toDisposable(dataSource));
        eventStore = createDataStore(dataSource);
        new JdbcStoreUpdate("sql/h2", dataSource).migrate();

        catalogViews = new CatalogViews(dataSource);
        eventBus = new DefaultEventBus<VersionedDomainEvent>();
        eventBus.addListener(catalogViews.asListener());
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

        uow = newUnitOfWork();
        Stream<VersionedDomainEvent> stream = eventStore.openStream(catalogId);
        catalog = Catalog.loadFromHistory(uow, eventStore, stream);
        assertThat(catalog.entryIds()).hasSize(9);

        List<CatalogView> catalogs = catalogViews.findCatalogsByLabel("Drinking");
        assertThat(catalogs).contains(new CatalogView(catalog.entityId(), 10, "Drinking"));
    }

    private EventStore createDataStore(JdbcConnectionPool dataSource) {
        return new JdbcEventStore(dataSource);
    }
}