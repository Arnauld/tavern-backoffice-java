package technbolts.compta.price;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Entity;
import technbolts.core.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

import static technbolts.core.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class CatalogOnEntryAddedEvent implements CatalogEvent {

    @JsonProperty
    private final Id catalogId;

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private final String entryLabel;

    @JsonProperty
    private final BigDecimal entryPrice;

    @JsonCreator
    public CatalogOnEntryAddedEvent(@JsonProperty("catalogId") Id catalogId,
                                    @JsonProperty("entryId") Id entryId,
                                    @JsonProperty("label") String entryLabel,
                                    @JsonProperty("price") BigDecimal entryPrice) {
        this.catalogId = catalogId;
        this.entryId = entryId;
        this.entryLabel = entryLabel;
        this.entryPrice = entryPrice;
    }

    @Override
    public Id entityId() {
        return catalogId;
    }

    public Id entryId() {
        return entryId;
    }

    public String entryLabel() {
        return entryLabel;
    }

    public BigDecimal entryPrice() {
        return entryPrice;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        CatalogState catalog = entity.adaptTo(CatalogState.class);
        catalog.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogOnEntryAddedEvent that = (CatalogOnEntryAddedEvent) o;

        if (!catalogId.equals(that.catalogId)) return false;
        if (!entryId.equals(that.entryId)) return false;
        if (!entryLabel.equals(that.entryLabel)) return false;
        if (!entryPrice.equals(that.entryPrice)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = catalogId.hashCode();
        result = 31 * result + entryId.hashCode();
        result = 31 * result + entryLabel.hashCode();
        result = 31 * result + entryPrice.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogOnEntryAddedEvent{" +
                "catalogId=" + catalogId +
                ", entryId=" + entryId +
                ", label='" + entryLabel + '\'' +
                ", price=" + entryPrice +
                '}';
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if(required.isInstance(this))
            return (T)this;
        return null;
    }

}
