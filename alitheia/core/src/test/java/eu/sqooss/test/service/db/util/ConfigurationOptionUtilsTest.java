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

import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.ConfigurationOptionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationOptionUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private ConfigurationOption expectedCO;
	private final String key = "KEY";
	private final String desc = "DESC";
	private ConfigurationOptionUtils cou;

	@Before
	public void setUp() {
		 this.expectedCO = new ConfigurationOption(this.key, this.desc);

		 this.cou = new ConfigurationOptionUtils(this.dbService);
	}

	@Test
	public void getClusterNodeByNameEmptyListTest() {
		when(
				dbService.findObjectsByProperties(eq(ConfigurationOption.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList<ConfigurationOption>());
		
		ConfigurationOption actual = cou.getConfigurationOptionByKey(key);
		
		assertNull(actual);
		verify(dbService).findObjectsByProperties(eq(ConfigurationOption.class), anyMapOf(String.class, Object.class));
	}

	@Test
	public void getClusterNodeByNameExistentTest() {
		when(
				dbService.findObjectsByProperties(eq(ConfigurationOption.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList<ConfigurationOption>(Arrays.asList(expectedCO)));
		
		ConfigurationOption actual = cou.getConfigurationOptionByKey(this.key);
		
		assertEquals(expectedCO, actual);
		verify(dbService).findObjectsByProperties(eq(ConfigurationOption.class), anyMapOf(String.class, Object.class));
	}
}
