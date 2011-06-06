package com.mws.squal.impl.service.cache;

import java.io.InputStream;

import eu.sqooss.core.AlitheiaCoreService;

public interface CacheService extends AlitheiaCoreService {

    public byte[] get(String key);
    public InputStream getStream(String key);
    public void set(String key, byte[] data);
    public void setStream(String key, InputStream in);
}
