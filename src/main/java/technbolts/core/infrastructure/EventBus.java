package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface EventBus<E> {
    void publish(E event);
}
