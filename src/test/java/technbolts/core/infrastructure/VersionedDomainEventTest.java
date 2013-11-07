package technbolts.core.infrastructure;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class VersionedDomainEventTest {

    private DomainEvent event;
    private Entity entity;

    @Before
    public void setUp() {
        event = mock(DomainEvent.class);
        entity = mock(Entity.class);
    }

    @Test(expected = IncompatibleEventVersionException.class)
    public void invalid_sequence_raises_an_exception__lower_version() {
        when(entity.version()).thenReturn(4L);
        VersionedDomainEvent vEvent = new VersionedDomainEvent(event, 3L, 17L);
        vEvent.applyOn(entity);
    }

    @Test(expected = IncompatibleEventVersionException.class)
    public void invalid_sequence_raises_an_exception__same_version() {
        when(entity.version()).thenReturn(3L);
        VersionedDomainEvent vEvent = new VersionedDomainEvent(event, 3L, 17L);
        vEvent.applyOn(entity);
    }

    @Test(expected = IncompatibleEventVersionException.class)
    public void invalid_sequence_raises_an_exception__greater_version() {
        when(entity.version()).thenReturn(2L);
        VersionedDomainEvent vEvent = new VersionedDomainEvent(event, 5L, 17L);
        vEvent.applyOn(entity);
    }

    @Test
    public void valid_sequence_delegates_to_event() {
        when(entity.version()).thenReturn(2L);
        VersionedDomainEvent vEvent = new VersionedDomainEvent(event, 3L, 17L);
        vEvent.applyOn(entity);

        verify(event).applyOn(entity);
    }
}
