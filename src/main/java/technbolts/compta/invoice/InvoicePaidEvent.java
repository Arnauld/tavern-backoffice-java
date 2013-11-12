package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.Entity;
import technbolts.core.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

import static technbolts.core.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class InvoicePaidEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final BigDecimal price;

    @JsonCreator
    public InvoicePaidEvent(@JsonProperty("orderId") Id orderId,
                            @JsonProperty("price") BigDecimal price) {
        this.orderId = orderId;
        this.price = price;
    }

    @Override
    public Id entityId() {
        return orderId;
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

        InvoicePaidEvent that = (InvoicePaidEvent) o;

        if (!orderId.equals(that.orderId)) return false;
        if (!price.equals(that.price)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orderId.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvoicePaidEvent{" +
                "orderId=" + orderId +
                ", price=" + price +
                '}';
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if(required.isInstance(this))
            return (T)this;
        return null;
    }

}
