package technbolts.core.infrastructure;

import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RandomBeanInstanciator {
    public enum ConstructorSelector {
        MostNumberOfParameters() {
            @Override
            Constructor lookupConstructor(Class<?> klazz) {
                Constructor ctor = null;
                for (Constructor c : klazz.getConstructors()) {
                    Class[] typeParameters = c.getParameterTypes();
                    if (ctor == null
                            || typeParameters.length > ctor.getParameterTypes().length)
                        ctor = c;
                }
                return ctor;
            }
        };

        abstract Constructor lookupConstructor(Class<?> klazz);
    }

    private final Random random = new Random();
    private ConstructorSelector constructorSelector = ConstructorSelector.MostNumberOfParameters;

    public Object generateRandomBean(Class<?> klazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = constructorSelector.lookupConstructor(klazz);
        if (constructor == null)
            throw new IllegalArgumentException("No constructor detected for " + klazz);
        Object[] params = randomConstructorValues(constructor);
        return constructor.newInstance(params);
    }

    private Object[] randomConstructorValues(Constructor constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] args = new Object[constructor.getParameterTypes().length];
        int index = 0;
        for (Class<?> var : constructor.getParameterTypes()) {
            args[index] = randomValue(var, constructor.getGenericParameterTypes()[index]);
            index++;
        }
        return args;
    }

    private Object randomValue(Class<?> type, Type genericType) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(String.class)) {
            return randomString();
        } else if (type.equals(Date.class)) {
            return new Date(random.nextLong());
        } else if (type.equals(BigDecimal.class)) {
            return BigDecimal.valueOf(random.nextDouble());
        } else if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(random.nextInt());
        } else if (type.isEnum()) {
            Class eKl = (Class) type;
            @SuppressWarnings("unchecked")
            EnumSet<? extends Enum<?>> enums = EnumSet.allOf(eKl);
            Object[] values = enums.toArray();
            return values[random.nextInt(values.length)];
        } else if (List.class.isAssignableFrom(type)) {
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) genericType;
                Class<?> tKlazz = (Class<?>) pType.getActualTypeArguments()[0];
                List<Object> values = Lists.newArrayList();
                for (int i = 0, nb = random.nextInt(3); i < nb; i++) {
                    values.add(generateRandomBean(tKlazz));
                }
                return values;
            }
        } else if (isPackageNotSupported(type)) {
            throw new UnsupportedOperationException("Type not *YET* supported: " + type + " (" + genericType + ")");
        } else {
            return generateRandomBean(type);
        }
        throw new UnsupportedOperationException("Type not *YET* supported: " + type + " (" + genericType + ")");
    }


    private boolean isPackageNotSupported(Class<?> type) {
        String name = type.getPackage().getName();
        return name.startsWith("java");
    }

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public String randomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = random.nextInt(25); i < n; i++) {
            char c = CHARS.charAt(random.nextInt(CHARS.length()));
            builder.append(c);
        }
        return builder.toString();
    }

}
