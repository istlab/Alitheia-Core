package eu.sqooss.service.cache;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache extends CacheServiceImpl {

    ConcurrentHashMap<String, byte[]> cache = new ConcurrentHashMap<>(1024);
    
    public InMemoryCache() {}
    
    @Override
    public byte[] get(String key) {
        return cache.get(key);
    }

    @Override
    public void set(String key, byte[] data) {
        cache.put(key, data);
    }
}
