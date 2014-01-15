package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.ClusterNodeUtils;

@RunWith(MockitoJUnitRunner.class)
public class ClusterNodeUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private ClusterNode expectedNode;
	private String serverName = "SERVERNAME";
	private ClusterNodeUtils clu;

	@Before
	public void setUp() {
		 this.expectedNode = new ClusterNode(this.serverName);

		 this.clu = new ClusterNodeUtils(this.dbService);
	}

	@Test
	public void getClusterNodeByNameNullTest() {
		ClusterNode actual = clu.getClusterNodeByName(serverName);
		
		assertNull(actual);
		verify(dbService).findObjectsByProperties(eq(ClusterNode.class), anyMapOf(String.class, Object.class));
	}

	@Test
	public void getClusterNodeByNameEmptyListTest() {
		when(
				dbService.findObjectsByProperties(eq(ClusterNode.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList<ClusterNode>());
		
		ClusterNode actual = clu.getClusterNodeByName(serverName);
		
		assertNull(actual);
		verify(dbService).findObjectsByProperties(eq(ClusterNode.class), anyMapOf(String.class, Object.class));
	}

	@Test
	public void getClusterNodeByNameExistentTest() {
		when(
				dbService.findObjectsByProperties(eq(ClusterNode.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList<ClusterNode>(Arrays.asList(expectedNode)));
		
		ClusterNode actual = clu.getClusterNodeByName(serverName);
		
		assertEquals(expectedNode, actual);
		verify(dbService).findObjectsByProperties(eq(ClusterNode.class), anyMapOf(String.class, Object.class));
	}
}
