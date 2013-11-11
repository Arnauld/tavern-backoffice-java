package technbolts.compta.price;

import technbolts.core.infrastructure.*;
import technbolts.pattern.annotation.Service;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Set;

import static technbolts.core.infrastructure.VersionedDomainEvent.applyOnAsSideEffect;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Service
public class PriceCatalog extends AbstractEntity {

    public static PriceCatalog create(UnitOfWork uow, String label, EventStore entriesStore) {
        return create(uow, Id.next(PriceCatalog.class), label, entriesStore);
    }

    public static PriceCatalog create(UnitOfWork uow, Id priceCatalogId, String label, EventStore entriesStore) {
        PriceCatalog entry = new PriceCatalog(uow, entriesStore);
        entry.doCreate(priceCatalogId, label);
        return entry;
    }

    public static PriceCatalog loadFromHistory(UnitOfWork uow, EventStore entriesStore, Stream<VersionedDomainEvent> stream) {
        if (!stream.hasRemaining())
            throw new EmptyStreamException();

        PriceCatalog entry = new PriceCatalog(uow, entriesStore);
        stream.consume(applyOnAsSideEffect(entry));
        return entry;
    }


    private final EventStore store;
    private final PriceCatalogState state = new PriceCatalogState();

    public PriceCatalog(UnitOfWork unitOfWork, EventStore entryStore) {
        super(unitOfWork);
        this.store = entryStore;
    }

    @Override
    public Id entityId() {
        return state.entityId();
    }

    private void doCreate(Id entryId, String label) {
        applyNewEvent(new PriceCatalogCreatedEvent(entryId, label));
    }


    public CatalogEntry registerNewEntry(String label, BigDecimal price) {
        CatalogEntry entry = CatalogEntry.create(unitOfWork(), label, price);
        applyNewEvent(new PriceCatalogEntryCreatedEvent(entityId(), entry.entityId(), entry.getLabel(), entry.getPrice()));
        return entry;
    }

    @Nullable
    public BigDecimal priceOf(Id entryId) {
        Stream<VersionedDomainEvent> stream = store.openStream(entryId);
        if (stream == null)
            throw new EntityNotFoundException("No entity found for id: " + entryId);

        CatalogEntry entry = CatalogEntry.loadFromHistory(unitOfWork(), stream);
        return entry.getPrice();
    }

    public Set<Id> entryIds() {
        return state.entryIds();
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        if (required.equals(PriceCatalogState.class))
            return (T) state;
        return super.adaptTo(required);
    }
}
