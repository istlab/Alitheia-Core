/**
 * 
 */
package eu.sqooss.test.service.webadmin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Dictionary;

import javax.servlet.Servlet;

import org.apache.velocity.app.VelocityEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.logging.Logger;


/**
 * @author elwin
 * comment out @PrepareForTest annotation in order to see coverage. Lets 
 * tests fail.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WebadminServiceImpl.class)
public class WebadminServiceImplTest {

	WebadminServiceImpl instance;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		instance = new WebadminServiceImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		instance = null;
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebadminServiceImpl#getMessageOfTheDay()}.
	 */
	@Test
	public void testGetMessageOfTheDay() {
		assertThat(instance.getMessageOfTheDay(),nullValue());
		Whitebox.setInternalState(instance, String.class, "myMessage");
		assertThat(instance.getMessageOfTheDay(),equalTo("myMessage"));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebadminServiceImpl#setMessageOfTheDay(java.lang.String)}.
	 */
	@Test
	public void testSetMessageOfTheDay() {
		assertThat(Whitebox.getInternalState(instance, String.class),nullValue());
		instance.setMessageOfTheDay("myMessage");
		assertThat(Whitebox.getInternalState(instance, String.class),equalTo("myMessage"));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebadminServiceImpl#setInitParams(org.osgi.framework.BundleContext, eu.sqooss.service.logging.Logger)}.
	 */
	@Test
	public void testSetInitParams() {
		BundleContext bc = mock(BundleContext.class);
		Logger l = mock(Logger.class);
		instance.setInitParams(bc,l);
		assertThat(Whitebox.getInternalState(instance, BundleContext.class),equalTo(bc));
		assertThat(Whitebox.getInternalState(instance, Logger.class),equalTo(l));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebadminServiceImpl#startUp()}.
	 * @throws Exception 
	 */
	@Test
	public void testStartUp() throws Exception {
		BundleContext bc = mock(BundleContext.class);
		Logger l = mock(Logger.class);
		instance.setInitParams(bc,l);
		Boolean result = instance.startUp();
		assertThat(result,equalTo(false));
		ServiceReference serviceReference = mock(ServiceReference.class);
		when(bc.getServiceReference(anyString())).thenReturn(serviceReference);
		
		result = instance.startUp();
		assertThat(result,equalTo(true));
		assertThat(Whitebox.getInternalState(instance, VelocityEngine.class),notNullValue());

		
		HttpService service = mock(HttpService.class);
		when(bc.getService(any(ServiceReference.class))).thenReturn(service);
		
		//comment @PrepareForTest annotation in order to see coverage. Lets this
		//test fail
		AdminServlet adminServlet = mock(AdminServlet.class);
		whenNew(AdminServlet.class).withAnyArguments().thenReturn(adminServlet);
		result = instance.startUp();
		verify(service).registerServlet(anyString(), any(Servlet.class), any(Dictionary.class), any(HttpContext.class));
		assertThat(result,equalTo(true));
		
		doThrow(new NullPointerException("Test")).when(service).registerServlet(anyString(), any(Servlet.class), any(Dictionary.class), any(HttpContext.class));
		result = instance.startUp();
		assertThat(result,equalTo(false));
		
	}

}
