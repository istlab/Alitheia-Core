package com.mws.squal.service.cache;

import java.io.InputStream;
import java.io.ObjectOutputStream;


/**
 * A generic key value pair cache.
 * 
 * The backing store is configured at system
 * initialization time through system wide properties.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public interface CacheService {

    /**
     * Get contents of key as an unencoded raw byte data array. The contents of
     * the array can be discarded after the call. 
     */
    byte[] get(String key);
    
    /**
     * Get the contents of key as an InputStream.
     * 
     * WARNING, ACHTUNG: The client is responsible to close the returned input
     * stream, otherwise the system's resources will be soon exhausted.
     */
    InputStream getObject(String key);
    
    /**
     * Set the contents of a key as an in-memory byte array. After the call, the
     * array can be deleted as the contents are guaranteed to be safely copied.
     */
    void set(String key, byte[] data);
    
    /**
     * Set the contents of key from the 
     */
    void setObject(String key, ObjectOutputStream oos);
}
