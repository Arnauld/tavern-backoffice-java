package technbolts.compta.invoice;

import org.junit.Before;
import org.junit.Test;
import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Id;
import technbolts.compta.infrastructure.Streams;
import technbolts.compta.infrastructure.VersionedDomainEvent;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static technbolts.compta.infrastructure.VersionedDomainEvent.versioned;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InvoiceTest {

    private Id orderId;
    private Id referenceId;
    private List<Item> items;

    @Before
    public void setUp() {
        orderId = Id.create("ab17f");
        referenceId = Id.create("aef");
        items = Arrays.asList(new Item(referenceId, 5, BigDecimal.TEN));

    }

    @Test
    public void create_order() {
        Invoice invoice = Invoice.create(orderId, items);

        assertThat(invoice.uncommittedEvents()).contains(versioned(1, new InvoiceCreatedEvent(orderId, items)));
    }

    @Test
    public void reload_from_history__simple_case() {
        List<VersionedDomainEvent> versionedEvents = versioned(1,
                Arrays.<DomainEvent>asList(new InvoiceCreatedEvent(orderId, items)));
        Invoice invoice = Invoice.loadFromHistory(Streams.from(versionedEvents));

        assertThat(invoice).isNotNull();
        assertThat(invoice.entityId()).isEqualTo(orderId);
        assertThat(invoice.items()).isEqualTo(items);
    }

    @Test
    public void price() {
        Invoice invoice = Invoice.create(orderId, items);

        assertThat(invoice).isNotNull();
        assertThat(invoice.price()).isEqualByComparingTo(new BigDecimal("50"));
    }

    @Test
    public void price_is_adjusted_with_discount() {
        Invoice invoice = Invoice.create(orderId, items);
        invoice.applyDiscount(referenceId, new Discount(Discount.Type.Percent, 50));

        assertThat(invoice.price()).isEqualByComparingTo(new BigDecimal("25"));
    }

}
