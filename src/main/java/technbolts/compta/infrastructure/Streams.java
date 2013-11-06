package technbolts.compta.infrastructure;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Streams {

    public static <E> Stream<E> from(List<E> values) {
        return new StreamInMemory<E>(values);
    }

    public static <E> Stream<E> from(E... values) {
        return new StreamInMemory<E>(values);
    }

    public static <E> List<E> toList(Stream<E> stream) {
        if(stream == null)
            throw new IllegalArgumentException("Stream cannot be null");

        final List<E> elements = Lists.newArrayList();
        stream.consume(new SideEffect<E>() {
            @Override
            public void apply(E e) {
                elements.add(e);
            }
        });
        return elements;
    }
}
