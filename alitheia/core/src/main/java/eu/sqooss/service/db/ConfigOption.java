package eu.sqooss.service.db;

/**
 * Stores all standard project-wide configuration options that
 * the system knows about.  
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public enum ConfigOption {
    
	/**
     * The project's original BTS URL
     */
    PROJECT_BTS_SOURCE("eu.sqooss.project.bts.source", "The project's original BTS URL"),
    
    /**
     * The project's BTS type
     */
    PROJECT_BTS_TYPE("eu.sqooss.project.bts.type", "The project's BTS type"),
	
    /**
     * The project's BTS URL
     */
    PROJECT_BTS_URL("eu.sqooss.project.bts.url", "The project's local BTS mirror URL"),
    
    /**
     * The project's contact address
     */
    PROJECT_CONTACT("eu.sqooss.project.contact", "The project's contact address"),
    
    /**
     * The project's MailingList source URL
     */
    PROJECT_ML_SOURCE("eu.sqooss.project.ml.source", "The project's source mailing list URL"),
    
    /**
     * The project's MailingList source URL
     */
    PROJECT_ML_TYPE("eu.sqooss.project.ml.type", "The project's local mailing list type"),

    /**
     * The project's MailingList URL
     */
    PROJECT_ML_URL("eu.sqooss.project.ml.url", "The project's local mailing list URL"),
    
    /**
     * The project's name
     */
    PROJECT_NAME("eu.sqooss.project.name", "The project's name"),
    
    /**
     * The project's SCM type
     */
    PROJECT_SCM_TYPE("eu.sqooss.project.scm.type", "The project's SCM type"),

    /**
     * The project's SCM URL
     */
    PROJECT_SCM_URL("eu.sqooss.project.scm.url", "The project's local SCM URL"),
    
    /**
     * The project's SCM URL
     */
    PROJECT_SCM_SOURCE("eu.sqooss.project.scm.source", "The project's original SCM URL"),
    
    /**
     * The source code paths to process while executing the updater
     */
    PROJECT_SCM_PATHS_INCL("eu.sqooss.project.scm.path.incl", "The source code paths to process"),
    
    /**
     * The source code paths to process not to process
     */
    PROJECT_SCM_PATHS_EXCL("eu.sqooss.project.scm.path.excl", "The source code paths not to process"),
    
    /**
     * The project's website
     */
    PROJECT_WEBSITE("eu.sqooss.project.website", "The project's website");
    
    
    private final String propname;
    private final String desc;
    
    public String getName() {
        return propname;
    }

    public String getDesc() {
        return desc;
    }

    private ConfigOption(String name, String desc) {
        this.propname = name;
        this.desc = desc;
    }
}

