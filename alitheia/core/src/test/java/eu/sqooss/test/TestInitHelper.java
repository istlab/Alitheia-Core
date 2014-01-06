package eu.sqooss.test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class TestInitHelper {
    private static DBServiceImpl dbInstance = null;

    public static Logger initLogger() {
        LogManager lm = new LogManagerImpl(true);
        return lm.createLogger("sqooss.updater");
    }

    public static DBServiceImpl initDatabase(Logger l) throws MalformedURLException {
        return initDatabase(l, false);
    }

    public static DBServiceImpl initDatabase(Logger l, boolean newInstance) throws MalformedURLException {
        if(dbInstance == null || !dbInstance.logger().getName().equals(l.getName()) || newInstance) {
            Properties conProp = new Properties();
            conProp.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            conProp.setProperty("hibernate.connection.url", "jdbc:h2:mem");
            conProp.setProperty("hibernate.connection.dialect", "org.hibernate.dialect.HSQLDialect");
            conProp.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");
    
            File root = new File(System.getProperty("user.dir"));
            File config = null;
            while (true) {
                String[] extensions = { "xml" };
                boolean recursive = true;
    
                @SuppressWarnings("unchecked")
                Collection<File> files = FileUtils.listFiles(root, extensions, recursive);
    
                for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
                    File file = iterator.next();
                    if (file.getName().equals("hibernate.cfg.xml")) {
                        config = file;
                        break;
                    }
                }
    
                if (config == null)
                    root = root.getParentFile();
                else
                    break;
            }
    
            dbInstance = new DBServiceImpl(conProp, config.toURI().toURL() , l);
        }
        return dbInstance;
    }
}
