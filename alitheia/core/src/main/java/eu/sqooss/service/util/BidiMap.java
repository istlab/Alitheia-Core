package eu.sqooss.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Quick and dirty implementation of a bidirectional map.  
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 * @param <K>
 * @param <V>
 */
public class BidiMap<K, V> extends HashMap<K, V> {

    Map<V, K> inverse;
    
    public BidiMap() {
        super();
        inverse = new HashMap<V, K>();
    }
    
    public K getKey(V value) {
        return inverse.get(value);
    }
    
    public void putKey (V value, K key) {
        put(key, value);
        inverse.put(value, key);
    }
}