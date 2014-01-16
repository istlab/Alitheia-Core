package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.DBService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class })
public class ConfigOptionTest {

	@Test
	public void testConfigOptionInitialisingMethod() {
		assertEquals(ConfigOption.PROJECT_BTS_SOURCE.getName(),
				"eu.sqooss.project.bts.source");
		assertEquals(ConfigOption.PROJECT_BTS_SOURCE.getDesc(),
				"The project's original BTS URL");
	}
}