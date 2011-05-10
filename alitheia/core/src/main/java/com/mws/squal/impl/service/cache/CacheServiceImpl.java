package com.mws.squal.impl.service.cache;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;

import com.mws.squal.service.cache.CacheService;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.logging.Logger;

public class CacheServiceImpl implements CacheService, AlitheiaCoreService {

    public static final String CACHE_IMPL = "com.mws.squal.cache.impl";
    
    private static List<Class<? extends CacheService>> impls;
    
    static {
        impls = new ArrayList<Class<? extends CacheService>>();
        impls.add(OnDiskCache.class);
        impls.add(InMemoryCache.class);
    }
  
    private CacheService c;
    private BundleContext bc;
    private Logger log;
    
    public CacheServiceImpl() {}
    
    @Override
    public byte[] get(String key) {
        return c.get(key);
    }
    
    @Override
    public InputStream getObject(String key) {
        return c.getObject(key);
    }

    @Override
    public void set(String key, byte[] data) {
        c.set(key, data);
    }

    @Override
    public void setObject(String key, ObjectOutputStream oos) {
        c.setObject(key, oos);
    }

    @Override
    public boolean startUp() {
        String impl = System.getProperty(CACHE_IMPL);
        
        if (impl == null)
            impl = "com.mws.squal.impl.service.cache.OnDiskCache";
        
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(impl);
            c = (CacheService) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Cannot load cache implementation:" + impl);
        } catch (InstantiationException e) {
            log.error("Cannot initialize cache implementation:" + impl + " Error:" + e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("Cannot initialize cache implementation:" + impl + " Error:" + e.getMessage());
        }
        return true;
    }

    @Override
    public void shutDown() {
        c = null;
    }

    @Override
    public void setInitParams(BundleContext bc, Logger l) {
       this.bc = bc;
       this.log = l;
    }   
}
