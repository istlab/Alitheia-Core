package eu.sqooss.service.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;

public class CacheServiceImpl implements CacheService {

    public static final String CACHE_IMPL = "eu.sqooss.service.cache.OnDiskCache";
    
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
    public InputStream getStream(String key) {
        byte[] buff = c.get(key);
        
        if (buff == null)
            return null;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buff);
        return bais;
    }

    @Override
    public void set(String key, byte[] data) {
        c.set(key, data);
    }

    @Override
    public void setStream(String key, InputStream in) {
        try {
            int nRead;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];

            while ((nRead = in.read(data, 0, data.length)) != -1) {
              buffer.write(data, 0, nRead);
            }

            buffer.flush();            
            set(key, buffer.toByteArray());
            
        } catch (IOException e) {
            log.error("Error");
        }
    }

    @Override
    public boolean startUp() {
        String impl = System.getProperty(CACHE_IMPL);
        
        if (impl == null)
            impl = "eu.sqooss.service.cache.OnDiskCache";
        
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
