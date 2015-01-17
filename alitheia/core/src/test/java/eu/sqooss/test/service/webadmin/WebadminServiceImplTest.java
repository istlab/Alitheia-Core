package eu.sqooss.test.service.webadmin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Hashtable;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.apache.velocity.app.VelocityEngine;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.logging.Logger;

/**
 *
 * @author Adam Kucera
 */
public class WebadminServiceImplTest {
    
    static WebadminServiceImpl impl;
    @Mock BundleContext bc;
    @Mock Logger logger;
    
	@BeforeClass
	public static void setUp() {
		impl = spy(new WebadminServiceImpl());
	}

    @Test
    public void testWebadminServiceImpl() {
        assertNotNull(impl);
    }
    
    @Test
    public void testShutDown() {
    }
    
    @Test
    public void testMessageOfTheDay() {
        assertNull(impl.getMessageOfTheDay());
        impl.setMessageOfTheDay("message");
        assertEquals("message", impl.getMessageOfTheDay());
        assertNotSame("different message", impl.getMessageOfTheDay());
        impl.setMessageOfTheDay("another message");
        assertEquals("another message", impl.getMessageOfTheDay());
        assertNotSame("message", impl.getMessageOfTheDay());
    }
    
    @Test
    public void testStartUpFailure() {
    	initStartUpTests();
    	
        when(bc.getServiceReference(HttpService.class.getName())).thenReturn(null);
        Assert.assertFalse(impl.startUp());
    }
    
    @Test
    public void testStartUpSuccess() {
    	initStartUpTests();

        AdminServlet servlet = mock(AdminServlet.class);
        ServiceReference sr = mock(ServiceReference.class);
        HttpService httpservice = mock(HttpService.class);

        when(bc.getServiceReference(HttpService.class.getName())).thenReturn(sr);
        when(bc.getService(any(ServiceReference.class))).thenReturn(null, httpservice, httpservice);
        doReturn(servlet).when(impl).makeAdminServlet(
        		eq(bc), eq(impl), eq(logger), any(VelocityEngine.class)); 
        try {
			doNothing().when(httpservice).registerServlet(anyString(),
					any(AdminServlet.class), any(Hashtable.class), (HttpContext) any());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
        //test with the null value of http service
        Assert.assertTrue(impl.startUp());
        //test with httpservice value
        Assert.assertTrue(impl.startUp());
        //test exception
        try {
			doThrow(new RuntimeException()).when(httpservice).registerServlet(anyString(),
					eq(servlet), any(Hashtable.class), (HttpContext) any());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
        Assert.assertFalse(impl.startUp());
        
    }
    
    protected void initStartUpTests() {
    	MockitoAnnotations.initMocks(this);
    	
    	impl.setInitParams(bc, logger);
    }
}
