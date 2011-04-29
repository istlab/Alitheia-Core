package eu.sqooss.service.db;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Instances of this class represent a measurement made against a
 * encapsulation unit version, as stored in the database
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
@Entity
@Table(name="EXECUTION_UNIT_MEASUREMENT")
@XmlRootElement(name="execu-measurement")
public class ExecutionUnitMeasurement extends MetricMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EXECUTION_UNIT_MEASUREMENT_ID")
    @XmlElement(name = "id")
    private long id; 

    /**
     * The encapsulation unit against which the measurement was made
     */ 
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "EXECUTION_UNIT_ID", referencedColumnName = "EXECUTION_UNIT_ID")
    private ExecutionUnit executionUnit;
    
    /**
     * The metric to which this result belongs
     */
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="METRIC_ID", referencedColumnName="METRIC_ID")
    private Metric metric;

    /**
     * A representation of the calculation result
     */
    @Column(name="RESULT")
    private String result;

    public ExecutionUnitMeasurement() {}
    
    public ExecutionUnitMeasurement(ExecutionUnit eu, Metric m, String result) {
        this.executionUnit = eu;
        this.metric = m;
        this.result = result;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExecutionUnit getExecutionUnit() {
        return executionUnit;
    }

    public void setExecutionUnit(ExecutionUnit executionUnit) {
        this.executionUnit = executionUnit;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
