package technbolts.compta.invoice;

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

    public Discount(Type type, BigDecimal amount) {
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

}
