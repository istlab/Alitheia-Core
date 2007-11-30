package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;

/**
 * This class must use the WSAccessorFactory to get the concrete WSAccessor object.
 * WSAccessor must return the result from the web services.  
 */
public interface WSConnection {
    
    public void addEventListener(String url, WSEventListener listener);
    
    public void removeEventListener(String url, WSEventListener listener);
    
    public WSResult getValue(String url);
    
    public WSResult addMetric(String url);
    
    public WSResult removeMetric(long metricId);
    
    public WSResult getFileGroupMetricResult(long merticId, long projectId, int projectVersison);
    
    //... and a lot of methods (see section 3.3.3 table 3)
}
