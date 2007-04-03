package eu.sqooss.tool;

import java.io.FileInputStream;

import java.util.Properties;

/**
 * The Configurator class holds the configuration
 * information stored in the sqooss.properties
 * 
 */
public class Configurator {
    private final static Configurator defaultInstance;
    //
    Properties configuration;
    String sqoossHome;
    
    static {
        defaultInstance = new Configurator();
    }
    
    private Configurator() {
        super();
        sqoossHome = System.getenv("SQOOSS_HOME");
        configuration = new Properties();
        try {
            FileInputStream fis = new FileInputStream(sqoossHome + "/sqooss.properties");
            configuration.load(fis);
            fis.close();
        } catch (Exception e) {
            return;
        }
    }
    
    public String getValue(ConfigurationOptions option) {
        if(option.compareTo(ConfigurationOptions.DB_URL) == 0) {
            return configuration.getProperty("DB_URL");
        }
        if(option.compareTo(ConfigurationOptions.PLUGINS) == 0) {
            return configuration.getProperty("PLUGINS");
        }
        if(option.compareTo(ConfigurationOptions.SQOOSS_HOME) == 0) {
            return sqoossHome;
        }        
        if(option.compareTo(ConfigurationOptions.VCS_SPOOL) == 0) {
            return configuration.getProperty("VCS_SPOOL");
        }
        
        return null;
    }
    
    public static Configurator getInstance() {
        return defaultInstance;
    }
}
