package com.mws.squal.impl.service.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.mws.squal.service.cache.CacheService;

public class OnDiskCache implements CacheService {

    public static final String CACHE_DIR = "com.mws.squal.cache.dir";
    
    private File dir;
    
    public OnDiskCache() throws Exception {
        
        String dirpath = System.getProperty(CACHE_DIR);
        
        if (dirpath == null) {
            dirpath = System.getProperty("java.io.tmpdir");
            if (dirpath == null)
                dirpath = "tmp";
        }
        
        dir = new File(dirpath);
        
        try {
            if (!dir.exists())
                dir.createNewFile();
        } catch (IOException e) {
            throw new Exception("Cannot create cache dir: " + dir.getPath());
        }
    }
    
    @Override
    public byte[] get(String key) {
        FileChannel file;
        MappedByteBuffer buff;
        FileLock lock = null;
        byte[] result = null;
        try {
            file = new FileInputStream(md5(key)).getChannel();
            lock = file.lock();
            buff = file.map(MapMode.READ_ONLY, 0, file.size());
            result = new byte[(int)file.size()]; // 4GB should be enough for everybody :-)
            buff.get(result);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                lock.release();
            } catch (IOException e) {
                return null;
            }
        }

        return result;
    }
    
    @Override
    public InputStream getObject(String key) {
        
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

    private String md5(String...args) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        
        for (String arg : args)
            m.update(arg.getBytes(), 0, arg.length());
        return new BigInteger(1, m.digest()).toString(16);
    }
}
