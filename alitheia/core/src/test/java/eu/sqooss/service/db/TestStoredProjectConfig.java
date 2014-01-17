package eu.sqooss.service.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.test.TestDAObject;

public class TestStoredProjectConfig extends TestDAObject {
    @Test
    public void testEquals() {
        ConfigOption co1 = ConfigOption.PROJECT_WEBSITE;
        StoredProject sp1 = new StoredProject("Test Project");
        Set<String> values1 = new HashSet<String>(Arrays.asList("Value 1", "Value 2"));
        
        StoredProjectConfig spc1 = new StoredProjectConfig(co1, values1, sp1);
        StoredProjectConfig spc2 = new StoredProjectConfig();
        spc2.setConfOpt(co1);
        spc2.setProject(sp1);
        
        assertEquals(spc1, spc1);
        assertEquals(spc2, spc2);
        
        assertEquals(spc1, spc2);
        assertEquals(spc1.hashCode(), spc2.hashCode());
        
        assertFalse(spc1.equals(null));
        assertFalse(spc1.equals(co1));
    }
    
    @Test
    public void testInsert() {
        ConfigOption co1 = ConfigOption.PROJECT_WEBSITE;
        StoredProject sp1 = new StoredProject("Test Project");
        Set<String> values1 = new HashSet<String>(Arrays.asList("Value 1", "Value 2"));
        
        assertTrue(db.addRecord(sp1));
        
        StoredProjectConfig spc1 = new StoredProjectConfig();
        spc1.setProject(sp1);
        spc1.setConfOpt(co1);
        spc1.setValues(values1);
        assertTrue(db.addRecord(spc1));
        
        List<StoredProjectConfig> dbSpcs = db.findObjectsByProperties(StoredProjectConfig.class, new HashMap<String,Object>());
        assertEquals(1, dbSpcs.size());
        assertEquals(spc1, dbSpcs.get(0));
        assertEquals(spc1.getValues(), dbSpcs.get(0).getValues());
    }
    
    @Test
    public void testInsertSameRecord() {
        ConfigOption co1 = ConfigOption.PROJECT_WEBSITE;
        StoredProject sp1 = new StoredProject("Test Project");
        Set<String> values1 = new HashSet<String>(Arrays.asList("Value 1", "Value 2"));
        
        assertTrue(db.addRecord(sp1));
        
        StoredProjectConfig spc1 = new StoredProjectConfig(co1, values1, sp1);
        assertTrue(db.addRecord(spc1));
        
        StoredProjectConfig spc2 = new StoredProjectConfig(co1, new HashSet<String>(), sp1);
        // Will roll back the session itself
        assertFalse(db.addRecord(spc2));
        // So don't let @after know that it doesn't have to roll back
        dontRollback();
    }
}
