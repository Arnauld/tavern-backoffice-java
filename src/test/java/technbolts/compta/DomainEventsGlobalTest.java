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

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@RunWith(Parameterized.class)
public class DomainEventsGlobalTest extends JsonSerializableChecker {

    @Parameterized.Parameters(name = "{index} - {0}")
    public static Collection<Object[]> data() {
        Reflections r = new Reflections("technbolts.compta");
        Set<Class<? extends DomainEvent>> eventTypes = r.getSubTypesOf(DomainEvent.class);
        List<Object[]> params = Lists.newArrayList();
        for (Class<? extends DomainEvent> klazz : eventTypes) {
            if(klazz.isInterface() || Modifier.isAbstract(klazz.getModifiers()))
                continue;
            params.add(new Object[]{klazz});
        }
        return params;
    }

    public DomainEventsGlobalTest(Class<? extends DomainEvent> klazz) {
        super(klazz);
    }
}
