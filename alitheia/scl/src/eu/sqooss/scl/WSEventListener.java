package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;

public interface WSEventListener {
    
    public void changeOccurred(WSResult oldValue, WSResult newValue);
    
}
