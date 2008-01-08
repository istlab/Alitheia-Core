package paxosk.schedule.lru;

import java.util.*;
import java.util.concurrent.*;

public class LastRUPool 
{
    //the pool, that stores the set of objects
    private HashSet pool_=null; 
    //the queue, based upon is dudged which is the LRU object,
    //which will be discarded,etc
    private ConcurrentLinkedQueue queue_=new ConcurrentLinkedQueue();
    //the capacity of the pool
    private int capacity_=0;
    //the current size of the pool
    private int size_=0;
    
    /**
     * Constructor:
     * create pool with the given capacity
     * @param int the capacity of the pool
     */
    public LastRUPool(int capacity) 
    {
        capacity_=capacity;
        pool_=new HashSet(capacity);
    }//LastRUPool
    
    
    
    /**
     * Constructor:
     * create a pool with the capacity of the given collection
     * and add all elements of the collection to it. The elements 
     * are added in the order returned by the iterator.
     * @param Collection the collection of elements
     */
    public LastRUPool(Collection collection)
    {
        capacity_=collection.size();
        pool_=new HashSet(collection.size());
        for (Object o: collection)
        {
            pool_.add(o);
            queue_.offer(o);
        }                        
    }//LastRUPool
    
    
    
    /**
     * Constructor:
     * create a pool with the capacity specified and add
     * all elements of the collection to it. The elements 
     * are added in the order returned by the iterator.
     * @param Collection the collection of elements
     * @param int the capacity of the pool
     * @throws IllegalArgumentException if the specified capacity is smaller
     *          than the size of the collection
     */
    public LastRUPool(Collection collection, int capacity)
    {
        if (collection.size()>capacity) {
            throw new IllegalArgumentException("Specified capacity must be greater or equal to the size of the collection!");
        }
        
        capacity_=capacity;
        pool_=new HashSet(capacity);
        for (Object o: collection)
        {
            pool_.add(o);
            queue_.offer(o);
        }                        
    }//LastRUPool    
    
    
    /**
     * Check if the pool is full
     * @return true if full and false otherwise 
     */
    public boolean isFull()
    {
        return capacity_==size_;
    }//isFull
    
    
    /**
     * Check if the pool is empty
     * @return true if empty and false otherwise
     */
    public boolean isEmpty()
    {
        return size_==0;
    }//isEmpty
    
    
    
    /**
     * Check if the given element is contained in the pool.
     * @param Object the element we don't know if it is contained
     * @return true if contained and false otherwise
     */
    public boolean contains(Object o)
    {
        return pool_.contains(o);
    }//contains
    

    /**
     * Check if the given element is NOT contained in the pool.
     * @param Object the element we don't know if it is contained
     * @return false if contained and true otherwise
     */    
    public boolean notContains(Object o)
    {
        return !pool_.contains(o);
    }//notContains
    
    
    /**
     * Will add an elment to the pool and will make room -if necessary
     * by discarding the older element.
     * @param Object
     * @return true if succedded and false otherwise
     */
    public boolean add(Object o)
    {
        if (size_==capacity_) 
        {
            Object oold=queue_.poll();
            pool_.remove(oold);
            pool_.add(o);
            queue_.add(o);
            return true;
        }
        else if ((size_!=capacity_) || (size_==0))
        {
            size_++;
            pool_.add(o);
            queue_.add(o);
            return true;
        }
        
        return false;
    }//add

    
    public HashSet getPool()
    {
        return pool_;
    }//getPool
    
        
    public String toString()
    {
        return queue_.toString();
    }//toString

    
//    //sample usage
//    public static void main(String[] args)
//    {
//        LastRUPool pool=new LastRUPool(10);
//        
//        for (int i=0; i< 100; i++)
//        {
//            pool.add(i);
//            System.out.println(pool);        
//        }
//    }
}
