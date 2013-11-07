package technbolts.core.infrastructure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface EventStore {

    /**
     * Store the stream of events. The stream's id is usually the aggregate's id the events belong to.
     *
     * @param tx
     * @param streamId the id of the stream the events will be appended to
     * @param stream   the stream of event to store
     */
    void store(Transaction tx, @Nonnull Id streamId, @Nonnull Stream<VersionedDomainEvent> stream);

    /**
     * @param streamId the id of the stream the events will be appended to
     */
    @Nullable
    Stream<VersionedDomainEvent> openStream(@Nonnull Id streamId);
}
