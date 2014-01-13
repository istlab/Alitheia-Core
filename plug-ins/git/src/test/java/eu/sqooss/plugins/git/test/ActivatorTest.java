package eu.sqooss.plugins.git.test;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.git.Activator;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class ActivatorTest {
	private Activator activator;
	
	private AlitheiaCore core;
	private TDSService tdsService;
	private UpdaterService upService;
	
	@Before
	public void setUp() {
		activator = new Activator();

		mockStatic(AlitheiaCore.class);
		
		core =  mock(AlitheiaCore.class);
		tdsService = mock(TDSService.class);
		upService = mock(UpdaterService.class);
	}
	
	@Test 
	public void testStart() throws Exception {		
		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getTDSService()).thenReturn(tdsService);
		when(core.getUpdater()).thenReturn(upService);
		
		activator.start(null);
		
		verify(tdsService).registerPlugin(new String[] {"git-file"}, GitAccessor.class);
		verify(upService).registerUpdaterService(GitUpdater.class);		
	}
	
	@Test
	public void testStop() throws Exception {
		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getTDSService()).thenReturn(tdsService);
		when(core.getUpdater()).thenReturn(upService);
		
		activator.stop(null);
		
		verify(tdsService).unregisterPlugin(GitAccessor.class);
		verify(upService).unregisterUpdaterService(GitUpdater.class);
	}
}