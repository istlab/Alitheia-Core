package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import static org.mockito.Mockito.*;

import eu.sqooss.impl.service.webadmin.AdminServletFactory;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.webadmin.WebadminService;

public class WebadminServiceImplTest {

	@Test
	public void testAdminServletFactoryInjection() {
		AdminServletFactory asf = mock(AdminServletFactory.class);
		WebadminServiceImpl was = new WebadminServiceImpl(asf);
		BundleContext bc = mock(BundleContext.class);
	
		HttpService hs = mock(HttpService.class);
		ServiceReference sr = mock(ServiceReference.class);
		when(bc.getServiceReference(anyString())).thenReturn(sr);
		when(bc.getService(any(ServiceReference.class))).thenReturn(hs);
		
		Logger l = mock(Logger.class);
		
		was.setInitParams(bc, l);
		was.startUp();
		
		verify(asf).create(any(BundleContext.class), any(WebadminService.class),
				any(Logger.class), any(VelocityEngine.class));
	}

}
