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
import org.osgi.framework.BundleContext;

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
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;

public class TestGitUpdater extends TestGitSetup {

    static DBService db;
    static Logger l;
    static GitUpdater updater;
    static StoredProject sp;
    
    static BundleContext bc;
	static AlitheiaCore core;
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        initTestRepo();
        
        bc = mock(BundleContext.class);
		when(bc.getProperty("eu.sqooss.db")).thenReturn("H2");
		when(bc.getProperty("eu.sqooss.db.host")).thenReturn("localhost");
		when(bc.getProperty("eu.sqooss.db.schema")).thenReturn("alitheia;LOCK_MODE=3;MULTI_THREADED=true");
		when(bc.getProperty("eu.sqooss.db.user")).thenReturn("sa");
		when(bc.getProperty("eu.sqooss.db.passwd")).thenReturn("");
		when(bc.getProperty("eu.sqooss.db.conpool")).thenReturn("c3p0");

		core = new AlitheiaCore(bc);
        
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
    	ProjectFile dir = pf.getEnclosingDirectory();
    	assertNotNull(dir);
    	assertEquals(pf.getProjectVersion().getRevisionId(), pf.getProjectVersion().getRevisionId());
    	assertFalse(dir.getState().getStatus() == ProjectFileState.STATE_DELETED);
    	
    	if (pf.isAdded()) {
    		//Not much to test...
    		return;
    	}
    	
    	//Check that old and new versions of a file point to the same path
		ProjectFile old = pf.getPreviousFileVersion();
		assertNotNull(old);
		assertEquals(old.getFileName(), pf.getFileName());
		if (old.getIsDirectory() != pf.getIsDirectory()) {
			assertEquals(false, true);
		}
    }
}
