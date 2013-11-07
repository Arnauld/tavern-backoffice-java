package technbolts.compta.invoice;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class Discount {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public enum Type {
        Percent,
        Value
    }

    @JsonProperty
    private final Type type;

    @JsonProperty
    private final BigDecimal amount;

    @JsonCreator
    public Discount(@JsonProperty("type") Type type,
                    @JsonProperty("amount") BigDecimal amount) {
        this.type = type;
        this.amount = amount;
    }

    public Discount(Type type, int amount) {
        this(type, BigDecimal.valueOf(amount));
    }

    public BigDecimal calculatePrice(Item item) {
        switch (type) {
            case Percent:
                return item.price().multiply(amount).divide(HUNDRED);
            case Value:
                BigDecimal discount = amount.multiply(item.quantity());
                return item.price().subtract(discount);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Discount discount = (Discount) o;

        if (!amount.equals(discount.amount)) return false;
        if (type != discount.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + amount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "type=" + type +
                ", amount=" + amount +
                '}';
    }
}
