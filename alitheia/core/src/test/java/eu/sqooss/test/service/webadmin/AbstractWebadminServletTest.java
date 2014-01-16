package eu.sqooss.test.service.webadmin;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.AbstractWebadminServlet;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;

/**
 * Sets up things needed for all Servlet test and contains tests all Servlets should pass
 */
public abstract class AbstractWebadminServletTest {
	// Mock request and response
	@Mock protected HttpServletRequest mockReq;
	@Mock protected HttpServletResponse mockResp;

	// Output of the response
	private StringWriter responseOutput;

	// All the dependencies of servlets
	@Mock protected AlitheiaCore mockAC;
	@Mock protected Logger mockLog;
	@Mock protected DBService mockDB;
	private boolean dbTransactionStarted = false;
	@Mock protected PluginAdmin mockPA;
	@Mock protected MetricActivator mockMA;

	/**
	 * Real VelocityEngine
	 */
	protected VelocityEngine ve;

	@Before
	public void setUp() throws Exception {
		ve = createVelocity();
		MockitoAnnotations.initMocks(AbstractWebadminServlet.class);

		// Create a byte output stream for the response so the response can be verified
		responseOutput = new StringWriter();
		//responseOutput = new StubServletOutputStream();
		//when(mockResp.getOutputStream()).thenReturn(responseOutput);
		when(mockResp.getWriter()).thenReturn(new PrintWriter(responseOutput));
		// Mock logger
		LogManager mockLM = mock(LogManager.class);
		when(mockAC.getLogManager()).thenReturn(mockLM);
		when(mockLM.createLogger(Logger.NAME_SQOOSS_WEBADMIN)).thenReturn(mockLog);
		//Logger real = new LoggerImpl(Logger.NAME_SQOOSS_WEBADMIN);
		//when(mockLM.createLogger(Logger.NAME_SQOOSS_WEBADMIN)).thenReturn(real);
		// Mock DB
		initDBmock();
		// Mock PluginAdmin
		when(mockAC.getPluginAdmin()).thenReturn(mockPA);
		// Mock MetricActivator
		when(mockAC.getMetricActivator()).thenReturn(mockMA);
	}

	/**
	 * Verify that every servlet created and commited a DB transaction
	 */
	@After
	public void verifyDBTransaction() {
		verify(mockDB).startDBSession();
		verify(mockDB).commitDBSession();
	}

	/**
	 * Get the output of the mock http response
	 */
	protected String getResponseOutput() {
		//return responseOutput.getOutput();
		return responseOutput.toString();
	}

	private VelocityEngine createVelocity() {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
		ve.setProperty("runtime.log.logsystem.log4j.category",
				Logger.NAME_SQOOSS_WEBADMIN);
		String resourceLoader = "classpath";
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
		ve.setProperty(resourceLoader + "." + RuntimeConstants.RESOURCE_LOADER + ".class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return ve;
	}

	/**
	 * Basic removal of HTML and whitespace suitable for testing if output contains something
	 */
	protected static String stripHTMLandWhitespace(String output) {
		return output.replaceAll("\\<[^>]+>","").replaceAll("\\s+","");
	}

	/**
	 * Initialize correct state behavior for the database mock
	 */
	private void initDBmock() {
		when(mockAC.getDBService()).thenReturn(mockDB);
		when(mockDB.startDBSession()).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if(dbTransactionStarted)
					return false;
				else {
					dbTransactionStarted = true;
					return true;
				}
			}
		});
		when(mockDB.commitDBSession()).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if(dbTransactionStarted) {
					dbTransactionStarted = false;
					return true;
				} else
					return false;
			}
		});
		when(mockDB.isDBSessionActive()).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return dbTransactionStarted;
			}

		});
	}
}
