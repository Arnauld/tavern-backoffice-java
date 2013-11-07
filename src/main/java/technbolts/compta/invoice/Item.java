package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class Item {
    @JsonProperty
    private final Id referenceId;

    @JsonProperty
    private final BigDecimal quantity;

    @JsonProperty
    private final BigDecimal unitPrice;

    @JsonCreator
    public Item(@JsonProperty("referenceId") Id referenceId,
                @JsonProperty("quantity") BigDecimal quantity,
                @JsonProperty("unitPrice") BigDecimal unitPrice) {
        this.referenceId = referenceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Item(Id referenceId, int quantity, BigDecimal unitPrice) {
        this(referenceId, BigDecimal.valueOf(quantity), unitPrice);
    }

    public BigDecimal quantity() {
        return quantity;
    }

    public BigDecimal price() {
        return unitPrice.multiply(quantity);
    }

    public Id referenceId() {
        return referenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return quantity.equals(item.quantity)
                && referenceId.equals(item.referenceId)
                && unitPrice.equals(item.unitPrice);
    }

    @Override
    public int hashCode() {
        int result = referenceId.hashCode();
        result = 31 * result + quantity.hashCode();
        result = 31 * result + unitPrice.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "referenceId=" + referenceId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
