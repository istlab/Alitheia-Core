package eu.sqooss.impl.service.webadmin;

import java.util.ListResourceBundle;

public class TestBundle extends ListResourceBundle {
    protected Object[][] getContents() {
        return new Object[][] {
            {"s1", "test1"},  // test1
            {"s2", "test2"},  // test2
            {"s3", "test3"}   // test3
        };
    }
}
