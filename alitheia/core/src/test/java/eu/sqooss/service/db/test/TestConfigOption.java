package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.test.TestDAObject;

public class TestConfigOption extends TestDAObject {
    @Test
    public void testGetSetValues() {
        // Set up test
        StoredProject sp = new StoredProject("Project");
        ConfigOption co = ConfigOption.PROJECT_CONTACT;
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
