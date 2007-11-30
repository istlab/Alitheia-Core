package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;

/**
 * The class has package visibility. 
 * The SCL's client can create the WSSession objects only from the WSAdmin. 
 */
class WSSessionImpl implements WSSession {

    public boolean clearWSResults() {
        return false;
    }

    public WSConnection getConnection() {
        return null;
    }

    public String getId() {
        return null;
    }

    public boolean removeWSResult(String key) {
        return false;
    }

    public void setWSResult(String key, WSResult result) {
    }

}
