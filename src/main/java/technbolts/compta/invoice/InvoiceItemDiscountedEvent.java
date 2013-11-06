package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Entity;
import technbolts.compta.infrastructure.Id;

import static technbolts.compta.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InvoiceItemDiscountedEvent implements DomainEvent {
    @JsonProperty
    private final Id orderId;

    @JsonProperty
    private final Id itemReferenceId;

    @JsonProperty
    private final Discount discount;

    public InvoiceItemDiscountedEvent(Id orderId, Id itemReferenceId, Discount discount) {
        this.orderId = orderId;
        this.itemReferenceId = itemReferenceId;
        this.discount = discount;
    }

    @Override
    public Id entityId() {
        return orderId;
    }

    public Id getItemReferenceId() {
        return itemReferenceId;
    }

    public Discount getDiscount() {
        return discount;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        Invoice invoice = (Invoice) entity;
        invoice.onEvent(this);
    }
}
