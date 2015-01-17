package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        initTestRepo();
           
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
