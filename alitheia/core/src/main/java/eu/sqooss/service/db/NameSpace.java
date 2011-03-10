package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;

import eu.sqooss.core.AlitheiaCore;

/**
 * A representation of a namespace. According to Wikipedia, a namespace is an
 * abstract container or environment created to hold a logical grouping of
 * unique identifiers or symbols (i.e., names). Its interpretation depends on
 * the language used. For languages with non-segmented namespaces (e.g. C,
 * Fortran) this represents the total codebase; in cases where the language has
 * explicit (C++, Python) or implicit (Java, C# with packages) namespace
 * support, this presents the codebase under the specified namespace.
 * 
 * A namespace has a lifetime; during the course of a project, as code changes,
 * so does the validity of the namespace. Everytime there is a source code
 * commit that affects a certain namespace, a new Namespace DB entry is added; a
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @assoc 1 - n EncapsulationUnit
 * @assoc 1 - n ExecutionUnit
 */
@XmlRootElement(name = "namespace")
@Entity
@Table(name = "NAMESPACE")
public class NameSpace extends DAObject {

    private static final String nsByVersion = 
    		"from NameSpace ns " +
    		"where ns.changeVersion = :pv " +
    		"and ns.name = :name";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NAMESPACE_ID")
    @XmlElement
    long id;

    /** The namespace name */
    @Column(name = "NAME")
    @XmlElement
    String name;

    /** Version until this namespace instance is valid */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANGE_VERSION_ID")
    ProjectVersion changeVersion;

    /** Encapsulation units, if any, */
    @OneToMany(mappedBy = "namespace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<EncapsulationUnit> encapsulationUnits;

    /** Encapsulation units belonging to this namespace */
    @OneToMany(mappedBy = "namespace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ExecutionUnit> executionUnits;

    /** The namespace language */
    @Enumerated(EnumType.STRING)
    @XmlElement
    @Index(name = "IDX_NAMESPACE_LANG")
    @Column(name = "LANG")
    Language lang;

    /** Measurements for this namespace*/
    @OneToMany(mappedBy = "namespace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<NameSpaceMeasurement> measurements;
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectVersion getChangeVersion() {
        return changeVersion;
    }

    public void setChangeVersion(ProjectVersion changeVersion) {
        this.changeVersion = changeVersion;
    }
    
    public Set<EncapsulationUnit> getEncapsulationUnits() {
        if (encapsulationUnits == null)
            encapsulationUnits = new HashSet<EncapsulationUnit>();
        return encapsulationUnits;
    }

    public void setEncapsulationUnits(Set<EncapsulationUnit> encapsulationUnits) {
        this.encapsulationUnits = encapsulationUnits;
    }

    public Set<ExecutionUnit> getExecutionUnits() {
        return executionUnits;
    }

    public void setExecutionUnits(Set<ExecutionUnit> executionUnits) {
        if (executionUnits == null)
            executionUnits = new HashSet<ExecutionUnit>();
        this.executionUnits = executionUnits;
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }
    
    public void setMeasurements(Set<NameSpaceMeasurement> measurements) {
        this.measurements = measurements;
    }

    public Set<NameSpaceMeasurement> getMeasurements() {
        return measurements;
    }
    
    public static NameSpace findByVersionName(ProjectVersion pv, String name) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pv", pv);
        params.put("name", name);
        
        List<NameSpace> ns = (List<NameSpace>) dbs.doHQL(nsByVersion, params);
        
        if (ns.isEmpty())
            return null;
        return ns.get(0);
    }
}
