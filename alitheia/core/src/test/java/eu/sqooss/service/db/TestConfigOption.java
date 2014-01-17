package eu.sqooss.service.db;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.test.TestDAObject;

public class TestConfigOption extends TestDAObject {
    @Test
    public void testFromKey() {
        assertEquals(ConfigOption.PROJECT_NAME, ConfigOption.fromKey(ConfigOption.PROJECT_NAME.getName()));
        assertEquals(null, ConfigOption.fromKey("BadKey"));
    }
}
