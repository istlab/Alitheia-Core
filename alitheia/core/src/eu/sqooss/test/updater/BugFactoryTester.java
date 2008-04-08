package eu.sqooss.test.updater;

import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.util.List;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import eu.sqooss.impl.service.updater.BugFactory;
import eu.sqooss.service.db.Bug;

public class BugFactoryTester {
    
    @Before public void setUp() {
    }

    @After public void tearDown() {
    }
    
    @Test public void parseBugs() throws DocumentException, 
        MalformedURLException, FileNotFoundException {
        List<Bug> results = null;
        
        System.err.println("Inside the test...");
        BugFactory factory = new BugFactory("C:/Users/louridas/Documents/Work/aueb/svn.sqo-oss.eu/devel/trunk/tools/mirror/bugs/kdebugs.out");
        try {
            results = factory.processBugs();
        } catch (DocumentException dex) {
            System.err.println(dex.getMessage());
            Throwable ex = dex.getCause();
            while (ex != null) {
                System.err.println(ex.getMessage());
                ex = ex.getCause();
            }
            dex.printStackTrace(System.err);
            return;
        }
        for (Bug bug : results) {
            System.out.println(bug);
        }
    }
    
    public static void main(String args[]) {
        System.err.println("Before tests...");
        org.junit.runner.JUnitCore.main("eu.sqooss.test.updater.BugFactoryTester");
        System.err.println("After tests...");
    }
}
