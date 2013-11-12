package technbolts.compta.price.view;

import com.google.common.collect.Lists;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import technbolts.compta.price.CatalogCreatedEvent;
import technbolts.core.infrastructure.DataAccessException;
import technbolts.core.infrastructure.Disposable;
import technbolts.core.infrastructure.Id;
import technbolts.core.infrastructure.VersionedDomainEvent;
import technbolts.core.infrastructure.support.JdbcConnectionPools;
import technbolts.core.infrastructure.support.JdbcStoreUpdate;

import java.sql.SQLException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogViewsTest {

    private List<Disposable> disposables = Lists.newArrayList();
    private JdbcConnectionPool dataSource;
    private CatalogViews catalogViews;

    @Before
    public void setUp() throws Exception {
        dataSource = JdbcConnectionPools.acquire();
        disposables.add(JdbcConnectionPools.toDisposable(dataSource));
        new JdbcStoreUpdate("sql/h2", dataSource).migrate();
        catalogViews = new CatalogViews(dataSource);
    }

    @After
    public void tearDown() {
        for (Disposable disposable : disposables)
            disposable.dispose();
    }

    @Test
    public void notifyEvent__catalogCreatedEvent() throws DataAccessException {
        CatalogCreatedEvent event = new CatalogCreatedEvent(Id.create("ct"), "Price catalog");
        catalogViews.asListener().notifyEvent(new VersionedDomainEvent(event, 1, System.currentTimeMillis()));

        List<CatalogView> views = catalogViews.findCatalogsByLabel("Price*");
        assertThat(views).isNotNull()
                .contains(new CatalogView(Id.create("ct"), 1, "Price catalog"))
                .hasSize(1);
    }

}
