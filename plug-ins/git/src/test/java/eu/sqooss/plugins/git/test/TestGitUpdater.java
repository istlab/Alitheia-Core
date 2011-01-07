package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class TestGitUpdater {

    static DBService db;
    static Logger l;
    static GitUpdater updater;

    @BeforeClass
    public static void setup() throws MalformedURLException {
        Properties conProp = new Properties();
        conProp.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        conProp.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:alitheia.db");
        conProp.setProperty("hibernate.connection.username", "sa");
        conProp.setProperty("hibernate.connection.password", "");
        conProp.setProperty("hibernate.connection.host", "localhost");
        conProp.setProperty("hibernate.connection.dialect", "org.hibernate.dialect.HSQLDialect");
        conProp.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");

        File root = new File(System.getProperty("user.dir"));
        File config = null;
        while (true) {
            try {
                String[] extensions = { "xml" };
                boolean recursive = true;

                Collection files = FileUtils.listFiles(root, extensions,
                        recursive);

                for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                    File file = (File) iterator.next();
                    if (file.getName().equals("hibernate.cfg.xml")) {
                        config = file; 
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (config == null)
                root = root.getParentFile();
            else 
                break;
        }
        
        LogManager lm = new LogManagerImpl(true);
        l = lm.createLogger("test.db");
        
        db = new DBServiceImpl(conProp, config.toURL() , l);
    }

    @Test
    public void testUpdate() {
        fail("Not yet implemented");
    }

}
