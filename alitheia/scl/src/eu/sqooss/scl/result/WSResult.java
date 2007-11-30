package eu.sqooss.scl.result;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The WSResult is similar to the MetricResult. 
 */
public abstract class WSResult implements Iterable<ArrayList<WSResultEntry>>,
                                          Iterator<ArrayList<WSResultEntry>> {
    
    public static WSResult fromXML() {
        return null;
    }
    
    public abstract String toXML();
    
    public abstract boolean validate();
    
}
