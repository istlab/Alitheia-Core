package eu.sqooss.test.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.service.db.BugReporter;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;

public class BugReporterTest {

    private BugReporter br1;
    private BugReporter br2;
    private BugReporter br3;
    private List<DAObject> bugReporters = new LinkedList<DAObject>();
    
    DBService dbs;
    
    @Before public void setUp() {
        br1 = new BugReporter();
        br2 = new BugReporter();
        br3 = new BugReporter();
        
        bugReporters.add(br1);
        bugReporters.add(br2);
        bugReporters.add(br3);
        
        br1.setName("foo");
        br2.setName("bar");
        br3.setName("foobar");
        
        dbs = new DBServiceImpl();
        System.err.println("Finished setup...");
    }

    @After public void tearDown() {
        dbs.deleteRecords(bugReporters);
        System.err.println("Finished teardown...");
    }
    
    @Test public void addRecords() {
        List<BugReporter> results;
        
        System.err.println("Inside the test...");
        dbs.addRecords(bugReporters);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", "foo");
        results = dbs.findObjectByProperties(BugReporter.class, properties);
        Assert.assertTrue(results.size() ==  1);
    }
    
    public static void main(String args[]) {
        System.err.println("Before tests...");
        org.junit.runner.JUnitCore.main("eu.sqooss.test.db.BugReporterTest");
        System.err.println("After tests...");
    }
}
