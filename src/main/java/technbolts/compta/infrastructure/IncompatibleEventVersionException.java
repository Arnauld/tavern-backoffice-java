package technbolts.compta.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class IncompatibleEventVersionException extends RuntimeException {

    public IncompatibleEventVersionException(String message) {
        super(message);
    }

    public IncompatibleEventVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
