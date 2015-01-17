package eu.sqooss.plugins.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.plugins.updater.git.GitFileManager;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.SCMNodeType;

public class TestGitFileManager extends TestGitSetup {

    static Logger l;
    static StoredProject sp;
    static GitFileManager filem;
    
    static BundleContext bc;
	static AlitheiaCore core;
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        initTestRepo();
           
        LogManager lm = new LogManagerImpl(true);
        l = lm.createLogger("sqooss.updater");
        
        //AlitheiaCore.testInstance();
        sp = new StoredProject();
        sp.setName(projectName);
    }
    
    @Before
    public void setUp() throws AccessorException, URISyntaxException {
        getGitRepo();
        assertNotNull(git);
        filem = new GitFileManager(sp, l);
    }
    
    @Test
    public void testAddFile() {
    	ProjectVersion version = new ProjectVersion(sp);
    	String fPath = "someDir/bla/";
    	ProjectFileState status = ProjectFileState.added();
    	ProjectFile copyFrom = null;
    	
    	ProjectFile newFile = filem.addFile(version, fPath, status, 
    			SCMNodeType.FILE, copyFrom);
    	
    	/*pf.setName(fname);
        pf.setDir(dir);
        pf.setState(status);
        pf.setCopyFrom(copyFrom);
        pf.setValidFrom(version);
        pf.setValidUntil(null);*/
        assertTrue(version.getVersionFiles().contains(newFile));
    	
    }
    
    @Test
    public void testHandleDirCopy() {
    	
    }
    
    @Test
    public void testHandleDirDeletion() {
    	
    }
    
    @Test
    public void testMkdirs(){
    	
    }
}
