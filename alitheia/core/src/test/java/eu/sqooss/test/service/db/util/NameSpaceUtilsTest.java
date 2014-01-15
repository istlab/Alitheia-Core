package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.util.NameSpaceUtils;

@RunWith(MockitoJUnitRunner.class)
public class NameSpaceUtilsTest {

	private NameSpaceUtils utils;
	@Mock private DBService dbs;
	
	@Before
	public void setUp() {
		this.utils = new NameSpaceUtils(this.dbs);
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetNameSpaceByVersionNameListEmpty() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertNull(this.utils.getNameSpaceByVersionName(new ProjectVersion(), "foobar"));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetNameSpaceByVersionNameListNonEmpty() {
		NameSpace ns = new NameSpace();
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(ns)));
		assertEquals(ns, this.utils.getNameSpaceByVersionName(new ProjectVersion(), "foobar"));
	}
}
