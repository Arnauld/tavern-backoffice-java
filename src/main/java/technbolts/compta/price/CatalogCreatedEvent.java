package technbolts.compta.price;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.core.infrastructure.Entity;
import technbolts.core.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import static technbolts.core.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class CatalogCreatedEvent implements CatalogEvent {

    @JsonProperty
    private final Id catalogId;

    @JsonProperty
    private final String label;

    @JsonCreator
    public CatalogCreatedEvent(@JsonProperty("catalogId") Id catalogId,
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
        ensureEntityId(Id.undefined(), entity);
        CatalogState entry = entity.adaptTo(CatalogState.class);
        entry.onEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogCreatedEvent that = (CatalogCreatedEvent) o;

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
        return "CatalogCreatedEvent{" +
                "catalogId=" + catalogId +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if(required.isInstance(this))
            return (T)this;
        return null;
    }
}
