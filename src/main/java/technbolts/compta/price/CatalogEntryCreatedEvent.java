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
public class CatalogEntryCreatedEvent implements DomainEvent {

    @JsonProperty
    private final Id entryId;

    @JsonProperty
    private String label;

    @JsonProperty
    private final BigDecimal initialPrice;

    public CatalogEntryCreatedEvent(Id entryId, String label, BigDecimal initialPrice) {
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
        ensureEntityId(this, entity);
        CatalogEntry entry = (CatalogEntry)entity;
        entry.onEvent(this);
    }
}
