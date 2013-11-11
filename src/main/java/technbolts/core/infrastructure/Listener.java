package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Listener<E> {
    void notifyEvent(E e);
}
