package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import org.junit.*;

import eu.sqooss.service.db.*;
import eu.sqooss.service.db.MetricType.*;

public class MetricTypeTest {
	
	public static MetricType metricSF;
	
	@BeforeClass
	public static void MetricTypeTest(){
		metricSF = new MetricType();
	}
	
	@Test
	public void testTypeFromString(){	
		assertEquals(0,Type.fromString("SOURCE_CODE").compareTo(Type.SOURCE_FILE));
		assertEquals(0,Type.fromString("SOURCE_FILE").compareTo(Type.SOURCE_FILE));

		assertEquals(0,Type.fromString("SOURCE_FOLDER").compareTo(Type.SOURCE_DIRECTORY));
		assertEquals(0,Type.fromString("SOURCE_DIRECTORY").compareTo(Type.SOURCE_DIRECTORY));

		assertEquals(0,Type.fromString("MAILING_LIST").compareTo(Type.MAILING_LIST));

		assertEquals(0,Type.fromString("BUG_DATABASE").compareTo(Type.BUG));
		assertEquals(0,Type.fromString("BUG").compareTo(Type.BUG));
		
		assertEquals(0,Type.fromString("THREAD").compareTo(Type.MAILTHREAD));
		assertEquals(0,Type.fromString("MAILTHREAD").compareTo(Type.MAILTHREAD));

		assertEquals(0,Type.fromString("MAILMESSAGE").compareTo(Type.MAILMESSAGE));

		assertEquals(0,Type.fromString("PROJECT_WIDE").compareTo(Type.PROJECT_VERSION));
		assertEquals(0,Type.fromString("PROJECT_VERSION").compareTo(Type.PROJECT_VERSION));

		assertEquals(0,Type.fromString("DEVELOPER").compareTo(Type.DEVELOPER));
		
		assertEquals(0,Type.fromString("NAMESPACE").compareTo(Type.NAMESPACE));

		assertEquals(0,Type.fromString("EXECUNIT").compareTo(Type.EXECUNIT));
		
		assertEquals(0,Type.fromString("ENCAPSUNIT").compareTo(Type.ENCAPSUNIT));

		assertEquals(null,Type.fromString("SOMETHING_RANDOM_FOR_THE_NULL"));


	}
	
	@Test
	public void testTypeOfMetric(){
		metricSF.setEnumType(Type.SOURCE_DIRECTORY);
		assertTrue(metricSF.toActivator().equals(ProjectDirectory.class));
		
		metricSF.setEnumType(Type.SOURCE_FILE);
		assertTrue(metricSF.toActivator().equals(ProjectFile.class));
		
		metricSF.setEnumType(Type.PROJECT_VERSION);
		assertTrue(metricSF.toActivator().equals(ProjectVersion.class));
		
		metricSF.setEnumType(Type.MAILMESSAGE);
		assertTrue(metricSF.toActivator().equals(MailMessage.class));
		
		metricSF.setEnumType(Type.MAILING_LIST);
		assertTrue(metricSF.toActivator().equals(MailingList.class));
		
		metricSF.setEnumType(Type.MAILTHREAD);
		assertTrue(metricSF.toActivator().equals(MailingListThread.class));
		
		metricSF.setEnumType(Type.BUG);
		assertTrue(metricSF.toActivator().equals(Bug.class));
		
		metricSF.setEnumType(Type.DEVELOPER);
		assertTrue(metricSF.toActivator().equals(Developer.class));
		
		metricSF.setEnumType(Type.NAMESPACE);
		assertTrue(metricSF.toActivator().equals(NameSpace.class));
		
		metricSF.setEnumType(Type.ENCAPSUNIT);
		assertTrue(metricSF.toActivator().equals(EncapsulationUnit.class));
		
		metricSF.setEnumType(Type.EXECUNIT);
		assertTrue(metricSF.toActivator().equals(ExecutionUnit.class));
		
		metricSF.setEnumType(Type.PROJECT);
		metricSF.toActivator();
	}
	
	/**
	 * Was used to test the old code, which would return null with toActivator for type Project.
	 */
	@Ignore
	@Test(expected=NullPointerException.class)
	public void testTypeOfMetricForProject(){
		metricSF.setEnumType(Type.PROJECT);
		metricSF.toActivator();
	}
}
