package technbolts.compta.price;

import technbolts.core.infrastructure.*;

import java.math.BigDecimal;

import static technbolts.core.infrastructure.VersionedDomainEvent.applyOnAsSideEffect;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogEntry extends AbstractEntity {

    public static CatalogEntry create(UnitOfWork uow, String label, BigDecimal price) {
        return create(uow, Id.next(CatalogEntry.class), label, price);
    }

    public static CatalogEntry create(UnitOfWork uow, Id entryId, String label, BigDecimal price) {
        CatalogEntry entry = new CatalogEntry(uow);
        entry.doCreate(entryId, label, price);
        return entry;
    }

    public static CatalogEntry loadFromHistory(UnitOfWork uow, Stream<VersionedDomainEvent> stream) {
        if (!stream.hasRemaining())
            throw new EmptyStreamException();

        CatalogEntry entry = new CatalogEntry(uow);
        stream.consume(applyOnAsSideEffect(entry));
        return entry;
    }

    private final CatalogEntryState state = new CatalogEntryState();

    public CatalogEntry(UnitOfWork uow) {
        super(uow);
    }

    @Override
    public Id entityId() {
        return state.entityId();
    }

    private void doCreate(Id entryId, String label, BigDecimal price) {
        applyNewEvent(new CatalogEntryCreatedEvent(entryId, label, price));
    }

    public String getLabel() {
        return state.getLabel();
    }

    public BigDecimal getPrice() {
        return state.getPrice();
    }

}
