package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class MailMessageTest {

	static MailMessage testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new MailMessage();
	}

	@Test
	public void testIdGetterSetter() {
		// set the value via the setter
		testObject.setId(1900L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject,
				"id");

		// Compare the expected with the actual result
		assertEquals(actualValue, 1900L);

		long getValue = testObject.getId();
		// Compare the expected with the actual result
		assertEquals(getValue, 1900L);
	}
	
	@Test
	public void testFileNameGetterSetter()
	{
		//set the value via the setter
		testObject.setFileName("testFileName");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "fileName");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testFileName");
		
		String getValue=  testObject.getFileName();
		//Compare the expected with the actual result
		assertEquals(getValue,"testFileName");	
	}
	
	@Test
	public void testSenderGetterSetter()
	{
		Developer setValue = new Developer();
		//set the value via the setter
		testObject.setSender(setValue);
		
		//Pull out property in order to compare
		Developer actualValue = Whitebox.<Developer>getInternalState(testObject, "sender");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Developer getValue=  testObject.getSender();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testListGetterSetter()
	{
		MailingList setValue = new MailingList();
		//set the value via the setter
		testObject.setList(setValue);
		
		//Pull out property in order to compare
		MailingList actualValue = Whitebox.<MailingList>getInternalState(testObject, "list");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		MailingList getValue=  testObject.getList();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testSendDateGetterSetter()
	{
		Date setValue = new Date();
		//set the value via the setter
		testObject.setSendDate(setValue);
		
		//Pull out property in order to compare
		Date actualValue = Whitebox.<Date>getInternalState(testObject, "sendDate");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Date getValue=  testObject.getSendDate();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testParentGetterSetter()
	{
		MailMessage setValue = new MailMessage();
		//set the value via the setter
		testObject.setParent(setValue);
		
		//Pull out property in order to compare
		MailMessage actualValue = Whitebox.<MailMessage>getInternalState(testObject, "parent");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		MailMessage getValue=  testObject.getParent();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testDepthGetterSetter()
	{
		//set the value via the setter
		testObject.setDepth(3);
		
		//Pull out property in order to compare
		int actualValue = Whitebox.<Integer>getInternalState(testObject, "depth");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,3);
		
		int getValue=  testObject.getDepth();
		//Compare the expected with the actual result
		assertEquals(getValue,3 );	
	}
	
	@Test
	public void testFileNameIdGetterSetter()
	{
		//set the value via the setter
		testObject.setFilename("testFileName");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "fileName");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testFileName");
		
		String getValue=  testObject.getFilename();
		//Compare the expected with the actual result
		assertEquals(getValue,"testFileName");	
	}
	
	@Test
	public void testMessageIdGetterSetter()
	{
		//set the value via the setter
		testObject.setMessageId("testMessageId");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "messageId");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testMessageId");
		
		String getValue=  testObject.getMessageId();
		//Compare the expected with the actual result
		assertEquals(getValue,"testMessageId");	
	}
	
	@Test
	public void testSubjectGetterSetter()
	{
		//set the value via the setter
		testObject.setSubject("testSubject");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "subject");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testSubject");
		
		String getValue=  testObject.getSubject();
		//Compare the expected with the actual result
		assertEquals(getValue,"testSubject");	
	}


}
