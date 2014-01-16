package eu.sqooss.test.service.webadmin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * Tests for WebadminService
 */
@RunWith(PowerMockRunner.class)
public class WebadminServiceTest {

	private WebadminService testee;

	@Mock private HttpService mockHTTPService;
	@Mock private Logger mockLog;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(WebadminServiceTest.class);

		testee = new WebadminServiceImpl();
		BundleContext mockBC = mock(BundleContext.class);

		// Deep mock AlitheiaCore and replace the core field of WebadminServiceImpl
		// VERY UGLY (and implementation specific)
		// TODO: Replace with dependency injection
		AlitheiaCore mockAC = mock(AlitheiaCore.class, Mockito.RETURNS_DEEP_STUBS);
		when(mockAC.getLogManager().createLogger(Mockito.anyString())).thenReturn(mockLog);
		Whitebox.setInternalState(testee, "core", mockAC);

		// Mock the HTTPServiceRef
		ServiceReference httpServRef = mock(ServiceReference.class);
		when(mockBC.getServiceReference(HttpService.class.getName())).thenReturn(httpServRef);
		when(mockBC.getService(httpServRef)).thenReturn(mockHTTPService);

		testee.setInitParams(mockBC, mockLog);
	}

	@Test
	public void testShutDown() {
		testee.startUp();
		testee.shutDown();
		// Verify that at least a root servlet was unregistered
		verify(mockHTTPService).unregister("/");
	}

	/**
	 * Test calling shutdown when never started up, this should silently fail/do nothing
	 */
	@Test
	public void testInvalidShutdown() {
		testee.shutDown();
	}

	/**
	 * Test calling shutdown when already shut down, this should silently fail/do nothing
	 */
	@Test
	public void testDoubleShutdown() {
		testee.startUp();
		testee.shutDown();
		testee.shutDown();
	}

	@Test
	public void testStartUp() throws ServletException, NamespaceException {
		testee.startUp();
		// Verify that at least a root servlet was added
		verify(mockHTTPService).registerServlet(Mockito.eq("/"), Mockito.any(HttpServlet.class), Mockito.any(Dictionary.class), Mockito.any(HttpContext.class));
	}
}
