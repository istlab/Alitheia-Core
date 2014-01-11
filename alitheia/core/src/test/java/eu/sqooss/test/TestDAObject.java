package eu.sqooss.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class TestDAObject {
    protected static DBService db;
    protected static Logger l;

    /**
     * True if the test expects the session to no be active when it is done.
     */
    private boolean needsRollback = true;

    /**
     * Do not rollback the database after this test.
     * 
     * Call this if the database is already rolled back, for instance due to an
     * expected hibernate exception.
     */
    protected void dontRollback() {
        needsRollback = false;
    }

    @BeforeClass
    public static void setUpDatabase() throws MalformedURLException {
        l = TestDAObject.initLogger();
        db = TestDAObject.initDatabase(l);
    }

    @Before
    public void startDBSession() {
        db.startDBSession();
    }

    @After
    public void rollbackDBSession() {
        if(needsRollback) {
            assertTrue("Make sure we do not store any changes", db.rollbackDBSession());
        } else if (db.isDBSessionActive()) {
            fail("Test thinks DB session should not be rolled back, but it is still active!");
        }
    }

    private static DBServiceImpl dbInstance = null;

    private static Logger initLogger() {
        LogManager lm = new LogManagerImpl(true);
        return lm.createLogger("sqooss.updater");
    }

    private static DBServiceImpl initDatabase(Logger l) throws MalformedURLException {
        return initDatabase(l, false);
    }

    private static DBServiceImpl initDatabase(Logger l, boolean newInstance) throws MalformedURLException {
        if(dbInstance == null || !dbInstance.logger().getName().equals(l.getName()) || newInstance) {
            Properties conProp = new Properties();
            
            // Setup Database connection
            conProp.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            conProp.setProperty("hibernate.connection.url", "jdbc:h2:mem");
            conProp.setProperty("hibernate.connection.dialect", "org.hibernate.dialect.HSQLDialect");
            conProp.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");
    
            // Find the XML configuration file
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
