package technbolts.compta.infrastructure;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Entity {
    Id entityId();

    long version();

    List<VersionedDomainEvent> uncommittedEvents();
}
