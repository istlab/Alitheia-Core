package eu.sqooss.test.service.rest;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import static org.mockito.Mockito.*;

import eu.sqooss.impl.service.rest.ResteasyServiceImpl;
import eu.sqooss.impl.service.rest.ResteasyServletFactory;
import eu.sqooss.service.logging.Logger;

public class ResteasyServiceImplTest {

	@Test
	public void testFactoryInject() {
		ResteasyServletFactory rasf = mock(ResteasyServletFactory.class);
		ResteasyServiceImpl re = new ResteasyServiceImpl(rasf);
		
		BundleContext bc = mock(BundleContext.class);
		ServiceReference sr = mock(ServiceReference.class);
		when(bc.getServiceReference(
				HttpService.class.getName())).thenReturn(sr);
		
		HttpService hs = mock(HttpService.class);
		when(bc.getService(sr)).thenReturn(hs);
		
		Logger l = mock(Logger.class);
		
		re.setInitParams(bc, l);
		re.addResource(eu.sqooss.rest.api.StoredProjectResource.class);
		
		verify(rasf).create();
	}
}
