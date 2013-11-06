package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Entity;
import technbolts.compta.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.util.List;

import static technbolts.compta.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class InvoiceCreatedEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final List<Item> items;

    @JsonCreator
    public InvoiceCreatedEvent(@JsonProperty("orderId") Id orderId,
                               @JsonProperty("items") List<Item> items) {
        this.orderId = orderId;
        this.items = items;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(Id.undefined(), entity);
        Invoice invoice = (Invoice) entity;
        invoice.onEvent(this);
    }

    public Id entityId() {
        return orderId;
    }

    public List<Item> items() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceCreatedEvent other = (InvoiceCreatedEvent) o;
        return items.equals(other.items) && orderId.equals(other.orderId);
    }

    @Override
    public int hashCode() {
        int result = orderId.hashCode();
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceCreatedEvent{" +
                "orderId=" + orderId +
                ", items=" + items +
                '}';
    }
}
