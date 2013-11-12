package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface DomainEvent extends Adaptable {
    Id entityId();

    void applyOn(Entity entity);
}
