package eu.sqooss.service.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProjectVersionParentId implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProjectVersionParentId() {}
    
    @Column(name = "PARENT_ID")
    private Long parentid;

    @Column(name = "CHILD_ID")
    private Long childid;

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Long getChildid() {
        return childid;
    }

    public void setChildid(Long childid) {
        this.childid = childid;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectVersionParentId))
            return false;
        ProjectVersionParentId other = (ProjectVersionParentId) obj;
        if (childid == other.childid && parentid == other.parentid)
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + parentid.intValue();
        hash = hash * 31 + childid.intValue();
        return hash;
    }
}