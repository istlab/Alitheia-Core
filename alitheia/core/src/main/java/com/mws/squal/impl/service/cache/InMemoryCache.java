package com.mws.squal.impl.service.cache;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.mws.squal.service.cache.CacheService;

public class InMemoryCache implements CacheService {

    @Override
    public byte[] get(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectInputStream getObject(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void set(String key, byte[] data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setObject(String key, ObjectOutputStream oos) {
        // TODO Auto-generated method stub
        
    }

}
