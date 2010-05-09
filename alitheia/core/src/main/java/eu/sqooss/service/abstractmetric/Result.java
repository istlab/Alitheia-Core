package eu.sqooss.service.abstractmetric;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;

public class Result {

    public enum ResultType {
        STRING, 
        INTEGER,
        FLOAT,
        DOUBLE
    }
    
    private Long artifactId;
    private Long metricId;
    private Object result;
    private ResultType type;

    public Result() {}
    
    public Result(DAObject o, Metric m, Object result, ResultType type) {
        this.artifactId = o.getId();
        this.metricId = m.getId();
        this.result = result;
        this.type = type;
    }

    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }
}


