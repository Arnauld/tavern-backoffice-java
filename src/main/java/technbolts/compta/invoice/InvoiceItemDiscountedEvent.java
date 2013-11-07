package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.Entity;
import technbolts.core.infrastructure.Id;

import static technbolts.core.infrastructure.DomainEvents.ensureEntityId;

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

    @JsonCreator
    public InvoiceItemDiscountedEvent(
            @JsonProperty("orderId") Id orderId,
            @JsonProperty("itemReferenceId") Id itemReferenceId,
            @JsonProperty("discount") Discount discount) {
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
        InvoiceState invoice = entity.adaptTo(InvoiceState.class);
        invoice.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceItemDiscountedEvent that = (InvoiceItemDiscountedEvent) o;

        if (!discount.equals(that.discount)) return false;
        if (!itemReferenceId.equals(that.itemReferenceId)) return false;
        if (!orderId.equals(that.orderId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orderId.hashCode();
        result = 31 * result + itemReferenceId.hashCode();
        result = 31 * result + discount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceItemDiscountedEvent{" +
                "orderId=" + orderId +
                ", itemReferenceId=" + itemReferenceId +
                ", discount=" + discount +
                '}';
    }
}
