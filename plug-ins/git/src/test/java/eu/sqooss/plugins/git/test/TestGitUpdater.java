package eu.sqooss.plugins.git.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import eu.sqooss.impl.service.db.ConfigurationKey;
import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.properties.PropertiesWorker;
import eu.sqooss.properties.PropertyKey;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.FileState;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.DeveloperUtils;
import eu.sqooss.service.db.util.ProjectFileUtils;
import eu.sqooss.service.db.util.ProjectVersionUtils;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LoggerName;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.Revision;

public class TestGitUpdater extends TestGitSetup {
	
	private enum TestKey implements PropertyKey {
		HOST("hibernate.connection.host");
		
		private String key;
		
		TestKey(String key) {
			this.key = key;
		}
		
		@Override
		public String getKey() {
			return this.key;
		}
	}

    static DBService db;
    static Logger l;
    static GitUpdater updater;
    static StoredProject sp ;
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        initTestRepo();
        
        PropertiesWorker conProp = new PropertiesWorker();
        conProp.setProperty(ConfigurationKey.DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        conProp.setProperty(ConfigurationKey.URL, "jdbc:hsqldb:file:alitheia.db");
        conProp.setProperty(ConfigurationKey.USERNAME, "sa");
        conProp.setProperty(ConfigurationKey.PASSWORD, "");
        conProp.setProperty(TestKey.HOST, "localhost");
        conProp.setProperty(ConfigurationKey.DIALECT, "org.hibernate.dialect.HSQLDialect");
        conProp.setProperty(ConfigurationKey.PROVIDER_CLASS, "org.hibernate.connection.DriverManagerConnectionProvider");
        
//        conProp.setProperty(ConfigurationKey.DRIVER_CLASS, "com.mysql.jdbc.Driver");
//        conProp.setProperty(ConfigurationKey.URL, "jdbc:mysql://localhost/alitheia?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8");
//        conProp.setProperty(ConfigurationKey.USERNAME, "root");
//        conProp.setProperty(ConfigurationKey.PASSWORD, "george");
//        conProp.setProperty("hibernate.connection.host", "localhost");
//        conProp.setProperty(ConfigurationKey.DIALECT, "org.hibernate.dialect.MySQLInnoDBDialect");
//        conProp.setProperty(ConfigurationKey.PROVIDER_CLASS, "org.hibernate.connection.DriverManagerConnectionProvider");

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
        l = lm.createLogger(LoggerName.UPDATER);
        
        AlitheiaCore.testInstance();
        
        db = new DBServiceImpl(conProp, config.toURI().toURL() , l);
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
        assertNotNull(new DeveloperUtils(db).getDeveloperByEmail("pm@smurfvillage.com", sp));
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
            ArrayList<ProjectFile> foundFiles = new ArrayList<>();
          
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
            ProjectVersion pv = new ProjectVersionUtils(db, new ProjectFileUtils(this.db)).getVersionByRevision(sp, from.getUniqueId());
            assertNotNull(pv);
            
            //Compare repository files against database files
            while (tw.next()) {
                String path = "/" + tw.getPathString();
                //System.err.println("Tree entry: " + path);
                String basename = eu.sqooss.service.util.FileUtils.basename(path);
                String dirname = eu.sqooss.service.util.FileUtils.dirname(path);
                ProjectFile pf = new ProjectFileUtils(db).findFile(sp.getId(), basename, dirname, pv.getRevisionId());
                testVersionedProjectFile(pf);
                if (!pf.getIsDirectory())
                	foundFiles.add(pf);
            }

            List<ProjectFile> allfiles = pv.allFiles();
            for (ProjectFile pf : allfiles) {
            	if (!foundFiles.contains(pf)) {
            		System.err.println("File " + pf + " not in repository");
            		assertTrue(false);
            	}
            }

            for (ProjectFile pf : foundFiles) {
            	if (!allfiles.contains(pf)) {
            		System.err.println("File " + pf + " not found in allFiles() result");
            		assertTrue(false);
            	}
            }
            
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
    	ProjectFile dir = new ProjectFileUtils(db).getEnclosingDirectory(pf);
    	assertNotNull(dir);
    	assertEquals(pf.getProjectVersion().getRevisionId(), pf.getProjectVersion().getRevisionId());
    	assertFalse(dir.getState().getFileStatus() == FileState.DELETED);
    	
    	if (pf.isAdded()) {
    		//Not much to test...
    		return;
    	}
    	
    	//Check that old and new versions of a file point to the same path
		ProjectFile old = new ProjectFileUtils(db).getPreviousFileVersion(pf);
		assertNotNull(old);
		assertEquals(old.getFileName(), pf.getFileName());
		if (old.getIsDirectory() != pf.getIsDirectory()) {
			assertEquals(false, true);
		}
    }
}
