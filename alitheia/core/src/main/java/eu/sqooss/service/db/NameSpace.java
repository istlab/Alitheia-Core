package eu.sqooss.service.db;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;

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
 * Namespace with the validUntil field equal to null means that it is current.
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NAMESPACE_ID")
    @XmlElement
    long id;

    /** The namespace name */
    @Column(name = "NAME")
    @XmlElement
    String name;

    /** Version since this namespace instance is valid */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VALID_FROM_ID")
    ProjectVersion validFrom;

    /** Version until this namespace instance is valid */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VALID_UNTIL_ID")
    ProjectVersion validUntil;

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

    public ProjectVersion getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(ProjectVersion validFrom) {
        this.validFrom = validFrom;
    }

    public ProjectVersion getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(ProjectVersion validUntil) {
        this.validUntil = validUntil;
    }

    public Set<EncapsulationUnit> getEncapsulationUnits() {
        return encapsulationUnits;
    }

    public void setEncapsulationUnits(Set<EncapsulationUnit> encapsulationUnits) {
        this.encapsulationUnits = encapsulationUnits;
    }

    public Set<ExecutionUnit> getExecutionUnits() {
        return executionUnits;
    }

    public void setExecutionUnits(Set<ExecutionUnit> executionUnits) {
        this.executionUnits = executionUnits;
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    
}
