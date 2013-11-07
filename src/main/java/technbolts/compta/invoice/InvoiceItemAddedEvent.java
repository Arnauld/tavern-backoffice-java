package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.Entity;
import technbolts.core.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.util.List;

import static technbolts.core.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class InvoiceItemAddedEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final List<Item> itemsAdded;

    @JsonCreator
    public InvoiceItemAddedEvent(@JsonProperty("orderId") Id orderId,
                                 @JsonProperty("itemsAdded")List<Item> itemsAdded) {
        this.orderId = orderId;
        this.itemsAdded = itemsAdded;
    }

    @Override
    public Id entityId() {
        return orderId;
    }

    public List<Item> getItemsAdded() {
        return itemsAdded;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        InvoiceState invoice = entity.adaptTo(InvoiceState.class);
        invoice.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceItemAddedEvent that = (InvoiceItemAddedEvent) o;

        if (!itemsAdded.equals(that.itemsAdded)) return false;
        if (!orderId.equals(that.orderId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orderId.hashCode();
        result = 31 * result + itemsAdded.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceItemAddedEvent{" +
                "orderId=" + orderId +
                ", itemsAdded=" + itemsAdded +
                '}';
    }
}
