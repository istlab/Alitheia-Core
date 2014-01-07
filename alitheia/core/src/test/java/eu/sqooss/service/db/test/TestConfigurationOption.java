package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.test.TestDAObject;

public class TestConfigurationOption extends TestDAObject {
    @Test
    public void testInsert() {
        ConfigurationOption co1 = new ConfigurationOption();
        co1.setKey("Key1");
        co1.setDescription("Desc1");
        assertTrue(db.addRecord(co1));
        
        ConfigurationOption co2 = new ConfigurationOption("Key2", "Desc2");
        assertTrue(db.addRecord(co2));
        
        List<ConfigurationOption> coList = db.findObjectsByProperties(ConfigurationOption.class, new HashMap<String, Object>());
        assertEquals(2, coList.size());
        assertEquals(new ConfigurationOption(co1.getKey(), co1.getDescription()), coList.get(0));
        assertEquals(new ConfigurationOption(co2.getKey(), co2.getDescription()), coList.get(1));
        
        ConfigurationOption coFound = ConfigurationOption.fromKey(db, co2.getKey());
        assertEquals(co2, coFound);
        
        assertEquals(null, ConfigurationOption.fromKey(db, "UnknownKey"));
    }

    @Test
    public void testGetSetValues() {
        // Set up test
        ConfigurationOption co = new ConfigurationOption("key", "description");
        StoredProject sp = new StoredProject("Project");
        assertTrue(db.addRecord(co));
        assertTrue(db.addRecord(sp));
        
        // Test adding first few values
        List<String> values = Arrays.asList("val1", "val2");
        List<String> expected = values;
        co.setValues(db, sp, values, false);
        
        List<String> result = co.getValues(db, sp);
        Collections.sort(result);
        assertEquals(expected, co.getValues(db, sp));
        
        // Test overwriting and adding one old value
        values = Arrays.asList("val2", "val3");
        expected = values;
        co.setValues(db, sp, values, true);
        
        result = co.getValues(db, sp);
        Collections.sort(result);
        assertEquals(expected, result);
        
        // Test adding already existing value without overwriting
        values = Arrays.asList("val2", "val1");
        expected = Arrays.asList("val1", "val2", "val3");
        co.setValues(db, sp, values, false);
        
        result = co.getValues(db, sp);
        Collections.sort(result);
        assertEquals(expected, result);
        
        // Test adding already existing value while overwriting
        expected = Arrays.asList("val1", "val2");
        co.setValues(db, sp, values, true);
        
        result = co.getValues(db, sp);
        Collections.sort(result);
        assertEquals(expected, result);
        
        // Test adding an empty list without overwriting
        values = Arrays.asList();
        co.setValues(db, sp, values, false);
        
        result = co.getValues(db, sp);
        Collections.sort(result);
        assertEquals(expected, result);
    }
}
