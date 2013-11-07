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
public class PriceCatalogEntryCreatedEvent implements DomainEvent {

    @JsonProperty
    private final Id catalogId;

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private final String label;

    @JsonProperty
    private final BigDecimal price;

    @JsonCreator
    public PriceCatalogEntryCreatedEvent(@JsonProperty("catalogId") Id catalogId,
                                         @JsonProperty("entryId") Id entryId,
                                         @JsonProperty("label") String label,
                                         @JsonProperty("price") BigDecimal price) {
        this.catalogId = catalogId;
        this.entryId = entryId;
        this.label = label;
        this.price = price;
    }

    @Override
    public Id entityId() {
        return catalogId;
    }

    public Id getEntryId() {
        return entryId;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        PriceCatalogState catalog = (PriceCatalogState) entity;
        catalog.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceCatalogEntryCreatedEvent that = (PriceCatalogEntryCreatedEvent) o;

        if (!catalogId.equals(that.catalogId)) return false;
        if (!entryId.equals(that.entryId)) return false;
        if (!label.equals(that.label)) return false;
        if (!price.equals(that.price)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = catalogId.hashCode();
        result = 31 * result + entryId.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PriceCatalogEntryCreatedEvent{" +
                "catalogId=" + catalogId +
                ", entryId=" + entryId +
                ", label='" + label + '\'' +
                ", price=" + price +
                '}';
    }
}
