package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Entity;
import technbolts.compta.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

import static technbolts.compta.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class InvoicePaidEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final BigDecimal price;

    public InvoicePaidEvent(Id orderId, BigDecimal price) {
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
        Invoice invoice = (Invoice) entity;
        invoice.onEvent(this);
    }
}
