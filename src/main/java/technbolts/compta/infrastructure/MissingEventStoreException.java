package technbolts.compta.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MissingEventStoreException extends RuntimeException {
    public MissingEventStoreException(String message) {
        super(message);
    }
}
