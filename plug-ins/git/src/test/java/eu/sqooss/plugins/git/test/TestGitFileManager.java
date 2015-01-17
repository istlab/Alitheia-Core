package eu.sqooss.plugins.git.test;

import static org.junit.Assert.assertNotNull;
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
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;

public class TestGitFileManager extends TestGitSetup {

    static Logger l;
    static StoredProject sp;
    static GitFileManager filem;
    
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
           
        LogManager lm = new LogManagerImpl(true);
        l = lm.createLogger("sqooss.updater");
        
        AlitheiaCore.testInstance();
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
