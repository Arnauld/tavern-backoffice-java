package technbolts.core.util;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MapBuilder<K,V> {

    public static <K,V> MapBuilder<K,V> mapBuilder() {
        return new MapBuilder<K,V>();
    }

    public static MapBuilder<Object,Object> mapBuilderOO() {
        return mapBuilder();
    }

    private Map<K,V> map = Maps.newHashMap();
    public MapBuilder with(K key, V value) {
        map.put(key, value);
        return this;
    }
    public Map<K,V> build() {
        return Maps.newHashMap(map);
    }
}
