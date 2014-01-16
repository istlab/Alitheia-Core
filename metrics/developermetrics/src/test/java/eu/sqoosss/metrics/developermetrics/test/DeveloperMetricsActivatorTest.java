package eu.sqoosss.metrics.developermetrics.test;

import static org.junit.Assert.*;

import java.util.Dictionary;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.developermetrics.Developermetrics;
import eu.sqooss.metrics.developermetrics.DevelopermetricsActivator;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.db.Tag;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class, LogManager.class })
public class DeveloperMetricsActivatorTest {

	static DevelopermetricsActivator testObject;

	@BeforeClass
	public static void setUp() {
		// Setup the AlitheiaCore class for testing
			
				PowerMockito.mockStatic(AlitheiaCore.class);
				PowerMockito.mockStatic(DBService.class);
				AlitheiaCore core = mock(AlitheiaCore.class);
				DBService dbService = mock(DBService.class);
				LogManager logManager = mock(LogManager.class);
				when(AlitheiaCore.getInstance()).thenReturn(core);
				when(core.getDBService()).thenReturn(dbService);
				when(core.getLogManager()).thenReturn(logManager);
				when(logManager.createLogger(anyString())).thenReturn(mock(Logger.class));
				testObject = new DevelopermetricsActivator();
	}
	
	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStartStopMethod() throws Exception {
	
		Developermetrics metrics  = mock(Developermetrics.class);
		PowerMockito.whenNew(Developermetrics.class).withAnyArguments().thenReturn(metrics);
		BundleContext mockedBC = mock(BundleContext.class);
		when(mockedBC.registerService(eq(Developermetrics.class.getName()), any(Developermetrics.class), any(Dictionary.class))).thenReturn(mock(ServiceRegistration.class));
		//Run the start test
		testObject.start(mockedBC);
		
		ServiceRegistration actualValue = Whitebox.<ServiceRegistration>getInternalState(testObject, "registration");
		//Verify the service was registered
		assertNotNull(actualValue);
		
		verify(actualValue, times(0)).unregister(); 
		testObject.stop(mockedBC);
		//Verify if the unregister method was called
		verify(actualValue, times(1)).unregister(); 
		
	}
	
	
}
