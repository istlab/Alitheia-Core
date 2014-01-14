package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;

//@RunWith(JUnit4.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OhlohDeveloper.class)
public class OhlohUpdaterTest {

	private OhlohUpdaterWrapper ohlohupdater;
	private Logger mockedLogger;
	
	@Test
	/**
	 * Invoke run() normally 
	 */
    public void testRun() throws Exception {
		Mockito.when(OhlohDeveloper.getByOhlohId(anyString())).thenReturn(null);
		ohlohupdater.run();
		
		DBService dbs = ohlohupdater.getMockedDBService();
		
		//file0.xml contains 0 entries
		//file1.xml contains 1 entry
		//file2.xml contains 3 entries
		verify(dbs, Mockito.times(4)).addRecord((DAObject) anyObject());
    }
	
	@Test(expected = FileNotFoundException.class) 
	/**
	 * run() should fail with a FileNotFoundException if
	 * the path does not exist
	 */
    public void testRunInvalidPath() throws Exception {
		System.setProperty("eu.sqooss.updater.ohloh.path", "THISpathDOESnotEXIST");
		
		OhlohUpdaterWrapper updater = new OhlohUpdaterWrapper();
		updater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		
		Exception e = new RuntimeException();
		try {
			updater.run();
		} catch (Exception ee){
			e = ee;
		}
		
		verify(mockedLogger).error(anyString());
		throw e;
    }
	
	@Test(expected = FileNotFoundException.class) 
	/**
	 * run() should fail with a FileNotFoundException if
	 * the path is null
	 */
    public void testRunNullPath() throws Exception {
		System.clearProperty("eu.sqooss.updater.ohloh.path");
		
		OhlohUpdaterWrapper updater = new OhlohUpdaterWrapper();
		updater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		
		Exception e = new RuntimeException();
		try {
			updater.run();
		} catch (Exception ee){
			e = ee;
		}
		
		verify(mockedLogger).error(anyString());
		throw e;
    }
	
	@Test
	/**
	 * priority() should return 3
	 */
    public void testPriority() {
		long priority = ohlohupdater.priority();
		
		assertEquals(3, priority);
    }
	
	@Test
	/**
	 * getJob() should return the OhlohUpdater itself
	 */
    public void testGetJob() {
		Job job = ohlohupdater.getJob();
		
		assertEquals(ohlohupdater, job);
    }
	
	@Before
    public void setUp() {
		File f = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "ohlohpath");
		
		System.setProperty("eu.sqooss.updater.ohloh.path", f.getAbsolutePath());
		ohlohupdater = new OhlohUpdaterWrapper();
		
		mockedLogger = Mockito.mock(Logger.class);
		
		ohlohupdater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		
		mockStatic(OhlohDeveloper.class);

    }
 
    @After
    public void tearDown() {

    }

}
