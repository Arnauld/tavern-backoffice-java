package technbolts.core.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import technbolts.compta.invoice.InvoiceCreatedEvent;
import technbolts.compta.invoice.Item;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static technbolts.core.infrastructure.VersionedDomainEvent.versioned;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class UnitOfWorkTest {

    private Id orderId;
    private List<Item> items;
    private DefaultEventBus<VersionedDomainEvent> eventBus;
    private UnitOfWork uow;
    private EventStore eventStore;

    @Before
    public void setUp() {
        orderId = Id.create("ab17f");
        Id referenceId = Id.create("aef");
        items = Arrays.asList(new Item(referenceId, 5, BigDecimal.TEN));
        eventBus = new DefaultEventBus<VersionedDomainEvent>();
        eventStore = Mockito.mock(EventStore.class);
        uow = new UnitOfWork(eventBus, eventStore);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void event_is_published_once_committed() {
        EventStore eventStore = mock(EventStore.class);
        Listener<VersionedDomainEvent> listener = mock(Listener.class);
        eventBus.addListener(listener);

        VersionedDomainEvent vEvent = versioned(1, new InvoiceCreatedEvent(orderId, items));
        uow.registerNew(vEvent);
        uow.registerEventStoreFor(orderId, eventStore);

        assertThat(uow.uncommittedEvents()).contains(vEvent);
        verifyZeroInteractions(listener);
        uow.commit();
        verify(listener).notifyEvent(eq(vEvent));
        assertThat(uow.uncommittedEvents()).isEmpty();
    }
}
