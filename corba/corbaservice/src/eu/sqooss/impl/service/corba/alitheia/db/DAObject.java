package eu.sqooss.impl.service.corba.alitheia.db;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersionMeasurement;

import org.omg.CORBA.TypeCodePackage.BadKind;

public abstract class DAObject {

    public static eu.sqooss.service.db.DAObject fromCorbaObject(org.omg.CORBA.Any object) {
        try
        {
            String type = object.type().name();
            if (type.equals("ProjectFile") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFileHelper.extract(object));
            }
            else if (type.equals("ProjectVersion") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersionHelper.extract(object));
            }
            else if (type.equals("StoredProject") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.StoredProjectHelper.extract(object));
            }
            else if (type.equals("FileGroup") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.FileGroupHelper.extract(object));
            }
            else if (type.equals("Plugin") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.PluginHelper.extract(object));
            }
            else if (type.equals("MetricType") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricTypeHelper.extract(object));
            }
            else if (type.equals("Metric") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricHelper.extract(object));
            }
            else if (type.equals("Developer") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.DeveloperHelper.extract(object));
            }
            else if (type.equals("Directory") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.DirectoryHelper.extract(object));
            }
            else if (type.equals("ProjectFileMeasurement") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurementHelper.extract(object));
            }
            else if (type.equals("ProjectVersionMeasurement") )
            {
                return fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurementHelper.extract(object));
            }
        }
        catch( org.omg.CORBA.TypeCodePackage.BadKind e )
        {
        }
        return null;
    }

    // use ISO date time format
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    private static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch( Exception e ) {
            return null;
        }
    }

    public static eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement toCorbaObject(ProjectFileMeasurement m) {
        eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement measurement = new eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement();
        measurement.id = (int)m.getId();
        measurement.measureMetric = toCorbaObject(m.getMetric());
        measurement.file = toCorbaObject(m.getProjectFile());
        measurement.whenRun = formatDate(m.getWhenRun());
        measurement.result = m.getResult() == null ? "" : m.getResult();
        return measurement;
    }

    public static ProjectFileMeasurement fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement m) {
        ProjectFileMeasurement measurement = new ProjectFileMeasurement();
        measurement.setId(m.id);
        measurement.setMetric(fromCorbaObject(m.measureMetric));
        measurement.setProjectFile(fromCorbaObject(m.file));
        measurement.setWhenRun(new Timestamp(parseDate(m.whenRun).getTime()));
        measurement.setResult(m.result);
        return measurement;
    }

    public static eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement toCorbaObject(ProjectVersionMeasurement m) {
        eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement measurement = new eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement();
        measurement.id = (int)m.getId();
        measurement.measureMetric = toCorbaObject(m.getMetric());
        measurement.version = toCorbaObject(m.getProjectVersion());
        measurement.whenRun = formatDate(m.getWhenRun());
        measurement.result = m.getResult() == null ? "" : m.getResult();
        return measurement;
    }

    public static ProjectVersionMeasurement fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement m) {
        ProjectVersionMeasurement measurement = new ProjectVersionMeasurement();
        measurement.setId(m.id);
        measurement.setMetric(fromCorbaObject(m.measureMetric));
        measurement.setProjectVersion(fromCorbaObject(m.version));
        measurement.setWhenRun(new Timestamp(parseDate(m.whenRun).getTime()));
        measurement.setResult(m.result);
        return measurement;
    }

    public static eu.sqooss.impl.service.corba.alitheia.Metric toCorbaObject(Metric m) {
        eu.sqooss.impl.service.corba.alitheia.Metric metric = new eu.sqooss.impl.service.corba.alitheia.Metric();
        metric.id = (int)m.getId();
        metric.metricPlugin = toCorbaObject(m.getPlugin());
        metric.type = toCorbaObject(m.getMetricType());
        metric.mnemonic = m.getMnemonic();
        metric.description = m.getDescription();
        return metric;
    }

    public static Metric fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Metric m) {
        Metric metric = new Metric();
        metric.setId(m.id);
        metric.setPlugin(fromCorbaObject(m.metricPlugin));
        metric.setMetricType(fromCorbaObject(m.type));
        metric.setMnemonic(m.mnemonic);
        metric.setDescription(m.description);
        return metric;
    }

    public static eu.sqooss.impl.service.corba.alitheia.MetricTypeType toCorbaObject(MetricType.Type type) {
        switch (type) {
        case SOURCE_CODE:
            return eu.sqooss.impl.service.corba.alitheia.MetricTypeType.SourceCode;
        case MAILING_LIST:
            return eu.sqooss.impl.service.corba.alitheia.MetricTypeType.MailingList;
        case BUG_DATABASE:
            return eu.sqooss.impl.service.corba.alitheia.MetricTypeType.BugDatabase;
        }
        return null;
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

    public static eu.sqooss.impl.service.corba.alitheia.MetricType toCorbaObject(MetricType type) {
        eu.sqooss.impl.service.corba.alitheia.MetricType metricType = new eu.sqooss.impl.service.corba.alitheia.MetricType();
        metricType.id = (int)type.getId();
        metricType.type = toCorbaObject(type.getEnumType());
        return metricType;
    }

    public static MetricType fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricType type) {
        MetricType metricType = new MetricType();
        metricType.setId(type.id);
        metricType.setEnumType(fromCorbaObject(type.type));
        return metricType;
    }

    public static eu.sqooss.impl.service.corba.alitheia.Plugin toCorbaObject(Plugin p) {
        eu.sqooss.impl.service.corba.alitheia.Plugin plugin = new eu.sqooss.impl.service.corba.alitheia.Plugin();
        plugin.id = (int)p.getId();
        plugin.name = p.getName() == null ? "" : p.getName();
        plugin.installdate = formatDate(p.getInstalldate());
        return plugin;
    }

    public static Plugin fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Plugin p) {
        Plugin plugin = new Plugin();
        plugin.setId(p.id);
        plugin.setName(p.name);
        plugin.setInstalldate(parseDate((p.installdate)));
        return plugin;
    }

    public static eu.sqooss.impl.service.corba.alitheia.FileGroup toCorbaObject(FileGroup group) {
        eu.sqooss.impl.service.corba.alitheia.FileGroup fileGroup = new eu.sqooss.impl.service.corba.alitheia.FileGroup();
        fileGroup.id = (int)group.getId();
        fileGroup.name = group.getName();
        fileGroup.subPath = group.getSubPath();
        fileGroup.regex = group.getRegex();
        fileGroup.recalcFreq = group.getRecalcFreq();
        fileGroup.lastUsed = formatDate(group.getLastUsed());
        fileGroup.version = toCorbaObject(group.getProjectVersion());
        return fileGroup;
    }

    public static FileGroup fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.FileGroup group) {
        FileGroup fileGroup = new FileGroup();
        fileGroup.setId(group.id);
        fileGroup.setName(group.name);
        fileGroup.setSubPath(group.subPath);
        fileGroup.setRegex(group.regex);
        fileGroup.setRecalcFreq(group.recalcFreq);
        fileGroup.setLastUsed(new Time(parseDate(group.lastUsed).getTime()));
        fileGroup.setProjectVersion(fromCorbaObject(group.version));
        return fileGroup;
    }

    public static eu.sqooss.impl.service.corba.alitheia.StoredProject toCorbaObject(StoredProject project) {
        eu.sqooss.impl.service.corba.alitheia.StoredProject storedProject = new eu.sqooss.impl.service.corba.alitheia.StoredProject();
        storedProject.id = (int)project.getId();
        storedProject.name = project.getName();
        storedProject.website = project.getWebsite();
        storedProject.contact = project.getContact();
        storedProject.bugs = project.getBugs();
        storedProject.repository = project.getRepository();
        storedProject.mail = project.getMail();
        return storedProject;
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

    public static eu.sqooss.impl.service.corba.alitheia.ProjectVersion toCorbaObject(ProjectVersion version ) {
        eu.sqooss.impl.service.corba.alitheia.ProjectVersion projectVersion = new eu.sqooss.impl.service.corba.alitheia.ProjectVersion();
        projectVersion.id = (int)version.getId();
        projectVersion.project = toCorbaObject(version.getProject());
        projectVersion.version = (int)version.getVersion();
        projectVersion.timeStamp = (int)version.getTimestamp();
        projectVersion.committer = toCorbaObject(version.getCommitter());
        projectVersion.commitMsg = version.getCommitMsg();
        projectVersion.properties = version.getProperties() == null ? "" : version.getProperties();
        return projectVersion;
    }

    public static ProjectVersion fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersion version) {
        ProjectVersion projectVersion = new ProjectVersion();
        projectVersion.setId(version.id);
        projectVersion.setProject(fromCorbaObject(version.project));
        projectVersion.setVersion(version.version);
        projectVersion.setTimestamp(version.timeStamp);
        projectVersion.setCommitter(fromCorbaObject(version.committer));
        projectVersion.setCommitMsg(version.commitMsg);
        projectVersion.setProperties(version.properties);
        return projectVersion;
    }

    public static eu.sqooss.impl.service.corba.alitheia.ProjectFile toCorbaObject(ProjectFile file) {
        eu.sqooss.impl.service.corba.alitheia.ProjectFile projectFile = new eu.sqooss.impl.service.corba.alitheia.ProjectFile();
        projectFile.id = (int)file.getId();
        projectFile.name = file.getName();
        projectFile.version = toCorbaObject(file.getProjectVersion());
        projectFile.status = file.getStatus();
        projectFile.isDirectory = file.getIsDirectory();
        projectFile.dir = toCorbaObject(file.getDir());
        return projectFile;
    }

    public static ProjectFile fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFile file) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setId(file.id);
        projectFile.setName(file.name);
        projectFile.setProjectVersion(fromCorbaObject(file.version));
        projectFile.setStatus(file.status);
        projectFile.setIsDirectory(file.isDirectory);
        projectFile.setDir(fromCorbaObject(file.dir));
        return projectFile;
    }

    public static eu.sqooss.impl.service.corba.alitheia.Developer toCorbaObject(Developer dev) {
        eu.sqooss.impl.service.corba.alitheia.Developer developer = new eu.sqooss.impl.service.corba.alitheia.Developer();
        developer.id = (int)dev.getId();
        developer.name = dev.getName() == null ? "" : dev.getName();
        developer.email = dev.getEmail() == null ? "" : dev.getEmail();
        developer.username = dev.getUsername();
        developer.project = toCorbaObject(dev.getStoredProject());
        return developer;
    }

    public static Developer fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Developer dev) {
        Developer developer = new Developer();
        developer.setId(dev.id);
        developer.setName(dev.name);
        developer.setEmail(dev.email);
        developer.setUsername(dev.username);
        developer.setStoredProject(fromCorbaObject(dev.project));
        return developer;
    }
    
    public static eu.sqooss.impl.service.corba.alitheia.Directory toCorbaObject(Directory dir) {
        eu.sqooss.impl.service.corba.alitheia.Directory directory = new eu.sqooss.impl.service.corba.alitheia.Directory();
        directory.id = (int)dir.getId();
        directory.path = dir.getPath();
        return directory;
    }

    public static Directory fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Directory dir) {
        Directory directory = new Directory();
        directory.setId(dir.id);
        directory.setPath(dir.path);
        return directory;
    }
}
