package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Transaction extends Commitable {
    /**
     * Context
     * @param key
     * @param value
     */
    void register(Object key, Object value);

    /**
     * Context
     * @param key
     */
    void unregister(Object key);

    /**
     * Context
     * @param key
     * @param <T>
     * @return
     */
    <T> T lookup(Object key);

    void registerCommitable(Commitable commitable);
}
