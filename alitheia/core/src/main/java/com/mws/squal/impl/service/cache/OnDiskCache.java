package com.mws.squal.impl.service.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.mws.squal.service.cache.CacheService;

import eu.sqooss.service.logging.Logger;

public class OnDiskCache implements CacheService {

    public static final String CACHE_DIR = "com.mws.squal.cache.dir";
    
    private File dir;
    
    private Logger log;
    
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
        FileChannel file = null;
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
                file.close();
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
        FileChannel file = null;
        MappedByteBuffer buff;
        FileLock lock = null;
        RandomAccessFile raf;

        try {
            String fname = dir.getAbsolutePath() + File.pathSeparator + md5(key);
            raf = new RandomAccessFile(fname, "rw");
            file = raf.getChannel();
            while (lock != null) {
                try {
                    lock = file.lock(0, data.length, false);
                } catch (ClosedChannelException cce) {
                    log.warn("Cannot store key " + key + 
                            "Cannot write to file " + fname + 
                            " Channel was closed");
                    return;
                } catch (FileLockInterruptionException ace) {
                    //ignored
                } catch (OverlappingFileLockException ofle ) {
                    //ignored
                } catch (NonWritableChannelException ofle ) {
                    log.warn("Cannot store key " + key + " File " + fname + 
                            " was not opened for writing");
                    return;
                } catch (IOException ioe) {
                    log.warn("Cannot store key " + key + 
                            " An exception occured: "+ ioe.getMessage());
                    file.close();
                    return;
                } 
            }
            buff = file.map(MapMode.READ_WRITE, 0, data.length);
            buff.put(data);
        } catch (FileNotFoundException e) {
            log.warn("Cannot store key " + key + 
                    " An exception occured: "+ e.getMessage());
        } catch (Exception e) {
            log.warn("Cannot store key " + key + 
                    " An exception occured: "+ e.getMessage());
        }  finally {
            try {
                lock.release();
                file.close();
            } catch (IOException e) {
                //Ignored
            }
        }
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
