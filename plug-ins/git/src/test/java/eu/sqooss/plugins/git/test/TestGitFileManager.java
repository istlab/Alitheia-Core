package eu.sqooss.plugins.git.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.plugins.updater.git.GitFileManager;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.util.FileUtils;

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
    	String fPath = "/someDir/bla/filename.ext";
    	ProjectFileState status = ProjectFileState.added();
    	ProjectFile copyFrom = null;
    	
    	ProjectFile pf = filem.addFile(version, fPath, status, 
    			SCMNodeType.FILE, copyFrom);
    	
    	assertEquals(FileUtils.basename(fPath),pf.getName());
        assertEquals(FileUtils.dirname(fPath),pf.getDir());
        assertEquals(version,pf.getValidFrom());
        assertTrue(version.getVersionFiles().contains(pf));    	
    }
    
    @Test
    public void testHandleDirCopy() {
    	ProjectVersion version = new ProjectVersion(sp);
    	ProjectVersion prev = version.getPreviousVersion();
    	Directory fdir = Directory.getDirectory("/someDir/bla/",false);
    	Directory tdir = Directory.getDirectory("/otherDir/",false);
    	filem.handleDirCopy(version, prev, fdir, tdir, null);
    	
    	assertTrue(false);
    }
    
    @Test
    public void testMkdirs(){
    	ProjectVersion version = new ProjectVersion(sp);
    	String fPath = "/someDir/bla/filename.ext";
    	Set<ProjectFile> res = filem.mkdirs(version, fPath);
    	for(ProjectFile pf : res){
    		if(pf.getDir().equals(FileUtils.dirname(fPath)))
    			return;
    	}
    	assertTrue(false);
    }
}
