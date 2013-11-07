package technbolts.core.infrastructure;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public abstract class AbstractEntity implements Entity {
    //
    private List<VersionedDomainEvent> uncommittedEvents = Lists.newArrayList();
    private long version;
    private final UnitOfWork unitOfWork;

    protected AbstractEntity(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public long version() {
        return version;
    }

    protected void applyNewEvent(DomainEvent event) {
        // TODO time service
        applyEvent(new VersionedDomainEvent(event, version + 1, System.currentTimeMillis()), true);
    }

    protected void applyEvent(VersionedDomainEvent versionedEvent, boolean isNew) {
        versionedEvent.applyOn(this);
        this.version = versionedEvent.version();
        if (isNew) {
            unitOfWork().registerNew(versionedEvent);
        }
    }

    protected UnitOfWork unitOfWork() {
        return unitOfWork;
    }

    @Override
    public <T> T adaptTo(Class<T> required) {
        return null;
    }
}
