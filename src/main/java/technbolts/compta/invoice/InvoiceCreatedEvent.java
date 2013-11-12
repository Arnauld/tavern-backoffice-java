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
public class InvoiceCreatedEvent implements DomainEvent {
    @JsonProperty
    private final Id invoiceId;

    @JsonProperty
    private final List<Item> items;

    @JsonCreator
    public InvoiceCreatedEvent(@JsonProperty("invoiceId") Id invoiceId,
                               @JsonProperty("items") List<Item> items) {
        this.invoiceId = invoiceId;
        this.items = items;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(Id.undefined(), entity);
        InvoiceState invoice = entity.adaptTo(InvoiceState.class);
        invoice.onEvent(this);
    }

    public Id entityId() {
        return invoiceId;
    }

    public List<Item> items() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceCreatedEvent other = (InvoiceCreatedEvent) o;
        return items.equals(other.items) && invoiceId.equals(other.invoiceId);
    }

    @Override
    public int hashCode() {
        int result = invoiceId.hashCode();
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceCreatedEvent{" +
                "invoiceId=" + invoiceId +
                ", items=" + items +
                '}';
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if(required.isInstance(this))
            return (T)this;
        return null;
    }

}
