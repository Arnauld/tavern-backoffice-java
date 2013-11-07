package technbolts.compta;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.reflections.Reflections;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.RandomBeanInstanciator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@RunWith(Parameterized.class)
public class DomainEventsGlobalTest {

    private Object value1;
    private Object value2;
    private String json1;
    private String json2;

    @Parameterized.Parameters(name = "{index} - {0}")
    public static Collection<Object[]> data() {
        Reflections r = new Reflections("technbolts.compta");
        Set<Class<? extends DomainEvent>> eventTypes = r.getSubTypesOf(DomainEvent.class);
        List<Object[]> params = Lists.newArrayList();
        for (Class<? extends DomainEvent> klazz : eventTypes) {
            params.add(new Object[]{klazz});
        }
        return params;
    }

    private final RandomBeanInstanciator instanciator = new RandomBeanInstanciator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<? extends DomainEvent> klazz;

    public DomainEventsGlobalTest(Class<? extends DomainEvent> klazz) {
        this.klazz = klazz;
    }

    @Before
    public void setUp() throws Exception  {
        value1 = instanciator.generateRandomBean(klazz);
        value2 = instanciator.generateRandomBean(klazz);
        json1 = mapper.writeValueAsString(value1);
        json2 = mapper.writeValueAsString(value2);
    }

    @Test
    public void equalsIsImplemented__basedOnRandomProbability() throws Exception {
        assertThat(value1)
                .describedAs("1: " + json1 + ",  2:" + json2)
                .isNotEqualTo(value2);
        assertThat(json1).isNotNull().isNotEqualTo(json2);
    }

    @Test
    public void instanceAreSerializableDeserializable() throws Exception {
        Object unser1 = mapper.readValue(json1, klazz);
        Object unser2 = mapper.readValue(json2, klazz);
        assertThat(unser1).isEqualTo(value1);
        assertThat(unser2).isEqualTo(value2);
        assertThat(unser1).isNotEqualTo(unser2);
    }
}
