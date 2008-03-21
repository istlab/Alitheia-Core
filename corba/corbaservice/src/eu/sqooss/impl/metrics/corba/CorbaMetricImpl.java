package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.MetricType;

import eu.sqooss.impl.service.corba.alitheia.*;

abstract public class CorbaMetricImpl extends AbstractMetric {

    eu.sqooss.impl.service.corba.alitheia.AbstractMetric m;

    public CorbaMetricImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.AbstractMetric m) {
        super(bc);
        this.m = m;
    }

    public boolean doAddSupportedMetrics(String desc, MetricType.Type type) {
        return addSupportedMetrics(desc, type);
    }

    public String getAuthor() {
        return m.getAuthor();
    }

    public String getDescription() {
        return m.getDescription();
    }

    public String getName() {
        return m.getName();
    }

    public String getVersion() {
        return m.getVersion();
    }

    public boolean install() {
        return super.install();
    }

    public boolean remove() {
        return super.remove();
    }

    public boolean update() {
        // TODO Auto-generated method stub
        return false;
    }
   
    protected static FileGroup fromDBObject(eu.sqooss.service.db.FileGroup o) {
        FileGroup group = new FileGroup();
        group.id = (int)o.getId();
        group.name = o.getName();
        group.subPath = o.getSubPath();
        group.regex = o.getRegex();
        group.recalcFreq = o.getRecalcFreq();
        group.lastUsed = o.getLastUsed().toString();
        group.projectVersion = fromDBObject(o.getProjectVersion());
        return group;
    }

    protected static StoredProject fromDBObject(eu.sqooss.service.db.StoredProject o) {
        StoredProject project = new StoredProject();
        project.id = (int)o.getId();
        project.name = o.getName();
        project.website = o.getWebsite();
        project.contact = o.getContact();
        project.bugs = o.getBugs();
        project.repository = o.getRepository();
        project.mail = o.getMail();
        return project;
    }

    protected static ProjectVersion fromDBObject(eu.sqooss.service.db.ProjectVersion o) {
        ProjectVersion version = new ProjectVersion();
        version.id = (int)o.getId();
        version.project = fromDBObject(o.getProject());
        version.version = (int)o.getVersion();
        version.timeStamp = (int)o.getTimestamp();
        return version;
    }

    protected static ProjectFile fromDBObject(eu.sqooss.service.db.ProjectFile o) {
        ProjectFile file = new ProjectFile();
        file.id = (int)o.getId();
        file.name = o.getName();
        file.status = o.getStatus();
        file.projectVersion = fromDBObject(o.getProjectVersion());
        return file;
    }
}
