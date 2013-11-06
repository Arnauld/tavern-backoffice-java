package technbolts.compta.infrastructure;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
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
    private final Map<Id, Entity> modifiedEntities = Maps.newHashMap();

    /**
     *
     * @param entity
     */
    public void registerModified(Entity entity) {
        modifiedEntities.put(entity.entityId(), entity);
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

    public void commit() {
        for(Id entityId : modifiedEntities.keySet()) {
            if(entityStores.get(entityId) == null)
                throw new MissingEventStoreException("Event store not defined for entity " + modifiedEntities.get(entityId));
        }
    }

}
