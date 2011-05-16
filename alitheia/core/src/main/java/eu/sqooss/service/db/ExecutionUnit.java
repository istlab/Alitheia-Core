package eu.sqooss.service.db;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an executable program entity, aka a function in functional and
 * procedural languages or a method in object oriented languages.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
@XmlRootElement(name = "execution-unit")
@Entity
@Table(name = "EXECUTION_UNIT")
public class ExecutionUnit extends DAObject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EXECUTION_UNIT_ID")
    @XmlElement
    private long id;

    @Column(name = "NAME", length = 256)
    @XmlElement
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NAMESPACE_ID")
    private NameSpace namespace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENCAPSULATION_UNIT_ID")
    private EncapsulationUnit encapsulationUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_FILE_ID")
    private ProjectFile file;

    @Column(name = "CHANGED")
    @XmlElement
    private boolean changed = false;
    
    /**
     * Measurements for this execution unit. Only execution units whose
     * <code>changed</code> field is true can have measurements.
     */
    @OneToMany(mappedBy = "executionUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ExecutionUnitMeasurement> measurements;

    public ExecutionUnit() {}
    
    public ExecutionUnit(EncapsulationUnit eu) {
        this.encapsulationUnit = eu;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NameSpace getNamespace() {
        return namespace;
    }

    public void setNamespace(NameSpace namespace) {
        this.namespace = namespace;
    }

    public EncapsulationUnit getEncapsulationUnit() {
        return encapsulationUnit;
    }

    public void setEncapsulationUnit(EncapsulationUnit encapsulationUnit) {
        this.encapsulationUnit = encapsulationUnit;
    }

    public void setFile(ProjectFile file) {
        this.file = file;
    }

    public ProjectFile getFile() {
        return file;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setMeasurements(Set<ExecutionUnitMeasurement> measurements) {
        this.measurements = measurements;
    }

    public Set<ExecutionUnitMeasurement> getMeasurements() {
        return measurements;
    }
    
    public String getFullyQualifiedName() {
        return toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(encapsulationUnit.toString());
        sb.append("::");
        sb.append(name);
        
        return sb.toString();
    }
}
