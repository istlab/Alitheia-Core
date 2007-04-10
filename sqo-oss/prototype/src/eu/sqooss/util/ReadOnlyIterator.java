package eu.sqooss.util;

import java.util.Iterator;

/**
 * Read-only Iterator
 * 
 */
// TODO: make this one generic-based to enforce type checking
public class ReadOnlyIterator implements Iterator {
    private Iterator iterator;

    public ReadOnlyIterator(Iterator i) {
        this.iterator = i;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public void remove() {
        // Read-only, no implementation for this method
    }

}
