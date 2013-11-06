package technbolts.compta.price;

import technbolts.compta.infrastructure.*;

import java.math.BigDecimal;

import static technbolts.compta.infrastructure.VersionedDomainEvent.applyOnAsSideEffect;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogEntry extends AbstractEntity {

    public static CatalogEntry create(String label, BigDecimal price) {
        return create(Id.next(CatalogEntry.class), label, price);
    }

    public static CatalogEntry create(Id entryId, String label, BigDecimal price) {
        CatalogEntry entry = new CatalogEntry();
        entry.doCreate(entryId, label, price);
        return entry;
    }

    public static CatalogEntry loadFromHistory(Stream<VersionedDomainEvent> stream) {
        if (!stream.hasRemaining())
            throw new EmptyStreamException();

        CatalogEntry entry = new CatalogEntry();
        stream.consume(applyOnAsSideEffect(entry));
        return entry;
    }

    private String label;
    private BigDecimal price;

    public String getLabel() {
        return label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    private void doCreate(Id entryId, String label, BigDecimal price) {
        applyNewEvent(new CatalogEntryCreatedEvent(entryId, label, price));
    }

    void onEvent(CatalogEntryCreatedEvent event) {
        assignId(event.entityId());
        this.label = event.getLabel();
        this.price = event.getInitialPrice();
    }

    void onEvent(CatalogEntryPriceChangedEvent event) {
        this.price = event.getPrice();
    }
}
