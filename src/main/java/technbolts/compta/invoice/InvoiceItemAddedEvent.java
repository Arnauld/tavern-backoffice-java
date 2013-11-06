package technbolts.compta.invoice;

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
public class InvoiceItemAddedEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final List<Item> itemAdded;

    public InvoiceItemAddedEvent(Id orderId, List<Item> itemAdded) {
        this.orderId = orderId;
        this.itemAdded = itemAdded;
    }

    @Override
    public Id entityId() {
        return orderId;
    }

    public List<Item> getItemAdded() {
        return itemAdded;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        Invoice invoice = (Invoice) entity;
        invoice.onEvent(this);
    }
}
