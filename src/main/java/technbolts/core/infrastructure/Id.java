package technbolts.core.infrastructure;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import technbolts.pattern.annotation.ValueObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@ValueObject
@JsonSerialize(using = Id.Serializer.class)
@JsonDeserialize(using = Id.Deserializer.class)
public class Id {

    private static final UndefinedId UNDEFINED_ID = new UndefinedId();

    public static Id create(String stringRepresentation) {
        if (stringRepresentation == null) {
            return UNDEFINED_ID;
        }
        return new Id(stringRepresentation);
    }

    public static Id undefined() {
        return UNDEFINED_ID;
    }

    public enum Strategy {
        Uuid {
            @Override
            public Id next(Class<?> entityType) {
                return create(UUID.randomUUID().toString());
            }

            @Override
            public void reset() {
            }
        },
        TypedSequence {
            private ConcurrentMap<Class<?>, AtomicLong> generators = Maps.newConcurrentMap();

            @Override
            public Id next(Class<?> entityType) {
                AtomicLong generator = generators.get(entityType);
                if (generator == null) {
                    AtomicLong newGen = new AtomicLong();
                    generator = generators.putIfAbsent(entityType, newGen);
                    if (generator == null) {
                        generator = newGen;
                    }
                }
                return create(entityType.getSimpleName() + "~" + generator.incrementAndGet());
            }

            @Override
            public void reset() {
                generators.clear();
            }
        },
        Sequence {
            private AtomicLong generator = new AtomicLong();

            @Override
            public Id next(Class<?> entityType) {
                return create(String.valueOf(generator.incrementAndGet()));
            }

            @Override
            public void reset() {
                generator.set(0);
            }
        };

        public abstract Id next(Class<?> entityType);

        public abstract void reset();
    }

    private static Strategy defaultStrategy = Strategy.Uuid;

    public static void setDefaultStrategy(Strategy defaultStrategy) {
        Id.defaultStrategy = defaultStrategy;
    }

    // TODO introduce non-static generator, to prevent concurrent reset or strategy change between tests
    // @Deprecated
    public static Id next(Class<?> entityType) {
        return defaultStrategy.next(entityType);
    }

    @JsonProperty
    private final String asString;

    public Id(final String asString) {
        this.asString = asString;
    }

    private Id() {
        //only for UndefinedId instance
        this.asString = null;
    }

    public String asString() {
        return asString;
    }

    @Override
    public String toString() {
        return asString;
    }

    @JsonIgnore
    public boolean isUndefined() {
        return this == UNDEFINED_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Id id = (Id) o;
        return StringUtils.equals(asString, id.asString);
    }

    @Override
    public int hashCode() {
        return (asString == null) ? 0 : asString.hashCode();
    }

    private static class UndefinedId extends Id {
        @Override
        public String toString() {
            return "UndefinedId";
        }
    }

    public static class Serializer extends JsonSerializer<Id> {
        @Override
        public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.asString());
        }
    }

    public static class Deserializer extends JsonDeserializer<Id> {
        @Override
        public Id deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return Id.create(jp.getText());
        }
    }
}