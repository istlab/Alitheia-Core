package eu.sqooss.test.service.logging;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LoggerName;

@RunWith(MockitoJUnitRunner.class)
public class LogManagerImplTest {

	private LogManagerImpl manager;
	@Mock private BundleContext mockedBC;
	@Mock private org.apache.log4j.Logger managerLogger;
	
	@Before
	public void setUp() {
		when(this.mockedBC.getProperty(anyString())).thenReturn(null);
		
		this.manager = new LogManagerImpl(this.mockedBC, this.managerLogger);
	}

	@After
	public void tearDown() {
		this.manager.shutDown();
	}
	
	@Test
	public void testCreateNewLogger() {
		Logger logger = this.manager.createLogger(LoggerName.SQOOSS);
		
		assertEquals(LoggerName.SQOOSS.getName(), logger.getName().getName());
	}

	@Test
	public void testCreateLoggerTwice() {
		Logger logger1 = this.manager.createLogger(LoggerName.SQOOSS);
		Logger logger2 = this.manager.createLogger(LoggerName.SQOOSS);
		
		assertSame(logger1, logger2);
	}

	@Test
	public void testReleaseLastLogger() {
		Logger logger1 = this.manager.createLogger(LoggerName.SQOOSS);
		this.manager.releaseLogger(LoggerName.SQOOSS);
		Logger logger2 = this.manager.createLogger(LoggerName.SQOOSS);
		
		assertNotSame(logger1, logger2);
	}

	@Test
	public void testReleaseLogger() {
		Logger logger1 = this.manager.createLogger(LoggerName.SQOOSS);
		Logger logger2 = this.manager.createLogger(LoggerName.SQOOSS);
		this.manager.releaseLogger(LoggerName.SQOOSS);
		Logger logger3 = this.manager.createLogger(LoggerName.SQOOSS);
		
		assertSame(logger1, logger3);
		assertSame(logger2, logger3);
	}
	
	@Test
	public void testReleaseLoggerNotContained() {
		this.manager.releaseLogger(LoggerName.SQOOSS);
		
		verify(this.managerLogger, times(1)).error(anyString());
	}
	
	@Test
	public void testGetRecentEntries() {
		assertArrayEquals(new String[0], this.manager.getRecentEntries());
	}
	
	@Test
	public void testSetGetBundleContext() {
		this.manager.setBundleContext(this.mockedBC);
		assertEquals(this.mockedBC, this.manager.getBundleContext());
	}
}
