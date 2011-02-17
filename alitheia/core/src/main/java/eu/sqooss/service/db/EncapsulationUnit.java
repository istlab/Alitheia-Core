package eu.sqooss.service.db;

import java.util.List;

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
 * A unit of data encapsulation. Until further notice, this only makes sense in
 * object oriented languages, where it represents a class. An encapsulation unit
 * is linked to a namespace (by a namespace/package declaration, depending on
 * the language) and a file (where it is defined).
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
@XmlRootElement(name = "encapsulation-unit")
@Entity
@Table(name = "ENCAPSULATION_UNIT")
public class EncapsulationUnit extends DAObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ENCAPSULATION_UNIT_ID")
    @XmlElement
    long id;

    @Column(name = "NAME")
    @XmlElement
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NAMESPACE_ID")
    private NameSpace namespace;

    @OneToMany(mappedBy = "encapsulationUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExecutionUnit> execUnits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_FILE_ID")
    private ProjectFile file;

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

    public List<ExecutionUnit> getExecUnits() {
        return execUnits;
    }

    public void setExecUnits(List<ExecutionUnit> execUnits) {
        this.execUnits = execUnits;
    }

    public void setFile(ProjectFile file) {
        this.file = file;
    }

    public ProjectFile getFile() {
        return file;
    }
}
