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
@Table(name="ENCAPSULATION_UNIT_MEASUREMENT")
@XmlRootElement(name="encu-measurement")
public class EncapsulationUnitMeasurement extends MetricMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ENCAPSULATION_UNIT_MEASUREMENT_ID")
    @XmlElement(name = "id")
    private long id; 

    /**
     * The encapsulation unit against which the measurement was made
     */ 
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ENCAPSULATION_UNIT_ID", referencedColumnName = "ENCAPSULATION_UNIT_ID")
    private EncapsulationUnit encapsulationUnit;
    
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

    public EncapsulationUnitMeasurement(){}
    
    public EncapsulationUnitMeasurement(EncapsulationUnit eu, Metric m, String result) {
        this.encapsulationUnit = eu;
        this.metric = m;
        this.result = result;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EncapsulationUnit getEncapsulationUnit() {
        return encapsulationUnit;
    }

    public void setEncapsulationUnit(EncapsulationUnit encapsulationUnit) {
        this.encapsulationUnit = encapsulationUnit;
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
