package eu.sqooss.impl.service.corba.alitheia.db;

import java.sql.Time;
import java.util.Date;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFileMeasurement;

public abstract class DAObject {

    public static eu.sqooss.service.db.DAObject fromCorbaObject(org.omg.CORBA.Any object) {
        return null;
    }

    public static ProjectFileMeasurement fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement m) {
        ProjectFileMeasurement measurement = new ProjectFileMeasurement();
        measurement.setMetric(fromCorbaObject(m.metric));
        measurement.setProjectFile(fromCorbaObject(m.projectFile));
        measurement.setWhenRun(Time.valueOf(m.whenRun));
        measurement.setResult(m.result);
        return measurement;
    }

    public static Metric fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Metric m) {
        Metric metric = new Metric();
        metric.setId(m.id);
        metric.setPlugin(fromCorbaObject(m.plugin));
        metric.setMetricType(fromCorbaObject(m.metricType));
        metric.setDescription(m.description);
        return metric;
    }

    public static MetricType.Type fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricTypeType type) {
        switch (type.value()) {
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._SourceCode:
            return MetricType.Type.SOURCE_CODE;
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._MailingList:
            return MetricType.Type.MAILING_LIST;
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._BugDatabase:
            return MetricType.Type.BUG_DATABASE;
        }
        return null;
    }

    public static MetricType fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricType type) {
        MetricType metricType = new MetricType();
        metricType.setId(type.id);
        metricType.setEnumType(fromCorbaObject(type.type));
        return metricType;
    }

    public static Plugin fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Plugin p) {
        Plugin plugin = new Plugin();
        plugin.setId(p.id);
        plugin.setName(p.name);
        plugin.setInstalldate(new Date(Date.parse(p.installdate)));
        return plugin;
    }

    public static FileGroup fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.FileGroup group) {
        FileGroup fileGroup = new FileGroup();
        fileGroup.setId(group.id);
        fileGroup.setName(group.name);
        fileGroup.setSubPath(group.subPath);
        fileGroup.setRegex(group.regex);
        fileGroup.setRecalcFreq(group.recalcFreq);
        fileGroup.setLastUsed(Time.valueOf(group.lastUsed));
        fileGroup.setProjectVersion(fromCorbaObject(group.projectVersion));
        return fileGroup;
    }

    public static StoredProject fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.StoredProject project) {
        StoredProject storedProject = new StoredProject();
        storedProject.setId(project.id);
        storedProject.setName(project.name);
        storedProject.setWebsite(project.website);
        storedProject.setContact(project.contact);
        storedProject.setBugs(project.bugs);
        storedProject.setRepository(project.repository);
        storedProject.setMail(project.mail);
        return storedProject;
    }

    public static ProjectVersion fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersion version) {
        ProjectVersion projectVersion = new ProjectVersion();
        projectVersion.setId(version.id);
        projectVersion.setProject(fromCorbaObject(version.project));
        projectVersion.setVersion(version.version);
        projectVersion.setTimestamp(version.timeStamp);
        return projectVersion;
    }

    public static ProjectFile fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFile file) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setId(file.id);
        projectFile.setName(file.name);
        projectFile.setProjectVersion(fromCorbaObject(file.projectVersion));
        projectFile.setStatus(file.status);
        return projectFile;
    }
}
