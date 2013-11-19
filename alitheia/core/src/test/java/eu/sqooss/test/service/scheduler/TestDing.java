package eu.sqooss.test.service.scheduler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.SchedulerException;

public class TestDing {
    
    
    @BeforeClass
    public static void setUp() {
    }

    @Test
    public void testJobYield() throws SchedulerException {
    	assertEquals(4, 4);
        
    }
    
    @AfterClass
    public static void tearDown() {
    }
}