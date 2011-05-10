package com.mws.squal.test.service.cache;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mws.squal.impl.service.cache.OnDiskCache;

public class OnDiskCacheTest {

    static OnDiskCache cache;
    static long failid;
    static long successid;

    @BeforeClass
    public static void setUp() throws Exception {
        cache = new OnDiskCache();
    }
    
    @Test
    public void testGet() {

    }

    @Test
    public void testGetObject() {
        
    }

    @Test
    public void testSet() {
        String val1 = "this is val1";
        cache.set("foo", val1.getBytes());
    }

    @Test
    public void testSetObject() {
        
    }

}
