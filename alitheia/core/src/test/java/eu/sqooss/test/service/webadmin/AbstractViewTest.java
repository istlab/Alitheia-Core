package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class AbstractViewTest {

	@Mock
	BundleContext bc;
	@Mock
	VelocityContext vc;
	AbstractView aw;

	@Test
	public void testAbstractView() {
		AlitheiaCore core = mock(AlitheiaCore.class);
		LogManager lm = mock(LogManager.class);
		DBService db = mock(DBService.class);
		MetricActivator ma = mock(MetricActivator.class);
		PluginAdmin pa = mock(PluginAdmin.class);
		Scheduler s = mock(Scheduler.class);
		TDSService tds = mock(TDSService.class);
		UpdaterService us = mock(UpdaterService.class);
		ClusterNodeService cn = mock(ClusterNodeService.class);
		SecurityManager sm = mock(SecurityManager.class);
		Logger logger = mock(Logger.class);

		when(core.getLogManager()).thenReturn(null, lm);
		when(lm.createLogger(anyString())).thenReturn(logger);

		reinitAbstractView();

		Field field;
		try {
			field = getField("sobjCore");

			// test with no AlitheiaCore initialization
			Assert.assertNull(field.get(aw));

			PowerMockito.mockStatic(AlitheiaCore.class);
			given(AlitheiaCore.getInstance()).willReturn(core);

			// test AlitheiaCore initialization
			reinitAbstractView();
			Assert.assertEquals(core, field.get(aw));

			when(core.getDBService()).thenReturn(db, null);
			when(core.getPluginAdmin()).thenReturn(pa, null);
			when(core.getScheduler()).thenReturn(s, null);
			when(core.getMetricActivator()).thenReturn(ma, null);
			when(core.getTDSService()).thenReturn(tds, null);
			when(core.getUpdater()).thenReturn(us, null);
			when(core.getClusterNodeService()).thenReturn(null, cn);
			when(core.getMetricActivator()).thenReturn(ma, null);
			when(core.getSecurityManager()).thenReturn(sm, null);
			doNothing().when(logger).debug(anyString());

			// test Logger manager and logger
			reinitAbstractView();
			field = getField("sobjLogManager");
			Assert.assertEquals(lm, field.get(aw));
			field = getField("sobjLogger");
			Assert.assertEquals(logger, field.get(aw));

			// test all the other fields
			field = getField("sobjDB");
			Assert.assertEquals(db, field.get(aw));
			field = getField("sobjPA");
			Assert.assertEquals(pa, field.get(aw));
			field = getField("sobjSched");
			Assert.assertEquals(s, field.get(aw));
			field = getField("compMA");
			Assert.assertEquals(ma, field.get(aw));
			field = getField("sobjTDS");
			Assert.assertEquals(tds, field.get(aw));
			field = getField("sobjUpdater");
			Assert.assertEquals(us, field.get(aw));
			field = getField("sobjSecurity");
			Assert.assertEquals(sm, field.get(aw));

			reinitAbstractView();
			field = getField("sobjClusterNode");
			Assert.assertEquals(cn, field.get(aw));
			verify(logger, times(8)).debug(anyString());

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testResourceGetters() {
		Assert.assertEquals("message", AbstractView.getMsg("message"));
		Assert.assertEquals("message", AbstractView.getErr("message"));
		Assert.assertEquals("message", AbstractView.getLbl("message"));
	}

	@Test
	public void testGetMsg() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getMsg("another message"));
		Assert.assertEquals("Check log for details.",
				AbstractView.getMsg("m0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getMsg(null));
	}

	@Test
	public void testGetErr() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getErr("another message"));
		Assert.assertEquals("Can not add this user to the selected group!",
				AbstractView.getErr("e0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getErr(null));
	}

	@Test
	public void testGetLbl() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getLbl("another message"));
		Assert.assertEquals("Alitheia", AbstractView.getLbl("l0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getLbl(null));
	}

	@Test
	public void testGetMessagesBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.ENGLISH));
	}

	@Test
	public void testGetErrorsBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.ENGLISH));
	}

	@Test
	public void testGetLabelsBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.ENGLISH));
	}

	@Test
	public void testDebugRequest() {
		Method method = getMethod("debugRequest", HttpServletRequest.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Enumeration<Object> e = new StringTokenizer("name1 name2 name3");
		String expected = "name1=null<br/>\nname2=null<br/>\nname3=null<br/>\n";
		when(request.getParameterNames()).thenReturn(e);
		try {
			Assert.assertEquals(expected, method.invoke(aw, request));
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void testFromString() {
		Method method = getMethod("fromString", String.class);
		try {
			Assert.assertEquals(null, method.invoke(aw, "zbla"));
			Assert.assertEquals((long)1349504334, method.invoke(aw, "1349504334"));
			Assert.assertEquals((long)-1349504334, method.invoke(aw, "-1349504334"));
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testCheckName() {
		Method method = getMethod("checkName", String.class);
		try {
			String text = null;
			Assert.assertFalse((boolean)method.invoke(aw, text));
			Assert.assertFalse((boolean)method.invoke(aw, " aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa "));
			Assert.assertTrue((boolean)method.invoke(aw, "alphabet"));
			Assert.assertTrue((boolean)method.invoke(aw, "123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanumeric123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanum eric123"));
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testCheckProjectName() {
		Method method = getMethod("checkProjectName", String.class);
		try {
			String text = null;
			Assert.assertFalse((boolean)method.invoke(aw, text));
			Assert.assertFalse((boolean)method.invoke(aw, " aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa "));
			Assert.assertFalse((boolean)method.invoke(aw, "_aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa_"));
			Assert.assertFalse((boolean)method.invoke(aw, "-aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa-"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphabet"));
			Assert.assertTrue((boolean)method.invoke(aw, "123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanumeric123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanum eric123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanum-eric123"));
			Assert.assertTrue((boolean)method.invoke(aw, "alphanum_eric123"));
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testCheckEmail() {
		Method method = getMethod("checkEmail", String.class);
		try {
			String text = null;
			Assert.assertFalse((boolean)method.invoke(aw, text));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa..aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa@aa@a"));
			Assert.assertFalse((boolean)method.invoke(aw, ".aaa@aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa@.aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa.@aaa"));
			Assert.assertFalse((boolean)method.invoke(aw, "aaa@aaa."));
			Assert.assertTrue((boolean)method.invoke(aw, "email@example.com"));
			Assert.assertTrue((boolean)method.invoke(aw, "_______@example.com"));
			Assert.assertTrue((boolean)method.invoke(aw, "1234567890@example.com"));
			Assert.assertFalse((boolean)method.invoke(aw, "Joe Smith <email@example.com>"));
			Assert.assertFalse((boolean)method.invoke(aw, "email@example"));
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testCheckTDSUrl() {
		Method method = getMethod("checkTDSUrl", String.class);
		TDSService tds = mock(TDSService.class);
		when(tds.isURLSupported(anyString())).thenReturn(true);
		try {
			Assert.assertTrue((boolean)method.invoke(aw, "url"));
			verify(tds, times(1)).isURLSupported(anyString());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testGetUptime() {
		String uptimeTruth = "25:02:03:02";

		long days = 25L * 24L * 60L * 60L * 1000L;
		long hours = 2L * 60L * 60L * 1000L;
		long minutes = 2L * 60L * 1000L;
		long seconds = 62L * 1000L;
		long dateEntry = new Date().getTime() - days - hours - minutes
				- seconds;

		Whitebox.setInternalState(AbstractView.class, dateEntry);
		String uptime = AbstractView.getUptime();
		assertEquals(uptimeTruth, uptime);
	}

	protected void reinitAbstractView() {
		aw = new AbstractView(bc, vc) {

			@Override
			public void exec(HttpServletRequest req) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	protected Field getField(String name) {
		Field field = null;
		try {
			field = AbstractView.class.getDeclaredField(name);
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}

	protected Method getMethod(String name, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = AbstractView.class.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return method;
	}

}
