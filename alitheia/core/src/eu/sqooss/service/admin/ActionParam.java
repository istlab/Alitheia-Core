package eu.sqooss.service.admin;

/**
 * All potential action parameters and associated help messages.
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public enum ActionParam {
    PROJECT_NAME("projectname", "The name used to register a project " +
    		"to the database"),
    PROJECT_ID("projectid", "The database key for the project"),
    CLUSTERNODE_NAME("clusternodename", " The name of a node in a " +
    		"SQO-OSS cluster.");
    
    private String name;
    private String help;
    
    private ActionParam(String name, String help) {
        this.name = name;
        this.help = help;
    }
    
    public String getName() {
        return name;
    }
    
    public String getHelp() {
        return help;
    }
    
    public static ActionParam fromString(String p) {
        if (p.equalsIgnoreCase(PROJECT_NAME.toString()))
            return PROJECT_NAME;
        if (p.equalsIgnoreCase(PROJECT_ID.toString()))
            return PROJECT_ID;
        if (p.equalsIgnoreCase(CLUSTERNODE_NAME.toString()))
            return CLUSTERNODE_NAME;
        return null;
    }
}