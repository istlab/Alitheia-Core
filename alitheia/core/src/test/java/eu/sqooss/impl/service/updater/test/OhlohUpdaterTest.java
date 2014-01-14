package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.FileNotFoundException;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;

@RunWith(JUnit4.class)
public class OhlohUpdaterTest {

	private OhlohUpdaterWrapper ohlohupdater;
	private Logger mockedLogger;
	
	@Test(expected = FileNotFoundException.class) 
	/**
	 * run() should fail with a FileNotFoundException if
	 * the path does not exist
	 */
    public void testRunInvalidPath() throws Exception {
		System.setProperty("eu.sqooss.updater.ohloh.path", "THISpathDOESnotEXIST");
		
		OhlohUpdaterWrapper updater = new OhlohUpdaterWrapper();
		updater.setUpdateParams(mock(StoredProject.class), mockedLogger);
		
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
		updater.setUpdateParams(mock(StoredProject.class), mockedLogger);
		
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
		System.setProperty("eu.sqooss.updater.ohloh.path", "ohlohpath/");
		ohlohupdater = new OhlohUpdaterWrapper();
		
		mockedLogger = mock(Logger.class);
		
		ohlohupdater.setUpdateParams(mock(StoredProject.class), mockedLogger);
    }
 
    @After
    public void tearDown() {

    }
	
}
