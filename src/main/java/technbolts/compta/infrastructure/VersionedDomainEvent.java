package technbolts.compta.infrastructure;

import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class VersionedDomainEvent {

    public static VersionedDomainEvent versioned(long version, DomainEvent event) {
        return new VersionedDomainEvent(event, version, System.currentTimeMillis());
    }

    public static List<VersionedDomainEvent> versioned(long startingVersion, List<DomainEvent> events) {
        long v = startingVersion;
        List<VersionedDomainEvent> versionedEvents = Lists.newArrayListWithCapacity(events.size());
        for (DomainEvent event : events)
            versionedEvents.add(versioned(v++, event));
        return versionedEvents;
    }

    @JsonProperty
    private final DomainEvent event;

    @JsonProperty
    private final long version;

    @JsonProperty
    private final long creationTimestamp;

    @JsonCreator
    public VersionedDomainEvent(@JsonProperty("event") DomainEvent event,
                                @JsonProperty("version") long version,
                                @JsonProperty("creationTimestamp") long creationTimestamp) {
        this.event = event;
        this.version = version;
        this.creationTimestamp = creationTimestamp;
    }

    public void applyOn(Entity entity) {
        ensureVersionCompatibility(version, entity);
        event.applyOn(entity);
    }

    private void ensureVersionCompatibility(long version, Entity entity) {
        if (version != entity.version() + 1)
            throw new IncompatibleEventVersionException("Attempt to use event with version " + version + " whereas entity version is " + entity.version());
    }

    public long creationTimestamp() {
        return creationTimestamp;
    }

    public long version() {
        return version;
    }

    public DomainEvent domainEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionedDomainEvent that = (VersionedDomainEvent) o;
        return version == that.version
                && creationTimestamp == that.creationTimestamp
                && event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = event.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    public static SideEffect<VersionedDomainEvent> applyOnAsSideEffect(final Entity entity) {
        return new SideEffect<VersionedDomainEvent>() {
            @Override
            public void apply(VersionedDomainEvent event) {
                event.applyOn(entity);
            }
        };
    }

    @Override
    public String toString() {
        return "VersionedDomainEvent{" +
                "event=" + event +
                ", version=" + version +
                ", creationTimestamp=" + creationTimestamp +
                '}';
    }
}
