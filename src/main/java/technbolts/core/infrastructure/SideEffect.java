package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface SideEffect<E> {
    void apply(E param);
}
