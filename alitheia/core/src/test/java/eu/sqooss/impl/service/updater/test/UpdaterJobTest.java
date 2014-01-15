package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import eu.sqooss.service.updater.MetadataUpdater;

@RunWith(JUnit4.class)
public class UpdaterJobTest {

	private UpdaterJobWrapper job;
	private MetadataUpdater metaupdater;
	
	@Test
    public void testToString(){
		// -- When
		String value = job.toString();
		// -- Then
		assertEquals(metaupdater.toString(), value);
    }
	
	@Test
    public void testRun() throws Exception {
		// -- When
		job.run();
		// -- Then
		verify(metaupdater).update();
    }
	
	@Test
    public void testGetUpdater() {
		// -- When
		MetadataUpdater upd = job.getUpdater();
		// -- Then
		assertEquals(metaupdater, upd);
    }
	
	@Test
    public void testPriority() {
		// -- When
		long priority = job.priority();
		// -- Then
		assertEquals(0, priority);
    }
	
	@Before
    public void setUp() {
		metaupdater = mock(MetadataUpdater.class);
    	job = new UpdaterJobWrapper(metaupdater);
    }
 
    @After
    public void tearDown() {
    	
    }
	
}
