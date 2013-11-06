package technbolts.compta.infrastructure;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public abstract class AbstractEntity implements Entity {
        //
    private List<VersionedDomainEvent> uncommittedEvents = Lists.newArrayList();
    private Id entityId = Id.undefined();
    private long version;


    protected void assignId(Id entityId) {
        this.entityId = entityId;
    }

    @Override
    public Id entityId() {
        return entityId;
    }

    @Override
    public long version() {
        return version;
    }


    @Override
    public List<VersionedDomainEvent> uncommittedEvents() {
        return uncommittedEvents;
    }

    protected void applyNewEvent(DomainEvent event) {
        // TODO time service
        applyEvent(new VersionedDomainEvent(event, version + 1, System.currentTimeMillis()), true);
    }

    protected void applyEvent(VersionedDomainEvent versionedEvent, boolean isNew) {
        versionedEvent.applyOn(this);
        if(isNew)
            uncommittedEvents.add(versionedEvent);
        this.version = versionedEvent.version();
    }

}
