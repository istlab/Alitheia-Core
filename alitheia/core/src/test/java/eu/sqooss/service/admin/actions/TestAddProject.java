package eu.sqooss.service.admin.actions;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;

public class TestAddProject {
    static DBService db;
    TDSService tds;
    static Logger l;

    public static void initLogger() {
        LogManager lm = new LogManagerImpl(true);
        l = lm.createLogger("sqooss.updater");
    }

    public static void initDatabase() throws MalformedURLException {
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

        db = new DBServiceImpl(conProp, config.toURI().toURL() , l);
    }

    @BeforeClass
    public static void setUpClass() throws MalformedURLException {
        initLogger();
        initDatabase();
    }

    @Before
    public void setUpTest() {
        db.startDBSession();
        tds = new TDSServiceImpl(db, l);
        tds.startUp();
    }

    @After
    public void tearDownTest() {
        tds.shutDown();
        assertTrue("Make sure we do not store any changes", db.rollbackDBSession());
    }

    @Test(expected=Exception.class)
    public void noAccessors() throws Exception {
        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();
    }

    @Test(expected=Exception.class)
    public void UnknownSCMAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();
    }

    @Test(expected=Exception.class)
    public void unknownMailAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();
    }

    @Test(expected=Exception.class)
    public void unknownBTSAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();
    }

    @Test
    public void testAllArgs() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();

        assertTrue(db.flushDBSession());

        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        assertEquals(1, projects.size());

        StoredProject sp = projects.get(0);
        assertEquals("Test Name", sp.getName());
        assertEquals("test-bts-acc://bts-path", sp.getBtsUrl());
        assertEquals("test-scm-acc://scm-path", sp.getScmUrl());
        assertEquals("test-mail-acc://mail-path", sp.getMailUrl());
        assertEquals("maintainer@isp.com", sp.getContactUrl());
        assertEquals("awesome.project.org", sp.getWebsiteUrl());

        ProjectAccessor acc = tds.getAccessor(sp.getId());
        assertEquals("Test Name", acc.getName());
        assertEquals(sp.getId(), (long)acc.getId());
    }
}
