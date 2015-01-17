package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class})
public class AdminServletTest {

	@Mock private BundleContext bc;
	@Mock private WebadminService webadmin;
	@Mock private Logger logger;
	@Mock private VelocityEngine ve;
	@Mock private AlitheiaCore core;
	@Mock private DBService dbs;
	@Mock private AdminService as;
	@Mock private AdminAction aa;
	@Mock private Scheduler scheduler;
	@Mock private VelocityContext vc;
	@Mock private HttpServletRequest request;
	@Mock private HttpServletResponse response;
	@Mock private Template template;
	@Mock private PrintWriter writer;
	@Mock private ServletOutputStream ostream;
	
	private AdminServlet servlet;
	
	private void initServlet() {
		mockStatic(AlitheiaCore.class);
				
		when(AlitheiaCore.getInstance()).thenReturn(core);	
		when(core.getDBService()).thenReturn(dbs);
		when(core.getAdminService()).thenReturn(as);
		when(as.create(any(String.class))).thenReturn(aa);
		
		servlet = new AdminServlet(bc, webadmin, logger, ve);

		Whitebox.setInternalState(servlet, vc);
		
		Whitebox.setInternalState(AbstractView.class, scheduler);
		when(scheduler.getSchedulerStats()).thenReturn(new SchedulerStats());
	}
  
	
	@Test
	public void testConstructor() {
		initServlet();
		assertNotNull(servlet);

		Hashtable<String, Pair<String, AbstractView>> dynContent = 
				Whitebox.getInternalState(servlet, "dynamicContentMap");
		Hashtable<String, Pair<String, String>> statContent = 
				Whitebox.getInternalState(servlet, "staticContentMap");

		assertEquals(14, dynContent.size());
		assertEquals(15, statContent.size());
	}
	
	@Test
	public void testCreateSubstitutions() throws Exception {
		initServlet();

		Whitebox.invokeMethod(servlet, "createSubstitutions", request);
		
		verify(vc, times(4)).put(any(String.class), any(Object.class));
	}

	@Test
	public void testSendPageException() throws Exception {
		initServlet();
		
		String path = "";
		when(ve.getTemplate(any(String.class))).thenThrow(new IllegalArgumentException());
		Whitebox.invokeMethod(servlet, "sendPage", response, request, path);
		
		//verify that response status is set correctly
		verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	

	@Test
	public void testSendPage() throws ResourceNotFoundException, ParseErrorException, Exception {
		initServlet();
		
		String path = "";
		
		when(ve.getTemplate(any(String.class))).thenReturn(template);
		when(response.getWriter()).thenReturn(writer);
		
		Whitebox.invokeMethod(servlet, "sendPage", response, request, path);
		
		//Verify that merge was called on template
		verify(template, times(1)).merge(any(VelocityContext.class), any(PrintWriter.class));
		//verify that print was called on writer
		verify(writer, times(1)).print(any(String.class));
			
	}

	@Test
	public void testSendResource() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);
		
		String sourceFile = "/sqo-oss.png";
		String contentType = "image/x-png";
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
		
		//verify that whole image was been sent
		verify(ostream, times(6)).write(any(byte[].class), any(int.class), any(int.class));
	}


	@Test(expected=IOException.class)
	public void testSendResourceNotFound() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);
		
		String sourceFile = "Non existing file";
		String contentType = "";
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
	}

	@Test
	public void testDoPost() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("Non existing path");
		Whitebox.invokeMethod(servlet, "doPost", request, response);
		
		//verify that response status is set correctly
		verify(dbs, times(2)).isDBSessionActive();		
	}
	

	@Test
	public void testDoGet() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("Non existing path");
		Whitebox.invokeMethod(servlet, "doGet", request, response);
		
		//verify that response status is set correctly
		verify(dbs, times(2)).isDBSessionActive();		
	}
	
	@Test
	public void testHandleRequestNonExistingPath() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("Non existing path");
		Whitebox.invokeMethod(servlet, "handleRequest", request, response);
		
		//verify that response status is set correctly
		verify(dbs, times(2)).isDBSessionActive();	
	}

	@Test
	public void testHandleRequestDynamic() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("/jobs");
		when(ve.getTemplate(any(String.class))).thenReturn(template);
		when(response.getWriter()).thenReturn(writer);

		Whitebox.invokeMethod(servlet, "handleRequest", request, response);
		
		//Verify that merge was called on template
		verify(template, times(1)).merge(any(VelocityContext.class), any(PrintWriter.class));
		//verify that print was called on writer
		verify(writer, times(1)).print(any(String.class));
	}
	
	@Test
	public void testHandleRequestStatic() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("/sqo-oss.png");
		when(response.getOutputStream()).thenReturn(ostream);

		Whitebox.invokeMethod(servlet, "handleRequest", request, response);
		
		//verify that whole image was been sent
		verify(ostream, times(6)).write(any(byte[].class), any(int.class), any(int.class));
	}
}


