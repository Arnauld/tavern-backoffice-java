package technbolts.compta.price;

import org.codehaus.jackson.annotate.JsonProperty;
import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Entity;
import technbolts.compta.infrastructure.Id;
import technbolts.pattern.annotation.ValueObject;

import java.math.BigDecimal;

import static technbolts.compta.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@ValueObject
public class CatalogEntryPriceChangedEvent implements DomainEvent {

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private final BigDecimal price;

    public CatalogEntryPriceChangedEvent(Id entryId, BigDecimal price) {
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
        CatalogEntry entry = (CatalogEntry)entity;
        entry.onEvent(this);
    }
}
