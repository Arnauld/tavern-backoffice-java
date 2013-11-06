package technbolts.compta.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InvalidEventEntityException extends RuntimeException {

    public InvalidEventEntityException(String message) {
        super(message);
    }

    public InvalidEventEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
