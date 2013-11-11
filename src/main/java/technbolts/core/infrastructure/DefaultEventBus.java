package technbolts.core.infrastructure;

import com.google.common.collect.Lists;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DefaultEventBus<E> implements EventBus<E> {

    private final CopyOnWriteArrayList<Listener<E>> listeners = Lists.newCopyOnWriteArrayList();

    @Override
    public void publish(E event) {
        for (Listener<E> listener : listeners)
            listener.notifyEvent(event);
    }

    public void addListener(Listener<E> listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener<E> listener) {
        listeners.remove(listener);
    }
}
