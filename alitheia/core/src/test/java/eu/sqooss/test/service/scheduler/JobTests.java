package eu.sqooss.test.service.scheduler;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import eu.sqooss.service.scheduler.Job;

@PrepareForTest(Job.class)
public class JobTests {
	@Mock
	protected static Job job;

	@BeforeClass
    public static void setUp() {
        job = new TestJob(10, "aaa");
    }
	
	@Test
	public void testGetJobType() {
		String type = "eu.sqooss.test.service.scheduler.TestJob";
		Assert.assertTrue(job.getJobType().startsWith(type));
	}
	
	@Test
	public void testGetExceptionType() {
		Assert.assertEquals("<b>NA</b>", job.getExceptionType());
		Exception e = new IllegalArgumentException("Exception text");
		setPrivateField("m_errorException", job, e);
		Assert.assertEquals("java.lang. IllegalArgumentException", job.getExceptionType());
	}
	
	@Test
	public void testGetExceptionText() {
		Assert.assertEquals("<b>NA</b>", job.getExceptionText());
		Exception e = new IllegalArgumentException("Exception text");
		setPrivateField("m_errorException", job, e);
		Assert.assertEquals("Exception text", job.getExceptionText());
	}
	
	@Test
	public void testGetExceptionBacktrace() {
		
		Assert.assertEquals("<b>NA</b>", job.getExceptionBacktrace());
		Exception e = new IllegalArgumentException("Exception text");
		setPrivateField("m_errorException", job, e);
		String backtrace = "eu.sqooss.test.service.scheduler.JobTests. testGetExceptionBacktrace(), (JobTests.java:80)<br/>sun.reflect.NativeMethodAccessorImpl. invoke0(), (NativeMethodAccessorImpl.java:-2)<br/>sun.reflect.NativeMethodAccessorImpl. invoke(), (NativeMethodAccessorImpl.java:57)<br/>sun.reflect.DelegatingMethodAccessorImpl. invoke(), (DelegatingMethodAccessorImpl.java:43)<br/>java.lang.reflect.Method. invoke(), (Method.java:606)<br/>org.junit.runners.model.FrameworkMethod$1. runReflectiveCall(), (FrameworkMethod.java:44)<br/>org.junit.internal.runners.model.ReflectiveCallable. run(), (ReflectiveCallable.java:15)<br/>org.junit.runners.model.FrameworkMethod. invokeExplosively(), (FrameworkMethod.java:41)<br/>org.junit.internal.runners.statements.InvokeMethod. evaluate(), (InvokeMethod.java:20)<br/>org.junit.internal.runners.statements.RunBefores. evaluate(), (RunBefores.java:28)<br/>org.junit.internal.runners.statements.RunAfters. evaluate(), (RunAfters.java:31)<br/>org.junit.runners.BlockJUnit4ClassRunner. runChild(), (BlockJUnit4ClassRunner.java:70)<br/>org.junit.runners.BlockJUnit4ClassRunner. runChild(), (BlockJUnit4ClassRunner.java:44)<br/>org.junit.runners.ParentRunner. runChildren(), (ParentRunner.java:180)<br/>org.junit.runners.ParentRunner. access$000(), (ParentRunner.java:41)<br/>org.junit.runners.ParentRunner$1. evaluate(), (ParentRunner.java:173)<br/>org.junit.internal.runners.statements.RunBefores. evaluate(), (RunBefores.java:28)<br/>org.junit.internal.runners.statements.RunAfters. evaluate(), (RunAfters.java:31)<br/>org.junit.runners.ParentRunner. run(), (ParentRunner.java:220)<br/>org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference. run(), (JUnit4TestReference.java:50)<br/>org.eclipse.jdt.internal.junit.runner.TestExecution. run(), (TestExecution.java:38)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. runTests(), (RemoteTestRunner.java:459)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. runTests(), (RemoteTestRunner.java:675)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. run(), (RemoteTestRunner.java:382)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. main(), (RemoteTestRunner.java:192)<br/>";
		Assert.assertEquals(backtrace, job.getExceptionBacktrace());
	}
	
	
	@After
	public void afterTest() {
		setPrivateField("m_errorException", job, null);
	}
	
	protected void setPrivateField(String field_name, Object object, Object value) {
		Field field;
		try {
			field = Job.class.getDeclaredField(field_name);
			field.setAccessible(true);
			field.set(object, value);
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	}
}
