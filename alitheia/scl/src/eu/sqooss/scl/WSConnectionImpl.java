package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;

/**
 * The class has package visibility.
 * The SCL's client can create the WSConnection objects only from the WSSession. 
 */
class WSConnectionImpl implements WSConnection {

    public void addEventListener(String url, WSEventListener listener) {
    }

    public WSResult addMetric(String url) {
        return null;
    }

    public WSResult getFileGroupMetricResult(long merticId, long projectId,
            int projectVersison) {
        return null;
    }

    public WSResult getValue(String url) {
        return null;
    }

    public void removeEventListener(String url, WSEventListener listener) {
    }

    public WSResult removeMetric(long metricId) {
        return null;
    }

}
