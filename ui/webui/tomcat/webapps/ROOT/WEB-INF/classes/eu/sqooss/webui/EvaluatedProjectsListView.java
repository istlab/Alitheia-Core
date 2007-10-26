package eu.sqooss.webui;

import eu.sqooss.webui.ListView;

public class EvaluatedProjectsListView extends ListView {

    String currentProject;
    
    public EvaluatedProjectsListView () {
        currentProject = "KDE";
        items.removeAllElements();
        items.addElement(new String("FreeBSD"));
        items.addElement(new String("Apache"));
        items.addElement(new String("KDE"));
        items.addElement(new String("Samba"));
        items.addElement(new String("Nmap"));
    }
    
    public void setCurrentProject ( String project ) {
        currentProject = project;
    }
    
    public String getCurrentProject () {
        return currentProject;
    }
}