package technbolts.core.infrastructure;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Adaptable {
    <T> T adaptTo(Class<T> required);
}
