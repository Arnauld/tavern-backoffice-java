package technbolts.core.infrastructure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DefaultTransaction implements Transaction {

    private Map<Object, Object> context = Maps.newHashMap();
    private List<Commitable> commitables = Lists.newArrayList();

    @Override
    public void registerCommitable(Commitable commitable) {
        if(!commitables.contains(commitable))
            commitables.add(commitable);
    }

    @Override
    public void register(Object key, Object value) {
        context.put(key, value);
    }

    @Override
    public void unregister(Object key) {
        context.remove(key);
    }

    @Override
    public <T> T lookup(Object key) {
        return (T) context.get(key);
    }

    @Override
    public void commit() {
        for (Commitable commitable : commitables)
            commitable.commit();
    }

    @Override
    public void rollback() {
        for (Commitable commitable : commitables)
            commitable.rollback();
    }
}
