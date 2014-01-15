package eu.sqooss.metrics.java;

import java.util.List;

/**
 * A generic data reducer. Allows concurrent data appending from
 * multiple mappers. Reduction may take place while data is appended, but
 * results are not guaranteed to be accurate.
 * 
 * The reduce operation itself is delegated to subclasses.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @param <K> Type of key to aggregate results by
 * @param <V> Type of the reduced value
 */
public abstract class Reducer<T, V> {

    List<ReducePair<T, V>> reducedata;
    
    public void addEntry(T subkey, V value) {
    	ReducePair<T, V> keyvalue = new ReducePair<T, V>(subkey, value);
        reducedata.add(keyvalue);
    }

    public V reduce(T key) {
        V value = null;
     
        for (ReducePair<T, V> pair : reducedata) {
            if (pair.getLeft().equals(key))
                value = reduce(value, pair.getRight());
        }
        return value;
    }

    public abstract V reduce(V val1, V val2);
}

class IntReducer<T> extends Reducer<T, Integer> {

    @Override
    public Integer reduce(Integer val1, Integer val2) {
        return val1 + val2;
    }
}

class GraphReducer extends Reducer<String, String> {

    @Override
    public String reduce(String val1, String val2) {
        return null;
    }
}