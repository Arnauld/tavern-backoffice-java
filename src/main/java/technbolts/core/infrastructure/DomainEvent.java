package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface DomainEvent {
    Id entityId();

    void applyOn(Entity entity);
}