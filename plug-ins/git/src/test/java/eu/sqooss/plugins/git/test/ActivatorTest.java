package eu.sqooss.plugins.git.test;

import eu.sqooss.plugins.git.Activator;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

import org.junit.Before;
import org.junit.Test;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class ActivatorTest {
	private Activator activator;
	
	private AlitheiaCore core;
	private TDSService tdsService;
	private UpdateService upService;
	
	@Before
	public void setUp() {
		activator = new Activator();

		mockStatic(AlitheiaCore.class);
		
		core =  mock(AlitheiaCore.class);
		tdsService = mock(TDSService.class);
		upService = mock(UpdateService.class);
	}
	
	@Test 
	public void testStart() throws Exception {		
		expect(AlitheiaCore.getInstance()).andReturn(core);
		expect(core.getTDSService()).andReturn(tdsService);
		expect(core.getUpdateService()).andReturn(upService);
		
		activator.start();
		
		verify(tdsService).registPlugin({"git-file"}, GitAccessor.class);
		verify(upService).registerUpdateDevice(GitUpdater.class);		
	}
	
	@Test
	public void testStop() throws Exception {
		expect(AlitheiaCore.getInstance()).andReturn(core);
		expect(core.getTDSService()).andReturn(tdsService);
		expect(core.getUpdateService()).andReturn(upService);
		
		activator.stop();
		
		verify(tdsService).unregisterPlugin(GitAccessor.class);
		verify(upService).unregisterUpdaterService(GitUpdater.class);
	}
}