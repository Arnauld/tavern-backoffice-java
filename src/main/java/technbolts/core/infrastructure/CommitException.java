package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CommitException extends RuntimeException {
    public CommitException(Throwable cause) {
        super(cause);
    }
}
