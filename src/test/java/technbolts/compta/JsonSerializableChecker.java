package technbolts.compta;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.reflections.Reflections;
import technbolts.core.infrastructure.DomainEvent;
import technbolts.core.infrastructure.RandomBeanInstanciator;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Ignore // this is just a base class
public abstract class JsonSerializableChecker {
    private Object value1;
    private Object value2;
    private String json1;
    private String json2;

    private final RandomBeanInstanciator instanciator = new RandomBeanInstanciator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<?> klazz;

    public JsonSerializableChecker(Class<?> klazz) {
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
