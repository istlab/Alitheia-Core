package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import eu.sqooss.impl.service.updater.OhlohUpdater;
import eu.sqooss.service.scheduler.Job;

@RunWith(JUnit4.class)
public class OhlohUpdaterTest {

	private OhlohUpdater ohlohupdater;
	
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
		ohlohupdater = new OhlohUpdater();
    }
 
    @After
    public void tearDown() {

    }
	
}
