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
public class CatalogEntryCreatedEvent implements DomainEvent {

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private String label;

    @JsonProperty
    private final BigDecimal initialPrice;

    @JsonCreator
    public CatalogEntryCreatedEvent(@JsonProperty("entryId") Id entryId,
                                    @JsonProperty("label") String label,
                                    @JsonProperty("initialPrice") BigDecimal initialPrice) {
        this.entryId = entryId;
        this.label = label;
        this.initialPrice = initialPrice;
    }

    @Override
    public Id entityId() {
        return entryId;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(Id.undefined(), entity);
        CatalogEntryState entry = entity.adaptTo(CatalogEntryState.class);
        entry.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogEntryCreatedEvent that = (CatalogEntryCreatedEvent) o;

        if (!entryId.equals(that.entryId)) return false;
        if (!initialPrice.equals(that.initialPrice)) return false;
        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + initialPrice.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogEntryCreatedEvent{" +
                "entryId=" + entryId +
                ", label='" + label + '\'' +
                ", initialPrice=" + initialPrice +
                '}';
    }
}
