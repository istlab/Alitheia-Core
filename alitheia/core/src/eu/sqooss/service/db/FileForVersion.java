package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.CoreActivator;

public class FileForVersion extends DAObject {
    
    ProjectFile file;
    ProjectVersion version;
    
    public FileForVersion() {
        
    }
    
    public FileForVersion (ProjectFile from, ProjectVersion pv) {
        file = from;
        version = pv;
    }
    
    public ProjectFile getFile() {
        return file;
    }
    
    public void setFile(ProjectFile file) {
        this.file = file;
    }
    
    public ProjectVersion getVersion() {
        return version;
    }
    
    public void setVersion(ProjectVersion version) {
        this.version = version;
    }
    
    public static FileForVersion getFileForVersion(ProjectFile pf, ProjectVersion pv) {
        
        DBService dbs = CoreActivator.getDBService();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("file", pf);
        params.put("version", pv);

        List<FileForVersion> ffv = 
            (List<FileForVersion>) dbs.findObjectsByProperties(FileForVersion.class, params);
        
        if (ffv.isEmpty() || ffv == null) {
            return null;
        } else {
            return ffv.get(0);
        }
    }
    
    public static List<ProjectFile> getFilesForVersion(ProjectVersion pv) {
        
        DBService dbs = CoreActivator.getDBService();
        String paramVersion = "paramVersion";
        
        String query = "select pf " +
            "from ProjectFile pf, ProjectVersion pv, FileForVersion ffv" +
            " where pf = ffv.file " +
            " and pv = ffv.version " +
            " and ffv.version = :" + paramVersion; 

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramVersion, pv);
        
        return (List<ProjectFile>) dbs.doHQL(query, parameters);

    }
    
    public String toString() {
        return file.toString();
    }
}
