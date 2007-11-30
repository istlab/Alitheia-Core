package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;

/**
 * The WSResult can be stored in the user session. 
 */
public interface WSSession {
    
    public String getId();
    
    public void setWSResult(String key, WSResult result);
    
    public boolean removeWSResult(String key);
    
    public boolean clearWSResults();
    
    public WSConnection getConnection();
    
}
