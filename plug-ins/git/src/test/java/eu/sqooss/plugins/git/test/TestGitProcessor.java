package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.plugins.updater.git.GitProcessor;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;

public class TestGitProcessor extends TestGitSetup {

    static DBService db;
    static Logger l;
    static StoredProject sp;
    static GitProcessor proc;
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        initTestRepo();
        
        Properties conProp = new Properties();
        conProp.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        conProp.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:alitheia.db");
        conProp.setProperty("hibernate.connection.username", "sa");
        conProp.setProperty("hibernate.connection.password", "");
        conProp.setProperty("hibernate.connection.host", "localhost");
        conProp.setProperty("hibernate.connection.dialect", "org.hibernate.dialect.HSQLDialect");
        conProp.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");
        
//        conProp.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
//        conProp.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/alitheia?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8");
//        conProp.setProperty("hibernate.connection.username", "root");
//        conProp.setProperty("hibernate.connection.password", "george");
//        conProp.setProperty("hibernate.connection.host", "localhost");
//        conProp.setProperty("hibernate.connection.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
//        conProp.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.DriverManagerConnectionProvider");

        File root = new File(System.getProperty("user.dir"));
        File config = null;
        while (true) {
            String[] extensions = { "xml" };
            boolean recursive = true;

            Collection files = FileUtils.listFiles(root, extensions, recursive);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
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
        
        LogManager lm = new LogManagerImpl(true);
        l = lm.createLogger("sqooss.updater");
        
        AlitheiaCore.getInstance();
        
        db = new DBServiceImpl(conProp, config.toURL() , l);
        db.startDBSession();
        sp = new StoredProject();
        sp.setName(projectName);
        db.addRecord(sp);
        db.commitDBSession();
    }
    
    @Before
    public void setUp() throws AccessorException, URISyntaxException {
        getGitRepo();
        assertNotNull(git);
        proc = new GitProcessor(sp, git, db, l);
    }
    
    @Test
    public void testProcessOneRevision() throws Exception {
    	db.startDBSession();
    	
    	Revision rev = git.newRevision("94f389bf5d9af4511597d035e69d1be9510b50c7");
        proc.processOneRevision(rev);
    	
    	db.rollbackDBSession();
    }
    
    @Test
    public void testProcessCopiedFiles() throws Exception {
    	db.startDBSession();
    	
    	Revision cur = git.getFirstRevision();
        Revision prev = git.getNextRevision(cur);
    	ProjectVersion curv = ProjectVersion.getVersionByRevision(sp, cur.getUniqueId());
        assertNotNull(curv);
        ProjectVersion prevv = ProjectVersion.getVersionByRevision(sp, prev.getUniqueId());
        assertNotNull(prevv);
        
    	proc.processCopiedFiles(git, cur, curv, prevv);
    	assertEquals(0,cur.compare(cur, prev));
    	/*for (CommitCopyEntry cce : cur.getCopyOperations()) {
    		String fromPath = cce.fromPath();
    		String toPath = cce.toPath();
    		
    		
    	}*/
    	
    	db.rollbackDBSession();
    }
    
    @Test
    public void testGetAuthor() {
        db.startDBSession();

        //Test a properly formatted name
        Developer d = proc.getAuthor(sp, "Papa Smurf <pm@smurfvillage.com>");
        assertNotNull(d);
        assertEquals("Papa Smurf", d.getName());
        assertNull(d.getUsername());
        assertEquals(1, d.getAliases().size());
        assertTrue(d.getAliases().contains(new DeveloperAlias("pm@smurfvillage.com", d)));

        //A bit of Developer DAO testing
        assertNotNull(Developer.getDeveloperByEmail("pm@smurfvillage.com", sp));
        d.addAlias("pm@smurfvillage.com");
        assertEquals(1, d.getAliases().size());
        
        //Test a non properly formated name
        d = proc.getAuthor(sp, "Gargamel <gar@smurfvillage.(name)>");
        assertNotNull(d);
        assertEquals("Gargamel", d.getUsername());
        assertNull(d.getName());
        assertEquals(1, d.getAliases().size());
        assertTrue(d.getAliases().contains(new DeveloperAlias("gar@smurfvillage.(name)", d)));
        
        //Test a user name only name
        d = proc.getAuthor(sp, "Smurfette");
        assertNotNull(d);
        assertEquals("Smurfette", d.getUsername());
        assertNull(d.getName());
        assertEquals(0, d.getAliases().size());
        
        //Test a non properly formated email
        d = proc.getAuthor(sp, "Clumsy Smurf <smurfvillage.com>");
        assertNotNull(d);
        assertNull(d.getUsername());
        assertEquals("Clumsy Smurf <smurfvillage.com>", d.getName());
        assertEquals(0, d.getAliases().size());
        
        //Test with name being just an email
        d = proc.getAuthor(sp, "chef@smurfvillage.com");
        assertNotNull(d);
        assertNull(d.getUsername());
        assertNull(d.getName());
        assertEquals(1, d.getAliases().size());
       
        db.rollbackDBSession();
    }
}
