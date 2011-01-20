package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.Revision;

public class TestGitUpdater extends TestGitSetup {

    static DBService db;
    static Logger l;
    static GitUpdater updater;
    static StoredProject sp ;

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
        
        AlitheiaCore.testInstance();
        
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
        updater = new GitUpdater(db, git, l, sp);
    }

    @Test
    public void testGetAuthor() {
        db.startDBSession();

        //Test a properly formatted name
        Developer d = updater.getAuthor(sp, "Papa Smurf <pm@smurfvillage.com>");
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
        d = updater.getAuthor(sp, "Gargamel <gar@smurfvillage.(name)>");
        assertNotNull(d);
        assertEquals("Gargamel", d.getUsername());
        assertNull(d.getName());
        assertEquals(1, d.getAliases().size());
        assertTrue(d.getAliases().contains(new DeveloperAlias("gar@smurfvillage.(name)", d)));
        
        //Test a user name only name
        d = updater.getAuthor(sp, "Smurfette");
        assertNotNull(d);
        assertEquals("Smurfette", d.getUsername());
        assertNull(d.getName());
        assertEquals(0, d.getAliases().size());
        
        //Test a non properly formated email
        d = updater.getAuthor(sp, "Clumsy Smurf <smurfvillage.com>");
        assertNotNull(d);
        assertNull(d.getUsername());
        assertEquals("Clumsy Smurf <smurfvillage.com>", d.getName());
        assertEquals(0, d.getAliases().size());
        
        //Test with name being just an email
        d = updater.getAuthor(sp, "chef@smurfvillage.com");
        assertNotNull(d);
        assertNull(d.getUsername());
        assertNull(d.getName());
        assertEquals(1, d.getAliases().size());
       
        db.rollbackDBSession();
    }
    
    @Test
    public void testUpdate() throws Exception {
        File repo = new File(localrepo, Constants.DOT_GIT);
        FileRepository local =  new FileRepository(repo);
        Revision from = git.getFirstRevision();
        Revision to = git.getNextRevision(from);
        Revision upTo = git.newRevision("94f389bf5d9af4511597d035e69d1be9510b50c7");
        
        while (to.compareTo(upTo) < 0) {
            ArrayList<ProjectFile> foundFiles = new ArrayList<ProjectFile>();
           
            System.err.println("Revision: " + from.getUniqueId());
            updater.updateFromTo(from, to);

            RevWalk rw = new RevWalk(local);
            ObjectId obj = local.resolve(from.getUniqueId());
            RevCommit commit = rw.parseCommit(obj);

            TreeWalk tw = new TreeWalk(local);
            tw.addTree(commit.getTree());
            tw.setRecursive(true);
            
            db.startDBSession();
            sp = db.attachObjectToDBSession(sp);
            ProjectVersion pv = ProjectVersion.getVersionByRevision(sp, from.getUniqueId());
            assertNotNull(pv);
            
            //Compare repository files against database files
            while (tw.next()) {
                String path = "/" + tw.getPathString();
                //System.err.println("Tree entry: " + path);
                String basename = eu.sqooss.service.util.FileUtils.basename(path);
                String dirname = eu.sqooss.service.util.FileUtils.dirname(path);
                ProjectFile pf = ProjectFile.findFile(sp.getId(), basename, dirname, pv.getRevisionId());
                testVersionedProjectFile(pf);
                foundFiles.add(pf);
            }
            
            //Check whether we have extra files in the revision
            //for (ProjectFile pf: pv.getFiles()) {
            //    assertTrue(foundFiles.contains(pf));
            //}
            
            db.commitDBSession();
            tw.release();
            rw.release();
            from = to;
            foundFiles.clear();
            to = git.getNextRevision(to);
        }
    }
    
    //From this point forward, all methods assume an open db session
    public void testVersionedProjectFile(ProjectFile pf) {
    	assertNotNull(pf);
    	//System.err.println("Testing file: " + pf);
    	
    	//Check that each file entry is accompanied with an enclosing directory
    	//entry with an added or modified state
    	ProjectFile dir = pf.getEnclosingDirectory();
    	assertNotNull(dir);
    	assertEquals(pf.getProjectVersion().getRevisionId(), pf.getProjectVersion().getRevisionId());
    	assertTrue(dir.getState().getStatus() == ProjectFileState.STATE_MODIFIED || 
    			dir.getState().getStatus() == ProjectFileState.STATE_ADDED);
    	
    	if (pf.isAdded()) {
    		//Not much more to test...
    		return;
    	}
    	
    	//Check that old and new versions of a file point to the same path
		ProjectFile old = pf.getPreviousFileVersion();
		assertNotNull(old);
		assertEquals(old.getFileName(), pf.getFileName());
		assertEquals(old.getIsDirectory(), pf.getIsDirectory());
    }
}
