/**
 * 
 */
package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.sql.Time;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.sqooss.impl.service.webadmin.AdminServlet;

/**
 * @author elwin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class})
public class AdminServletTest {
	BundleContext bc;
	WebadminService webadmin;
	Logger logger;
	DBService db;
	VelocityContext vc;
	VelocityEngine ve;
	AlitheiaCore core;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bc = Mockito.mock(BundleContext.class);
		webadmin = Mockito.mock(WebadminService.class);
		logger = Mockito.mock(Logger.class);
		db = Mockito.mock(DBService.class);
		ve = Mockito.mock(VelocityEngine.class);
		core = Mockito.mock(AlitheiaCore.class);
		mockStatic(AlitheiaCore.class);
		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getDBService()).thenReturn(db);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#AdminServlet(org.osgi.framework.BundleContext, eu.sqooss.service.webadmin.WebadminService, eu.sqooss.service.logging.Logger, org.apache.velocity.app.VelocityEngine)}.
	 */
	@Test
	public void testConstructor() {
		AdminServlet adminServlet = new AdminServlet(bc, webadmin, logger, ve);
		verify(core,times(1)).getDBService();
		
		Hashtable<String, Pair<String, String>> staticContentMap = Whitebox.getInternalState(adminServlet, "staticContentMap");
		Hashtable<String, String> dynamicContentMap = Whitebox.getInternalState(adminServlet, "dynamicContentMap");
		
		// staticContentMap should contain 15 Elements 
		assertNotNull(staticContentMap);
		assertEquals(15, staticContentMap.size());
		
		// dynamicContentMap should contain 10 elements
		assertNotNull(dynamicContentMap);
		assertEquals(10, dynamicContentMap.size());
		
		// check for correct element in staticContentMap
		assertEquals(new Pair<String, String> ("/screen.css","text/css"), staticContentMap.get("/screen.css"));
		assertEquals(new Pair<String, String> ("/screen.css","text/css"), staticContentMap.get("/screen.css"));
		assertEquals(new Pair<String, String> ("/webadmin.css", "text/css"), staticContentMap.get("/webadmin.css"));
		assertEquals(new Pair<String, String> ("/sqo-oss.png", "image/x-png"), staticContentMap.get("/sqo-oss.png"));
		assertEquals(new Pair<String, String> ("/queue.png", "image/x-png"), staticContentMap.get("/queue.png"));
		assertEquals(new Pair<String, String> ("/uptime.png", "image/x-png"), staticContentMap.get("/uptime.png"));
		assertEquals(new Pair<String, String> ("/greyBack.jpg", "image/x-jpg"), staticContentMap.get("/greyBack.jpg"));
		assertEquals(new Pair<String, String> ("/projects.png", "image/x-png"), staticContentMap.get("/projects.png"));
		assertEquals(new Pair<String, String> ("/logs.png", "image/x-png"), staticContentMap.get("/logs.png"));
		assertEquals(new Pair<String, String> ("/metrics.png", "image/x-png"), staticContentMap.get("/metrics.png"));
		assertEquals(new Pair<String, String> ("/gear.png", "image/x-png"), staticContentMap.get("/gear.png"));
		assertEquals(new Pair<String, String> ("/header-repeat.png", "image/x-png"), staticContentMap.get("/header-repeat.png"));
		assertEquals(new Pair<String, String> ("/add_user.png", "image/x-png"), staticContentMap.get("/add_user.png"));
		assertEquals(new Pair<String, String> ("/edit.png", "image/x-png"), staticContentMap.get("/edit.png"));
		assertEquals(new Pair<String, String> ("/jobs.png", "image/x-png"), staticContentMap.get("/jobs.png"));
		assertEquals(new Pair<String, String> ("/rules.png", "image/x-png"), staticContentMap.get("/rules.png"));
		
		// check for correct elements in dynamicContentMap
		assertEquals("index.html", dynamicContentMap.get("/"));
		assertEquals("/index", "index.html", dynamicContentMap.get("/index"));
		assertEquals("/projects", "projects.html", dynamicContentMap.get("/projects"));
		assertEquals("/projectlist", "projectslist.html", dynamicContentMap.get("/projectlist"));
		assertEquals("/logs", "logs.html", dynamicContentMap.get("/logs"));
		assertEquals("/jobs", "jobs.html", dynamicContentMap.get("/jobs"));
		assertEquals("/alljobs", "alljobs.html", dynamicContentMap.get("/alljobs"));
		assertEquals("/users", "users.html", dynamicContentMap.get("/users"));
		assertEquals("/rules", "rules.html", dynamicContentMap.get("/rules"));
		assertEquals("/jobstat", "jobstat.html", dynamicContentMap.get("/jobstat"));
		
		// vc, admindView, pluginsView and projectsView should be initialized
		assertNotNull(Whitebox.getInternalState(adminServlet, "vc")); 
		assertNotNull(Whitebox.getInternalState(adminServlet, "adminView")); 
		assertNotNull(Whitebox.getInternalState(adminServlet, "pluginsView")); 
		assertNotNull(Whitebox.getInternalState(adminServlet, "projectsView")); 
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddStaticContent() throws Exception {
		AdminServlet adminServlet = new AdminServlet(bc, webadmin, logger, ve);
		verify(core,times(1)).getDBService();
		
		Hashtable<String, Pair<String, String>> staticContentMap = Whitebox.getInternalState(adminServlet, "staticContentMap");
//		System.out.println(staticContentMap.get("/myTest"));
		assertNull(staticContentMap.get("/myTest"));
		
		// now add /myTest
		Whitebox.invokeMethod(adminServlet, "addStaticContent","/myTest","myType");
		assertNotNull(staticContentMap.get("/myTest"));
		assertEquals(new Pair<String, String> ("/myTest", "myType"), staticContentMap.get("/myTest"));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws Exception 
	 */
	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		
		AdminServlet adminServlet = new AdminServlet(bc, webadmin, logger, ve);
		AdminServlet spy = spy(adminServlet);
		
		doNothing().when(spy,"sendPage",any(HttpServletResponse.class),any(HttpServletRequest.class), anyString());
		
		// simulate a non active db session
		when(db.isDBSessionActive()).thenReturn(false);
		
		// Case 1: query = null
		// simulate `doGet'
		Whitebox.invokeMethod(adminServlet, "doGet", request, response);
		
		// dbSession is not active, so `doGet' is supposed to start it
		verify(db,times(1)).startDBSession();
		
		// no active session => no commit
		verify(db,times(0)).commitDBSession();
		
		// no query => no sendPage, but nullpointer exception
		verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		verifyPrivate(spy,times(0)).invoke("sendPage",eq(response), eq(request), anyString());
		
		// simulate an active db session
		when(db.isDBSessionActive()).thenReturn(true);
		Whitebox.invokeMethod(spy, "doGet", request, response);
		
		// db.startDBSession() should not be called now (since it is active already)
		// so still one time 1 called
		verify(db,times(1)).startDBSession();
		
		// Case 2: query starts with "/stop"
		when(request.getPathInfo()).thenReturn("/stopIt");
		
		Bundle bundle = Mockito.mock(Bundle.class);
		when(bc.getBundle(0)).thenReturn(bundle);
		Whitebox.invokeMethod(spy, "doGet", request, response);
		
		// we should have called sendPage for the stopQuery after invoke
		verifyPrivate(spy,times(1)).invoke("sendPage",eq(response), eq(request), anyString());
		
		verify(bc,times(1)).getBundle(0);
		verify(bundle,times(1)).stop();
		
		doThrow(new BundleException(null)).when(bundle).stop();
		Whitebox.invokeMethod(spy, "doGet", request, response);
		verify(bc,times(2)).getBundle(0);
		verify(bundle,times(2)).stop();
				
		// Case 3: query starts with restart
		when(request.getPathInfo()).thenReturn("/restartIt");
		
		Whitebox.invokeMethod(spy, "doGet", request, response);
		verifyPrivate(spy,times(3)).invoke("sendPage",eq(response), eq(request), anyString());
		verify(bc,times(2)).getBundle(0);
		verify(bundle,times(2)).stop();
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#sendResource(javax.servlet.http.HttpServletResponse, eu.sqooss.service.util.Pair)}.
	 */
	@Test
	public void testSendResource() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.AdminServlet#sendPage(javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpServletRequest, java.lang.String)}.
	 */
	@Test
	public void testSendPage() {
		fail("Not yet implemented"); // TODO
	}

}
