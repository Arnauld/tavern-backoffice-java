package technbolts.compta.price;

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
public class CatalogEntryPriceChangedEvent implements CatalogEntryEvent {

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private final BigDecimal price;

    @JsonCreator
    public CatalogEntryPriceChangedEvent(@JsonProperty("entryId") Id entryId,
                                         @JsonProperty("price") BigDecimal price) {
        this.entryId = entryId;
        this.price = price;
    }

    @Override
    public Id entityId() {
        return entryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        CatalogEntryState entry = entity.adaptTo(CatalogEntryState.class);
        entry.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogEntryPriceChangedEvent that = (CatalogEntryPriceChangedEvent) o;

        if (!entryId.equals(that.entryId)) return false;
        if (!price.equals(that.price)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogEntryPriceChangedEvent{" +
                "entryId=" + entryId +
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
