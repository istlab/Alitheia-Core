package eu.sqooss.plugins.git;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

@RunWith(MockitoJUnitRunner.class)
public class ActivatorTest {
	private Activator activator;
	
	private AlitheiaCore core;
	private TDSService tdsService;
	private UpdaterService upService;
	
	class TestableActivator extends Activator {
		@Override
		protected AlitheiaCore getAlitheiaCoreInstance() {
			return core;
		}
	}
	
	@Before
	public void setUp() {
		activator = new TestableActivator();
		
		core = mock(AlitheiaCore.class);
		tdsService = mock(TDSService.class);
		upService = mock(UpdaterService.class);
		
		when(core.getTDSService()).thenReturn(tdsService);
		when(core.getUpdater()).thenReturn(upService);
	}
	
	@Test 
	public void testStart() throws Exception {		
		activator.start(null);
		
		verify(tdsService).registerPlugin(new String[] {"git-file"}, GitAccessor.class);
		verify(upService).registerUpdaterService(GitUpdater.class);		
	}
	
	@Test
	public void testStop() throws Exception {
		activator.stop(null);
		
		verify(tdsService).unregisterPlugin(GitAccessor.class);
		verify(upService).unregisterUpdaterService(GitUpdater.class);
	}
}