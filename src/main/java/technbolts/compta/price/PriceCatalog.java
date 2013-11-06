package technbolts.compta.price;

import com.google.common.collect.Sets;
import technbolts.compta.infrastructure.*;
import technbolts.pattern.annotation.Service;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Service
public class PriceCatalog extends AbstractEntity {

    private final EventStore store;
    private final Set<Id> entryIds = Sets.newLinkedHashSet();

    public PriceCatalog(EventStore store) {
        this.store = store;
    }

    public CatalogEntry registerNewEntry(String label, BigDecimal price) {
        CatalogEntry entry = CatalogEntry.create(label, price);
        entryCreated(entry);

        UnitOfWork uow = UnitOfWork.current();
        uow.registerModified(entry);
        uow.registerModified(this);
        return entry;
    }

    private void entryCreated(CatalogEntry entry) {
        applyNewEvent(new PriceCatalogEntryCreatedEvent(entityId(), entry.entityId(), entry.getLabel(), entry.getPrice()));
    }

    @Nullable
    public BigDecimal priceOf(Id entryId) {
        Stream<VersionedDomainEvent> stream = store.openStream(entryId);
        if (stream == null)
            throw new EntityNotFoundException("No entity found for id: " + entryId);

        CatalogEntry entry = CatalogEntry.loadFromHistory(stream);
        return entry.getPrice();
    }

    void onEvent(PriceCatalogEntryCreatedEvent event) {
        entryIds.add(event.getEntryId());
    }

    public Set<Id> entryIds() {
        return Collections.unmodifiableSet(entryIds);
    }
}
