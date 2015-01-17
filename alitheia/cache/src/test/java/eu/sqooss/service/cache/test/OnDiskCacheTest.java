package eu.sqooss.service.cache.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.service.cache.OnDiskCache;
import org.junit.BeforeClass;
import org.junit.Test;

public class OnDiskCacheTest {

    static OnDiskCache cache;
    static long failid;
    static long successid;
    static String path = "tmp";

    @BeforeClass
    public static void setUp() throws Exception {
        cache = new OnDiskCache(path);
    }
    
    @Test
    public void testSet() {
        String val1 = "this is val1";
        cache.set("foo", val1.getBytes());
    }
    
    @Test
    public void testGet() {
        testSet();
        byte[] b = cache.get("foo");
        String s = new String(b);
        assertEquals(s, "this is val1");
    }
    
    @Test
    public void testStress() throws InterruptedException {
        Thread old = null;
        List<Thread> threads = new ArrayList<Thread>();
        
        for (int i = 0; i < 8; i++) {
            StresserThread t = new StresserThread(cache);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    class StresserThread extends Thread {
        OnDiskCache cache;
        
        public StresserThread(OnDiskCache cache) {
            this.cache = cache;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                String key = "foo" + i;
                String content = "bar" + i;
                cache.set(key, content.getBytes());
                int random = (int)(Math.random() * (double)i);
                byte [] b = cache.get("foo" + random);
                String read = new String(b);
                String expected = "bar" + random;
                assertEquals(read, expected);
                if (!read.equals(expected)){
                    System.err.println("Error");
                }
            }
        }
    }
}
