package com.mws.squal.service.cache;

import java.io.InputStream;
import java.io.ObjectOutputStream;

public interface CacheService {

    byte[] get(String key);
    InputStream getObject(String key);
    
    void set(String key, byte[] data);
    void setObject(String key, ObjectOutputStream oos);
}
