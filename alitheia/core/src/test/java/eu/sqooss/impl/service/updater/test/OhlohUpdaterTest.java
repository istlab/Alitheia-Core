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
import java.util.Date;

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
	 * Invoke run() normally, with a known developer
	 */
    public void testRunKnown() throws Exception {
		// -- Given
		OhlohDeveloper od = Mockito.mock(OhlohDeveloper.class);
		Mockito.when(OhlohDeveloper.getByOhlohId(anyString())).thenReturn(od).thenReturn(null);
		
		// -- When
		ohlohupdater.run();
		
		// -- Then
		verify(od).setEmailHash("L9ThxnotKPzthJ7hu3bnORuT6xI=");
		verify(od).setTimestamp(Mockito.any(Date.class));
		verify(od).setUname("TestDeveloper0");
    }
	
	@Test
	/**
	 * Invoke run() normally 
	 */
    public void testRun() throws Exception {
		// -- Given
		Mockito.when(OhlohDeveloper.getByOhlohId(anyString())).thenReturn(null);
		DBService dbs = ohlohupdater.getMockedDBService();
		
		// -- When
		ohlohupdater.run();
		
		// -- Then
		//file0.xml contains 0 entries
		//file1.xml contains 1 entry
		//file2.xml contains 3 entries
		verify(dbs, Mockito.times(4)).addRecord((DAObject) anyObject());
		
		//file0.xml has no entries
		//file3.xml contains an xml error
		verify(mockedLogger, Mockito.times(2)).warn(anyString());
    }
	
	@Test(expected = FileNotFoundException.class) 
	/**
	 * run() should fail with a FileNotFoundException if
	 * the path does not exist
	 */
    public void testRunInvalidPath() throws Exception {
		// -- Given
		//A non-existent Ohloh path
		System.setProperty("eu.sqooss.updater.ohloh.path", "THISpathDOESnotEXIST");
		OhlohUpdaterWrapper updater = new OhlohUpdaterWrapper();
		updater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		//RuntimeException is not equal to FileNotFoundException, therefore the test will fail
		//  if a FileNotFoundException is not thrown
		Exception e = new RuntimeException();
		try {
			// -- When
			updater.run();
		} catch (Exception ee){
			e = ee;
		}
		
		// -- Then
		//Note that we have to save the thrown error and throw it ourselves later: to be
		//  able to veryify the logger error entry
		verify(mockedLogger).error(anyString());
		throw e;
    }
	
	@Test(expected = FileNotFoundException.class) 
	/**
	 * run() should fail with a FileNotFoundException if
	 * the path is null
	 */
    public void testRunNullPath() throws Exception {
		// -- Given
		//No Ohloh path set
		System.clearProperty("eu.sqooss.updater.ohloh.path");
		OhlohUpdaterWrapper updater = new OhlohUpdaterWrapper();
		updater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		//RuntimeException is not equal to FileNotFoundException, therefore the test will fail
		//  if a FileNotFoundException is not thrown
		Exception e = new RuntimeException();
		try {
			// -- When
			updater.run();
		} catch (Exception ee){
			e = ee;
		}
		
		// -- Then
		//Note that we have to save the thrown error and throw it ourselves later: to be
		//  able to veryify the logger error entry
		verify(mockedLogger).error(anyString());
		throw e;
    }
	
	@Test
	/**
	 * priority() should return 3
	 */
    public void testPriority() {
		// -- When
		long priority = ohlohupdater.priority();
		// -- Then
		assertEquals(3, priority);
    }
	
	@Test
	/**
	 * getJob() should return the OhlohUpdater itself
	 */
    public void testGetJob() {
		// -- When
		Job job = ohlohupdater.getJob();
		// -- Then
		assertEquals(ohlohupdater, job);
    }
	
	@Before
    public void setUp() {
		//Divert the Ohloh path to our local test resource folder
		File f = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "ohlohpath");
		System.setProperty("eu.sqooss.updater.ohloh.path", f.getAbsolutePath());
		//Wrap the OhlohUpdater class, so we can modify protected variables (and hereby facilitate testing)
		ohlohupdater = new OhlohUpdaterWrapper();
		//Mock and store the logger so we can verify error() and warning() logging
		mockedLogger = Mockito.mock(Logger.class);
		ohlohupdater.setUpdateParams(Mockito.mock(StoredProject.class), mockedLogger);
		//Mock OhlohDeveloper static methods so we can intercept calls to getByOhlohId()
		mockStatic(OhlohDeveloper.class);
    }
 
    @After
    public void tearDown() {

    }

}
