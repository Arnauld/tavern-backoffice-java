package technbolts.core.infrastructure.support;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import technbolts.core.infrastructure.*;
import technbolts.compta.invoice.InvoiceCreatedEvent;
import technbolts.compta.invoice.Item;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static technbolts.core.infrastructure.VersionedDomainEvent.versioned;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcEventStoreTest {


    private Id orderId;
    private Id referenceId;
    private List<Item> items;
    private JdbcConnectionPool dataSource;
    private JdbcEventStore eventStore;

    @Before
    public void setUp() {
        orderId = Id.Strategy.Uuid.next(null);
        referenceId = Id.create("aef");
        items = Arrays.asList(new Item(referenceId, 5, BigDecimal.TEN));
        dataSource = JdbcConnectionPools.acquire("jdbc:h2:~/.h2/test-ide");
        //
        new JdbcStoreUpdate("sql/h2", dataSource).migrate();
        //
        eventStore = new JdbcEventStore(dataSource);
    }

    @After
    public void tearDown() {
        dataSource.dispose();
    }

    @Test
    public void store_simple_case() {
        List<VersionedDomainEvent> versionedEvents = versioned(1,
                Arrays.<DomainEvent>asList(new InvoiceCreatedEvent(orderId, items)));

        Transaction tx = new DefaultTransaction();
        eventStore.store(tx, orderId, Streams.from(versionedEvents));
        tx.commit();
        //
        Stream<VersionedDomainEvent> stream = eventStore.openStream(orderId);
        assertThat(stream).isNotNull();

        List<VersionedDomainEvent> events = Streams.toList(stream);
        assertThat(events).isEqualTo(versionedEvents);
    }
}
