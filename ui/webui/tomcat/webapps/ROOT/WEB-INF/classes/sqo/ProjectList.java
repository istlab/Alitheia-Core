
package sqo;

public class ProjectList {

    //String[] allProjects = {"KDE", "Apache", "FreeBSD"};
    //String currentProject = "KDE";
    String currentProject;
    
    public ProjectList() {
        currentProject = "KDE";
    }
    
    public void setCurrentProject (String project) {
        currentProject = project;
    }
    
    public String getCurrentProject () {
        return currentProject;
    }
}