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
        
    @Override
    public V put(K key, V value) {
        V prev = super.put(key, value);
        inverse.put(value, key);
        return prev;
    };
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        for (K key : m.keySet()) {
            inverse.put(super.get(key), key);
        }
    }
}