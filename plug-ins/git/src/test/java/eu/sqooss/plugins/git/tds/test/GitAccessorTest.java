package eu.sqooss.plugins.git.tds.test;

import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import eu.sqooss.plugins.tds.git.GitAccessor;
//import eu.sqooss.service.tds.Revision;

public class GitAccessorTest {
	
	private GitAccessor accessor;
	
	@Before
	public void setUp() {
		accessor = new GitAccessor();
	}
	
	@Test
	public void test_newRevision_null() {
		assertNull(accessor.newRevision((Date) null));
	}	
}