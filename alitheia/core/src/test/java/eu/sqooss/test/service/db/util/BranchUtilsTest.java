package eu.sqooss.test.service.db.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.BranchUtils;

@RunWith(MockitoJUnitRunner.class)
public class BranchUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private String name = "TESTNAME";
	private Branch expectedBranch;
	private BranchUtils bu;

	@Before
	public void setUp() {
		this.expectedBranch = new Branch();
		this.expectedBranch.setProject(this.sp);
		this.expectedBranch.setName(this.name);
		
		bu = new BranchUtils(this.dbService);
	}

	@Test
	public void getBranchByNameNoCreateTest() {
		Branch b = bu.getBranchByName(this.sp, this.name, false);
		
		assertNull(b);
		verify(this.dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
	}

	@Test
	public void getBranchByNameCreateTest() {
		Branch expectedBranch = new Branch();
		expectedBranch.setProject(this.sp);
		expectedBranch.setName(this.name);
		// DAO objects through Hibernate have inconsistent equals() behaviour!
		// Using a captor and loose asserts on different fields in stead!
		ArgumentCaptor<Branch> actualBranchCaptor = ArgumentCaptor
				.forClass(Branch.class);
		
		Branch b = bu.getBranchByName(this.sp, this.name, true);
		
		assertNull(b);
		verify(this.dbService, times(2)).doHQL(
				anyString(),
				anyMapOf(String.class, Object.class));
		verify(this.dbService).addRecord(actualBranchCaptor.capture());
		assertEquals(expectedBranch.getProject(), actualBranchCaptor.getValue()
				.getProject());
		assertEquals(expectedBranch.getName(), actualBranchCaptor.getValue()
				.getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBranchByNameExistingTest() {
		when(
				this.dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList(Arrays.asList(this.expectedBranch)));
		// DAO objects through Hibernate have inconsistent equals() behaviour!
		// Using a captor and loose asserts on different fields in stead!

		Branch b = bu.getBranchByName(this.sp, this.name, true);

		assertEquals(this.expectedBranch, b);
		verify(this.dbService).doHQL(
				anyString(),
				anyMapOf(String.class, Object.class));
	}
	
	@Test
	public void suggestBranchNameNoBranchesTest() {
		String actual = bu.suggestBranchName(this.sp);
		
		assertEquals("1", actual);
		verify(this.dbService).doHQL(
				anyString(),
				anyMapOf(String.class, Object.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void suggestBranchNameABranchTest() {
		when(
				this.dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList(Arrays.asList(1L)));
		// DAO objects through Hibernate have inconsistent equals() behaviour!
		// Using a captor and loose asserts on different fields in stead!


		String actual = bu.suggestBranchName(this.sp);
		
		assertEquals(String.valueOf(1L + 1), actual);
		verify(this.dbService).doHQL(
				anyString(),
				anyMapOf(String.class, Object.class));
	}
}
