package technbolts.compta.price;

import technbolts.compta.infrastructure.DomainEvent;
import technbolts.compta.infrastructure.Entity;
import technbolts.compta.infrastructure.Id;

import java.math.BigDecimal;

import static technbolts.compta.infrastructure.DomainEvents.ensureEntityId;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PriceCatalogEntryCreatedEvent implements DomainEvent {
    private final Id catalogId;
    private final Id entryId;
    private final String label;
    private final BigDecimal price;

    public PriceCatalogEntryCreatedEvent(Id catalogId, Id entryId, String label, BigDecimal price) {
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
                PriceCatalog catalog = (PriceCatalog)entity;
        catalog.onEvent(this);
    }
}
