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
public class PriceCatalogCreatedEvent implements DomainEvent {

    @JsonProperty
    private final Id catalogId;

    @JsonProperty
    private final String label;

    @JsonCreator
    public PriceCatalogCreatedEvent(@JsonProperty("catalogId") Id catalogId,
                                    @JsonProperty("label") String label) {
        this.catalogId = catalogId;
        this.label = label;
    }

    @Override
    public Id entityId() {
        return catalogId;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void applyOn(Entity entity) {
        ensureEntityId(this, entity);
        PriceCatalogState entry = entity.adaptTo(PriceCatalogState.class);
        entry.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceCatalogCreatedEvent that = (PriceCatalogCreatedEvent) o;

        if (!catalogId.equals(that.catalogId)) return false;
        if (!label.equals(that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = catalogId.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PriceCatalogCreatedEvent{" +
                "catalogId=" + catalogId +
                ", label='" + label + '\'' +
                '}';
    }
}
