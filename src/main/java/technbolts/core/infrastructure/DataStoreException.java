package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DataStoreException extends RuntimeException {
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
