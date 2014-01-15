package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.DeveloperUtils;

@RunWith(MockitoJUnitRunner.class)
public class DeveloperUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private DeveloperUtils du;
	private final String email   = "DEVELOPERNAME@DOMAIN.EXT";
	private final String name    = "DEVELOPERNAME";
	private final String hash    = DigestUtils.shaHex(email);
	private final String ohlohID = "1";
	private Developer expectedDeveloper;
	private OhlohDeveloper expectedOhlohDeveloper;

	@Before
	public void setUp() {
		 this.du = new DeveloperUtils(this.dbService);
		 
		 this.expectedDeveloper = new Developer();
		 this.expectedDeveloper.setUsername(name);
		 this.expectedDeveloper.setName(name);
		 this.expectedDeveloper.setStoredProject(sp);
		 this.expectedDeveloper.addAlias(email);
		 
		 this.expectedOhlohDeveloper = new OhlohDeveloper(name, hash, ohlohID);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailExistentTest() {
		when(dbService.doHQL(anyString(), anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList(Arrays.asList(expectedDeveloper)));

		Developer actual = du.getDeveloperByEmail(email, sp, false);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertEquals(expectedDeveloper.getAliases().iterator().next()
				.getEmail(), actual.getAliases().iterator().next().getEmail());
		assertEquals(expectedDeveloper.getStoredProject(),
				actual.getStoredProject());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailNonExistentTest() {
		when(dbService.doHQL(anyString(), anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());

		Developer actual = du.getDeveloperByEmail(email, sp, false);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailNonExistentCreateTest() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(true);
		
		Developer actual = du.getDeveloperByEmail(email, sp, true);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertEquals(expectedDeveloper.getAliases().iterator().next()
				.getEmail(), actual.getAliases().iterator().next().getEmail());
		assertEquals(expectedDeveloper.getStoredProject(), actual.getStoredProject());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailAddRecordFailsTest2() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);
		
		Developer actual = du.getDeveloperByEmail(email, sp);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailAddRecordFailsTest() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);
		
		Developer actual = du.getDeveloperByEmail(email, sp, true);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailHashFailTest() {
		when(dbService.doHQL(anyString(), anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedOhlohDeveloper)));
		when(
				dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);

		Developer actual = du.getDeveloperByEmail(email, sp, false);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertNull(actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByEmailHashTest() {
		when(dbService.doHQL(anyString(), anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList());
		when(
				dbService.findObjectsByProperties(eq(OhlohDeveloper.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedOhlohDeveloper)));
		when(
				dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedDeveloper)));
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);

		Developer actual = du.getDeveloperByEmail(email, sp, false);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
		assertEquals(expectedDeveloper.getAliases().iterator().next()
				.getEmail(), actual.getAliases().iterator().next().getEmail());
		assertEquals(expectedDeveloper.getStoredProject(), actual.getStoredProject());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByUsernameNonExistentTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());

		Developer actual = du.getDeveloperByUsername(name, sp, false);

		assertNull(actual);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByUsernameExistentTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedDeveloper)));

		Developer actual = du.getDeveloperByUsername(name, sp, false);

		assertEquals(expectedDeveloper.getAliases().iterator().next()
				.getEmail(), actual.getAliases().iterator().next().getEmail());
		assertEquals(expectedDeveloper.getStoredProject(), actual.getStoredProject());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByUsernameNonExistentCreateTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList()));
		when(dbService.addRecord(any(DAObject.class))).thenReturn(true);

		Developer actual = du.getDeveloperByUsername(name, sp, true);

		assertEquals(expectedDeveloper.getUsername(), actual.getUsername());
		assertEquals(expectedDeveloper.getStoredProject(), actual.getStoredProject());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByUsernameNonExistentCreateTest2() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList()));
		when(dbService.addRecord(any(DAObject.class))).thenReturn(true);

		Developer actual = du.getDeveloperByUsername(name, sp);

		assertEquals(expectedDeveloper.getUsername(), actual.getUsername());
		assertEquals(expectedDeveloper.getStoredProject(), actual.getStoredProject());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDeveloperByUsernameNonExistentAddFailsTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList()));
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);

		Developer actual = du.getDeveloperByUsername(name, sp, true);

		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeveloperByNameNonExistentTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		Developer actual = du.getDeveloperByName(name, sp, false);

		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeveloperByNameExistentTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedDeveloper)));
		
		Developer actual = du.getDeveloperByName(name, sp, false);

		assertEquals(expectedDeveloper.getName(), actual.getName());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeveloperByNameNonExistentCreateTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(true);
		
		Developer actual = du.getDeveloperByName(name, sp, true);

		assertEquals(expectedDeveloper.getName(), actual.getName());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeveloperByNameNonExistentCreateFailsTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(DAObject.class))).thenReturn(false);
		
		Developer actual = du.getDeveloperByName(name, sp, true);

		assertNull(actual);
	}
	
	@Test
	public void getByUserNameTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				null);
		
		List<OhlohDeveloper> actual = du.getByUsername(name);

		assertTrue(actual.isEmpty());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getByOhlohIdTest() {
		when(   dbService.findObjectsByProperties(eq(Developer.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		OhlohDeveloper actual = du.getByOhlohId(ohlohID);

		assertNull(actual);
	}
}
