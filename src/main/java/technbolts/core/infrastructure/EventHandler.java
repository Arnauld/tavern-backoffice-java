package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface EventHandler {
    void handleEvent(VersionedDomainEvent event);
}
