package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.test.TestInitHelper;

public class TestConfigurationOption {
    static DBService db;
    static Logger l;

    @BeforeClass
    public static void setUpClass() throws MalformedURLException {
        l = TestInitHelper.initLogger();
        db = TestInitHelper.initDatabase(l);
    }

    @Before
    public void setUpTest() {
        db.startDBSession();
    }

    @After
    public void tearDownTest() {
        assertTrue("Make sure we do not store any changes", db.rollbackDBSession());
    }

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
}
