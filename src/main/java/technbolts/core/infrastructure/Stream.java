package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Stream<E> {
    /**
     * @return <code>true</code> if, and only if, there is at least one element remaining in this stream
     */
    boolean hasRemaining();

    void consume(SideEffect<E> sideEffect);
}
