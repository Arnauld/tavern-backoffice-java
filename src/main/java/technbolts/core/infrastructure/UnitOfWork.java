package technbolts.core.infrastructure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class UnitOfWork {

    private static ThreadLocal<UnitOfWork> holder = new ThreadLocal<UnitOfWork>();

    public static UnitOfWork current() {
        UnitOfWork uow = holder.get();
        if (uow == null)
            throw new UnitOfWorkNotBoundException();
        return uow;
    }

    public static UnitOfWork start() {
        UnitOfWork uow = holder.get();
        if (uow != null)
            throw new UnitOfWorkAlreadyBoundException();
        return uow;
    }

    private final Map<Id, EventStore> entityStores = Maps.newHashMap();
    private final List<VersionedDomainEvent> newEvents = Lists.newArrayList();
    private final EventBus<VersionedDomainEvent> eventBus;
    private final EventStore defaultDataStore;

    public UnitOfWork(EventBus<VersionedDomainEvent> eventBus, EventStore defaultDataStore) {
        this.eventBus = eventBus;
        this.defaultDataStore = defaultDataStore;
    }

    /**
     * New and uncommitted event.
     *
     * @param event
     */
    public void registerNew(VersionedDomainEvent event) {
        newEvents.add(event);
    }

    /**
     * Register the event store that must be used to store the event for the given
     * aggregate root. This allows to use different event store within the same
     * unit of work. Event store can be different based on entity type.
     *
     * @param entityId   aggregate root's id
     * @param eventStore event store used to store the aggregate's events.
     */
    public void registerEventStoreFor(@Nonnull Id entityId, @Nonnull EventStore eventStore) {
        entityStores.put(entityId, eventStore);
    }

    public List<VersionedDomainEvent> uncommittedEvents() {
        return Collections.unmodifiableList(newEvents);
    }

    public void commit() {
        Transaction tx = new DefaultTransaction();
        boolean txCommitted = false;
        try {
            List<VersionedDomainEvent> batch = Lists.newArrayList();
            Id currentId = Id.undefined();
            for (VersionedDomainEvent event : newEvents) {
                Id entityId = event.entityId();
                if (!currentId.equals(entityId)) {
                    flushBatch(tx, currentId, batch);
                    currentId = entityId;
                    if (!batch.isEmpty())
                        batch = Lists.newArrayList();
                }
                batch.add(event);
            }

            // remaining?
            flushBatch(tx, currentId, batch);

            tx.commit();
            txCommitted = true;
            for (VersionedDomainEvent event : newEvents) {
                eventBus.publish(event);
            }
            newEvents.clear();
        } finally {
            if (!txCommitted)
                tx.rollback();
        }
    }

    private void flushBatch(Transaction tx, Id entityId, List<VersionedDomainEvent> batch) {
        if (batch == null || batch.isEmpty())
            return;
        EventStore eventStore = entityStores.get(entityId);
        if (eventStore == null) {
            eventStore = defaultDataStore;
        }
        if (eventStore == null) {
            throw new MissingEventStoreException("Event store not defined for entity " + entityId);
        }
        eventStore.store(tx, entityId, Streams.from(batch));
    }

}
