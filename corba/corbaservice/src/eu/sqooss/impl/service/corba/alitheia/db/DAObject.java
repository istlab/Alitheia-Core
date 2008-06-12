package eu.sqooss.impl.service.corba.alitheia.db;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCodePackage.BadKind;

import eu.sqooss.impl.service.corba.alitheia.DeveloperHelper;
import eu.sqooss.impl.service.corba.alitheia.DirectoryHelper;
import eu.sqooss.impl.service.corba.alitheia.FileGroupHelper;
import eu.sqooss.impl.service.corba.alitheia.MetricHelper;
import eu.sqooss.impl.service.corba.alitheia.MetricTypeHelper;
import eu.sqooss.impl.service.corba.alitheia.PluginConfigurationHelper;
import eu.sqooss.impl.service.corba.alitheia.PluginHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurementHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurementHelper;
import eu.sqooss.impl.service.corba.alitheia.ResultEntry;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectHelper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;

/**
 * Helper class providing mappings between Alitheia's DAObject
 * and the Corba version.
 * @author Christoph Schleifenbaum, KDAB
 */
public abstract class DAObject {

    // this one is set in DbImpl's c'tor
    protected static DBService db = null;
    
    /**
     * Gets an Alitheia class from a type of a CORBA.Any object.
     * The actual content of the Any object doesn't matter.
     * @param type A CORBA.Any object which type is wanted.
     * @return The corresponding java.lang.Class
     */
    public static Class<?> fromCorbaType(Any type) {
        String typeName = "";
        try {
            typeName = type.type().name();
            return Class.forName("eu.sqooss.service.db." + typeName);
        } catch (BadKind e) {
            if (type.type().kind().value() == TCKind.tk_long.value() )
                return Long.class;
            else if (type.type().kind().value() == TCKind.tk_boolean.value() )
                return Boolean.class;
            else if (type.type().kind().value() == TCKind.tk_string.value() )
                return String.class;
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
     
    /**
     * Gets an object from the DB or creates a new one of the corresponding type.
     * @param <T> The type
     * @param daoClass The class type the object is of.
     * @param id If 0, a new object is generated. Otherwise this method tries to get it from the DB.
     * @return The DAObject if id was 0 or it could be loaded from the DB. null, otherwise.
     */
    public static <T extends eu.sqooss.service.db.DAObject> T getOrCreateObject( Class<T> daoClass, int id )
    {
        T result = null;
        if (id != 0) {
            result = db.findObjectById(daoClass, id);
        } else {
            try {
                result = daoClass.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return result;
    }
    
    public static eu.sqooss.service.abstractmetric.ResultEntry fromCorbaObject(ResultEntry object) {
    	return eu.sqooss.service.abstractmetric.ResultEntry.fromString(object.value, 
    			object.mimeType, 
    			object.mnemonic);
    }
    
    public static ResultEntry toCorbaObject(eu.sqooss.service.abstractmetric.ResultEntry object) {
    	ResultEntry result = new ResultEntry();
    	result.value = object.toString();
    	result.mimeType = object.getMimeType();
    	result.mnemonic = object.getMnemonic();
    	return result;
    }
    
	/**
	 * Translates a Corba DBObject into the Alitheia format.
	 * @param object The corba object to be translated.
	 * @return A reference to the Alitheia style DAObject.
	 */
    public static eu.sqooss.service.db.DAObject fromCorbaObject(Any object) {
        return fromCorbaObject(eu.sqooss.service.db.DAObject.class, object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromCorbaObject(Class<T> c, Any object) {
        if (object.type().kind().value() == TCKind.tk_long.value() ) {
            return (T) new Long(object.extract_long() );
        }
        else if (object.type().kind().value() == TCKind.tk_string.value() ) {
            return (T) object.extract_string();
        }
        else if (object.type().kind().value() == TCKind.tk_boolean.value() ) {
            return (T) new Boolean(object.extract_boolean() );
        }
        try
        {
            String type = object.type().name();
            if (type.equals("ProjectFile") )
            {
                return (T) fromCorbaObject(ProjectFileHelper.extract(object));
            }
            else if (type.equals("ProjectVersion") )
            {
                return (T) fromCorbaObject(ProjectVersionHelper.extract(object));
            }
            else if (type.equals("StoredProject") )
            {
                return (T) fromCorbaObject(StoredProjectHelper.extract(object));
            }
            else if (type.equals("FileGroup") )
            {
                return (T) fromCorbaObject(FileGroupHelper.extract(object));
            }
            else if (type.equals("Plugin") )
            {
                return (T) fromCorbaObject(PluginHelper.extract(object));
            }
            else if (type.equals("PluginConfiguration") )
            {
                return (T) fromCorbaObject(PluginConfigurationHelper.extract(object));
            }
            else if (type.equals("MetricType") )
            {
                return (T) fromCorbaObject(MetricTypeHelper.extract(object));
            }
            else if (type.equals("Metric") )
            {
                return (T) fromCorbaObject(MetricHelper.extract(object));
            }
            else if (type.equals("Developer") )
            {
                return (T) fromCorbaObject(DeveloperHelper.extract(object));
            }
            else if (type.equals("Directory") )
            {
                return (T) fromCorbaObject(DirectoryHelper.extract(object));
            }
            else if (type.equals("ProjectFileMeasurement") )
            {
                return (T) fromCorbaObject(ProjectFileMeasurementHelper.extract(object));
            }
            else if (type.equals("ProjectVersionMeasurement") )
            {
                return (T) fromCorbaObject(ProjectVersionMeasurementHelper.extract(object));
            }
        }
        catch( BadKind e )
        {
        }
        return null;
    }

    /**
     * Translates an Alitheia DAObject into a Corba DAObject
     * @param An Alitheia style DAObject
     * @return A corba Any object containing the object.
     */
    public static Any toCorbaObject(eu.sqooss.service.db.DAObject obj) {
        Any result = ORB.init().create_any();
        if (obj instanceof eu.sqooss.service.db.ProjectFile)
            eu.sqooss.impl.service.corba.alitheia.ProjectFileHelper.insert(result, toCorbaObject((eu.sqooss.service.db.ProjectFile)obj));
        else if (obj instanceof eu.sqooss.service.db.ProjectVersion)
            eu.sqooss.impl.service.corba.alitheia.ProjectVersionHelper.insert(result, toCorbaObject((eu.sqooss.service.db.ProjectVersion)obj));
        else if (obj instanceof eu.sqooss.service.db.StoredProject)
            eu.sqooss.impl.service.corba.alitheia.StoredProjectHelper.insert(result, toCorbaObject((eu.sqooss.service.db.StoredProject)obj));
        else if (obj instanceof eu.sqooss.service.db.FileGroup)
            eu.sqooss.impl.service.corba.alitheia.FileGroupHelper.insert(result, toCorbaObject((eu.sqooss.service.db.FileGroup)obj));
        else if (obj instanceof eu.sqooss.service.db.Plugin)
            eu.sqooss.impl.service.corba.alitheia.PluginHelper.insert(result, toCorbaObject((eu.sqooss.service.db.Plugin)obj));
        else if (obj instanceof eu.sqooss.service.db.PluginConfiguration)
            eu.sqooss.impl.service.corba.alitheia.PluginConfigurationHelper.insert(result, toCorbaObject((eu.sqooss.service.db.PluginConfiguration)obj));
        else if (obj instanceof eu.sqooss.service.db.MetricType)
            eu.sqooss.impl.service.corba.alitheia.MetricTypeHelper.insert(result, toCorbaObject((eu.sqooss.service.db.MetricType)obj));
        else if (obj instanceof eu.sqooss.service.db.Metric)
            eu.sqooss.impl.service.corba.alitheia.MetricHelper.insert(result, toCorbaObject((eu.sqooss.service.db.Metric)obj));
        else if (obj instanceof eu.sqooss.service.db.Developer)
            eu.sqooss.impl.service.corba.alitheia.DeveloperHelper.insert(result, toCorbaObject((eu.sqooss.service.db.Developer)obj));
        else if (obj instanceof eu.sqooss.service.db.Directory)
            eu.sqooss.impl.service.corba.alitheia.DirectoryHelper.insert(result, toCorbaObject((eu.sqooss.service.db.Directory)obj));
        else if (obj instanceof eu.sqooss.service.db.ProjectFileMeasurement)
            eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurementHelper.insert(result, toCorbaObject((eu.sqooss.service.db.ProjectFileMeasurement)obj));
        else if (obj instanceof eu.sqooss.service.db.ProjectVersionMeasurement)
            eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurementHelper.insert(result, toCorbaObject((eu.sqooss.service.db.ProjectVersionMeasurement)obj));
        return result;
    }
    
    // use ISO date time format
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static DateFormat otherParsingDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Formats a date as ISO date time format.
     */
    private static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Parses a date from ISO date time format.
     */
    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch( Exception e ) {
        }
        // try another version...
        try {
             return otherParsingDateFormat.parse(date);
        } catch( Exception e ) {
        }
        return new Date();
    }

    /**
     * Translates an Alitheia-ProjectFileMeasurement into a Corba one. 
     */
    public static eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement toCorbaObject(ProjectFileMeasurement m) {
        eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement measurement = new eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement();
        measurement.id = (int)m.getId();
        measurement.measureMetric = toCorbaObject(m.getMetric());
        measurement.file = toCorbaObject(m.getProjectFile());
        measurement.whenRun = formatDate(m.getWhenRun());
        measurement.result = m.getResult() == null ? "" : m.getResult();
        return measurement;
    }

    /**
     * Translates a Corba-ProjectFileMeasurement into an Alitheia one.
     */
    public static ProjectFileMeasurement fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFileMeasurement m) {
        ProjectFileMeasurement measurement = getOrCreateObject(ProjectFileMeasurement.class, m.id);
        measurement.setId(m.id);
        measurement.setMetric(fromCorbaObject(m.measureMetric));
        measurement.setProjectFile(fromCorbaObject(m.file));
        measurement.setWhenRun(new Timestamp(parseDate(m.whenRun).getTime()));
        measurement.setResult(m.result);
        return measurement;
    }

    /**
     * Translates an Alitheia ProjectVersionMeasurement into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement toCorbaObject(ProjectVersionMeasurement m) {
        eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement measurement = new eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement();
        measurement.id = (int)m.getId();
        measurement.measureMetric = toCorbaObject(m.getMetric());
        measurement.version = toCorbaObject(m.getProjectVersion());
        measurement.whenRun = formatDate(m.getWhenRun());
        measurement.result = m.getResult() == null ? "" : m.getResult();
        return measurement;
    }

    /**
     * Translates a Corba-ProjectVersionMeasurement into an Alitheia one.
     */
    public static ProjectVersionMeasurement fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersionMeasurement m) {
        ProjectVersionMeasurement measurement = getOrCreateObject(ProjectVersionMeasurement.class, m.id);
        measurement.setId(m.id);
        measurement.setMetric(fromCorbaObject(m.measureMetric));
        measurement.setProjectVersion(fromCorbaObject(m.version));
        measurement.setWhenRun(new Timestamp(parseDate(m.whenRun).getTime()));
        measurement.setResult(m.result);
        return measurement;
    }

    /**
     * Translates an Alitheia Metric into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.Metric toCorbaObject(Metric m) {
        eu.sqooss.impl.service.corba.alitheia.Metric metric = new eu.sqooss.impl.service.corba.alitheia.Metric();
        metric.id = (int)m.getId();
        metric.metricPlugin = toCorbaObject(m.getPlugin());
        metric.type = toCorbaObject(m.getMetricType());
        metric.mnemonic = m.getMnemonic();
        metric.description = m.getDescription();
        return metric;
    }

    /**
     * Translates a Corba-Metric into an Alitheia one.
     */
    public static Metric fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Metric m) {
        Metric metric = getOrCreateObject(Metric.class, m.id);
        metric.setId(m.id);
        metric.setPlugin(fromCorbaObject(m.metricPlugin));
        metric.setMetricType(fromCorbaObject(m.type));
        metric.setMnemonic(m.mnemonic);
        metric.setDescription(m.description);
        return metric;
    }

    /**
     * Translates an Alitheia MetricType.Type into a Corba one.
     */
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

   /**
    * Translates a Corba-MetricTypeType into an Alitheia one.
    */
    public static MetricType.Type fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricTypeType type) {
        switch (type.value()) {
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._SourceCode:
            return MetricType.Type.SOURCE_CODE;
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._MailingList:
            return MetricType.Type.MAILING_LIST;
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._BugDatabase:
            return MetricType.Type.BUG_DATABASE;
        case eu.sqooss.impl.service.corba.alitheia.MetricTypeType._ProjectWide:
            return MetricType.Type.PROJECT_WIDE;
        }
        return null;
    }

    /**
     * Translates an Alitheia MetricType into a Corba one.
     */
   public static eu.sqooss.impl.service.corba.alitheia.MetricType toCorbaObject(MetricType type) {
        eu.sqooss.impl.service.corba.alitheia.MetricType metricType = new eu.sqooss.impl.service.corba.alitheia.MetricType();
        metricType.id = (int)type.getId();
        metricType.type = toCorbaObject(type.getEnumType());
        return metricType;
    }

   /**
    * Translates a Corba-MetricType into an Alitheia one.
    */
    public static MetricType fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.MetricType type) {
        MetricType metricType = getOrCreateObject(MetricType.class, type.id);
        metricType.setId(type.id);
        metricType.setEnumType(fromCorbaObject(type.type));
        return metricType;
    }

    /**
     * Translates an Alitheia Plugin into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.Plugin toCorbaObject(Plugin p) {
        eu.sqooss.impl.service.corba.alitheia.Plugin plugin = new eu.sqooss.impl.service.corba.alitheia.Plugin();
        plugin.id = (int)p.getId();
        plugin.name = p.getName() == null ? "" : p.getName();
        plugin.installdate = formatDate(p.getInstalldate());
        return plugin;
    }

    /**
     * Translates a Corba-Plugin into an Alitheia one.
     */
    public static Plugin fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Plugin p) {
        Plugin plugin = getOrCreateObject(Plugin.class, p.id);
        plugin.setId(p.id);
        plugin.setName(p.name);
        plugin.setInstalldate(parseDate((p.installdate)));
        return plugin;
    }

    /**
     * Translates an Alitheia Plugin into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.PluginConfiguration toCorbaObject(PluginConfiguration p) {
        eu.sqooss.impl.service.corba.alitheia.PluginConfiguration plugin = new eu.sqooss.impl.service.corba.alitheia.PluginConfiguration();
        plugin.id = (int)p.getId();
        plugin.name = p.getName() == null ? "" : p.getName();
        plugin.value = p.getValue() == null ? "" : p.getValue();
        plugin.type = p.getType() == null ? "" : p.getType();
        plugin.msg = p.getMsg() == null ? "" : p.getMsg();
        plugin.metricPlugin = p.getPlugin() == null ? new eu.sqooss.impl.service.corba.alitheia.Plugin() : toCorbaObject(p.getPlugin());
        return plugin;
    }

    /**
     * Translates a Corba-Plugin into an Alitheia one.
     */
    public static PluginConfiguration fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.PluginConfiguration p) {
        PluginConfiguration plugin = getOrCreateObject(PluginConfiguration.class, p.id);
        plugin.setId(p.id);
        plugin.setName(p.name);
        plugin.setValue(p.value);
        plugin.setType(p.type);
        plugin.setMsg(p.msg);
        plugin.setPlugin(fromCorbaObject(p.metricPlugin));
        return plugin;
    }

    /**
     * Translates an Alitheia FileGroup into a Corba one.
     */
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

    /**
     * Translates a Corba-FileGroup into an Alitheia one.
     */
    public static FileGroup fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.FileGroup group) {
        FileGroup fileGroup = getOrCreateObject(FileGroup.class, group.id);
        fileGroup.setId(group.id);
        fileGroup.setName(group.name);
        fileGroup.setSubPath(group.subPath);
        fileGroup.setRegex(group.regex);
        fileGroup.setRecalcFreq(group.recalcFreq);
        fileGroup.setLastUsed(new Time(parseDate(group.lastUsed).getTime()));
        fileGroup.setProjectVersion(fromCorbaObject(group.version));
        return fileGroup;
    }

    /**
     * Translates an Alitheia StoredProject into a Corba one.
     */
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

    /**
     * Translates a Corba-StoredProject into an Alitheia one.
     */
    public static StoredProject fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.StoredProject project) {
        StoredProject storedProject = getOrCreateObject(StoredProject.class, project.id);
        storedProject.setId(project.id);
        storedProject.setName(project.name);
        storedProject.setWebsite(project.website);
        storedProject.setContact(project.contact);
        storedProject.setBugs(project.bugs);
        storedProject.setRepository(project.repository);
        storedProject.setMail(project.mail);
        return storedProject;
    }

    /**
     * Translates an Alitheia ProjectVersion into a Corba one.
     */
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

    /**
     * Translates a Corba-ProjectVersion into an Alitheia one.
     */
    public static ProjectVersion fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectVersion version) {
        ProjectVersion projectVersion = getOrCreateObject(ProjectVersion.class, version.id);
        projectVersion.setId(version.id);
        projectVersion.setProject(fromCorbaObject(version.project));
        projectVersion.setVersion(version.version);
        projectVersion.setTimestamp(version.timeStamp);
        projectVersion.setCommitter(fromCorbaObject(version.committer));
        projectVersion.setCommitMsg(version.commitMsg);
        projectVersion.setProperties(version.properties);
        return projectVersion;
    }

    /**
     * Translates an Alitheia ProjectFile into a Corba one.
     */
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

    /**
     * Translates a Corba-ProjectFile into an Alitheia one.
     */
    public static ProjectFile fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.ProjectFile file) {
        ProjectFile projectFile = getOrCreateObject(ProjectFile.class, file.id);
        projectFile.setId(file.id);
        projectFile.setName(file.name);
        projectFile.setProjectVersion(fromCorbaObject(file.version));
        projectFile.setStatus(file.status);
        projectFile.setIsDirectory(file.isDirectory);
        projectFile.setDir(fromCorbaObject(file.dir));
        return projectFile;
    }

    /**
     * Translates an Alitheia Developer into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.Developer toCorbaObject(Developer dev) {
        eu.sqooss.impl.service.corba.alitheia.Developer developer = new eu.sqooss.impl.service.corba.alitheia.Developer();
        developer.id = (int)dev.getId();
        developer.name = dev.getName() == null ? "" : dev.getName();
        developer.email = dev.getEmail() == null ? "" : dev.getEmail();
        developer.username = dev.getUsername();
        developer.project = toCorbaObject(dev.getStoredProject());
        return developer;
    }

    /**
     * Translates a Corba-Developer into an Alitheia one.
     */
    public static Developer fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Developer dev) {
        Developer developer = getOrCreateObject(Developer.class, dev.id);
        developer.setId(dev.id);
        developer.setName(dev.name);
        developer.setEmail(dev.email);
        developer.setUsername(dev.username);
        developer.setStoredProject(fromCorbaObject(dev.project));
        return developer;
    }
    
    /**
     * Translates an Alitheia Directory into a Corba one.
     */
    public static eu.sqooss.impl.service.corba.alitheia.Directory toCorbaObject(Directory dir) {
        eu.sqooss.impl.service.corba.alitheia.Directory directory = new eu.sqooss.impl.service.corba.alitheia.Directory();
        directory.id = (int)dir.getId();
        directory.path = dir.getPath();
        return directory;
    }

    /**
     * Translates a Corba-Directory into an Alitheia one.
     */
    public static Directory fromCorbaObject(eu.sqooss.impl.service.corba.alitheia.Directory dir) {
        Directory directory = getOrCreateObject(Directory.class, dir.id);
        directory.setId(dir.id);
        directory.setPath(dir.path);
        return directory;
    }
}
