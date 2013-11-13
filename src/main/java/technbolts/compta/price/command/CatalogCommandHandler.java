package technbolts.compta.price.command;

import technbolts.compta.price.Catalog;
import technbolts.core.infrastructure.*;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CatalogCommandHandler {
    private final EventStore eventStore;
    private final EventBus<VersionedDomainEvent> eventBus;

    public CatalogCommandHandler(EventStore eventStore, EventBus<VersionedDomainEvent> eventBus) {
        if (eventBus == null)
            throw new IllegalArgumentException("Event bus cannot be null");
        if (eventStore == null)
            throw new IllegalArgumentException("Event store cannot be null");

        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    public Id handle(CreateCatalogCommand command) {
        UnitOfWork uow = new UnitOfWork(eventBus, eventStore);
        Catalog catalog = Catalog.create(uow, command.getLabel(), eventStore);
        uow.commit();
        return catalog.entityId();
    }
}
