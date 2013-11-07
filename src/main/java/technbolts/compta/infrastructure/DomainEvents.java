package technbolts.compta.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DomainEvents {

    private DomainEvents() {
    }

    public static void ensureEntityId(DomainEvent event, Entity entity) {
        ensureEntityId(event.entityId(), entity);
    }

    public static void ensureEntityId(Id entityId, Entity entity) {
        if (!entityId.equals(entity.entityId()))
            throw new InvalidEventEntityException("Attempt to use event that does not belongs to the entity got: " + entityId + ", expected: " + entity.entityId());
    }

}
