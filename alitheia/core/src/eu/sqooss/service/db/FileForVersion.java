package eu.sqooss.service.db;

public class FileForVersion extends DAObject {
    
    ProjectFile file;
    ProjectVersion version;
    
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
}
