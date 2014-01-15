package eu.sqooss.test.service.db.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.MailingListUtils;

@RunWith(MockitoJUnitRunner.class)
public class MailingListUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private Date d;
	private MailingList expectedMailingList;
	private MailMessage expectedMailMessage;
	private MailingListThread expectedThread;
	private MailingListUtils mlu;

	@Before
	public void setUp() {
		 this.expectedMailMessage = new MailMessage();
		 this.expectedMailingList = new MailingList();
		 this.d = new Date();
		 this.expectedThread = new MailingListThread(this.expectedMailingList, this.d);
		 this.mlu = new MailingListUtils(this.dbService);
	}
	
	@Test
	public void getMessagesNewerThanNullTest() {
		List<MailMessage> actual = mlu.getMessagesNewerThan(expectedMailingList, this.d);
		
		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesNewerThanEmptyTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		
		List<MailMessage> actual = mlu.getMessagesNewerThan(expectedMailingList, this.d);
		
		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesNewerThanNonEmptyTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList(Arrays.asList(expectedMailMessage)));
				
		List<MailMessage> actual = mlu.getMessagesNewerThan(expectedMailingList, this.d);
		
		assertEquals(expectedMailMessage, actual.get(0));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLatestEmailNullTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList());
				
		MailMessage actual = mlu.getLatestEmail(expectedMailingList);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLatestEmailTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList(Arrays.asList(expectedMailMessage)));
				
		MailMessage actual = mlu.getLatestEmail(expectedMailingList);
		
		assertEquals(expectedMailMessage, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLatestThreadNullTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList());
				
		MailingListThread actual = mlu.getLatestThread(expectedMailingList);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLatestThreadTest() {
		when(   dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList(Arrays.asList(expectedThread)));
				
		MailingListThread actual = mlu.getLatestThread(expectedMailingList);
		
		assertEquals(expectedThread, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getStartingEmailNullTest() {
		when(
		dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		new ArrayList());
				
		MailMessage actual = mlu.getStartingEmail(expectedThread);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getStartingEmailTest() {
		when(
		dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		new ArrayList(Arrays.asList(expectedMailMessage)));
				
		MailMessage actual = mlu.getStartingEmail(expectedThread);
		
		assertEquals(expectedMailMessage, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesByArrivalOrderTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(new ArrayList(Arrays.asList(expectedMailMessage)));
				
		List<MailMessage> actual = mlu.getMessagesByArrivalOrder(expectedThread);
		
		assertEquals(expectedMailMessage, actual.get(0));
	}
	
	@Test
	public void getMessagesByArrivalOrderNullTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(null);
				
		List<MailMessage> actual = mlu.getMessagesByArrivalOrder(expectedThread);
		
		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesByArrivalOrderEmptyTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(new ArrayList());
				
		List<MailMessage> actual = mlu.getMessagesByArrivalOrder(expectedThread);
		
		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getThreadDepthZeroTest1() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
		.thenReturn(new ArrayList());
				
		int actual = mlu.getThreadDepth(expectedThread);
		
		assertEquals(0, actual);
	}
	
	@Test
	public void getThreadDepthZeroTest2() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
		.thenReturn(null);
				
		int actual = mlu.getThreadDepth(expectedThread);
		
		assertEquals(0, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getThreadDepthTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
		.thenReturn(new ArrayList(Arrays.asList(1)));
				
		int actual = mlu.getThreadDepth(expectedThread);
		
		assertEquals(1, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesAtLevelEmptyTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(new ArrayList());
				
		List<MailMessage> actual = mlu.getMessagesAtLevel(expectedThread, 0);
		
		assertTrue(actual.isEmpty());
	}
	
	@Test
	public void getMessagesAtLevelNullTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(null);
				
		List<MailMessage> actual = mlu.getMessagesAtLevel(expectedThread, 0);
		
		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessagesAtLevelTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
		.thenReturn(new ArrayList(Arrays.asList(expectedMailMessage)));
				
		List<MailMessage> actual = mlu.getMessagesAtLevel(expectedThread, 0);
		
		assertEquals(expectedMailMessage, actual.get(0));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessageByIdEmptyTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
				
		MailMessage actual = mlu.getMessageById("");
		
		assertNull(actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getMessageByIdNullTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		null);
				
		MailMessage actual = mlu.getMessageById("");
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessageByIdTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		new ArrayList(Arrays.asList(expectedMailMessage)));
				
		MailMessage actual = mlu.getMessageById("");
		
		assertEquals(expectedMailMessage, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessageByFileNameTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		new ArrayList(Arrays.asList(expectedMailMessage)));
				
		MailMessage actual = mlu.getMessageByFileName("");
		
		assertEquals(expectedMailMessage, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getMessageByFileNameEmptyTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
				
		MailMessage actual = mlu.getMessageByFileName("");
		
		assertNull(actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getMessageByFileNameNullTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class))).thenReturn(
		null);
				
		MailMessage actual = mlu.getMessageByFileName("");
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLastMailMessageNullTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
		.thenReturn(new ArrayList());
				
		MailMessage actual = mlu.getLatestMailMessage(this.sp);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getLastMailMessageTest() {
		when(   dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
		.thenReturn(new ArrayList(Arrays.asList(expectedMailMessage)));
				
		MailMessage actual = mlu.getLatestMailMessage(this.sp);
		
		assertEquals(expectedMailMessage, actual);
	}
	
//		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
}
