package technbolts.core.infrastructure;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DomainEventsTest {

    @Test
    public void ensureEntityId_sameIds() {
        DomainEvents.ensureEntityId(Id.undefined(), entity(Id.undefined()));
        DomainEvents.ensureEntityId(Id.create("aef12"), entity(Id.create("aef12")));
    }

    @Test(expected = InvalidEventEntityException.class)
    public void ensureEntityId_differentIds_1() {
        DomainEvents.ensureEntityId(Id.create("aef12"), entity(Id.undefined()));
    }

    @Test(expected = InvalidEventEntityException.class)
    public void ensureEntityId_differentIds_2() {
        DomainEvents.ensureEntityId(Id.create("aef12"), entity(Id.create("abf23")));
    }

    private Entity entity(Id id) {
        Entity e = mock(Entity.class);
        when(e.entityId()).thenReturn(id);
        return e;
    }
}
