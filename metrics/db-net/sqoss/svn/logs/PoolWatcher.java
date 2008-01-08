package paxosk.sqoss.svn.logs;

import java.util.*;
import java.io.*;
import paxosk.schedule.lru.*;

public class PoolWatcher implements Runnable
{
    private HashSet pool_=null;
    private FileWriter fout_=null;
    
    public PoolWatcher(HashSet hset) 
    {
        pool_=hset;
        
        try {        
            fout_=new FileWriter(FILENAME);
        }
        catch(IOException e) {
            System.err.println("Failed to open input stream to Commit log file");
            e.printStackTrace();
        }
    }//PoolWatcher
    
    public PoolWatcher(LastRUPool pool)
    {
        pool_=pool.getPool();
    }//PoolWatcher
    
    
    
    public void run()
    {
        while (true)
        {
            try
            {                
//                String sPool=toString();
//                fout_.write(sPool,0,sPool.length());
                fout_.write(toString());  
                //System.err.println(toString());
                //fout_.flush();
                Thread.currentThread().sleep(DELAY);
            }
            catch(IOException e)
            {
                System.out.println("Failed to write data to Commit log!");
                e.printStackTrace();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }//while
    }//run
    
    
    public String toString()
    {
        return "Pool Size: "+pool_.size()+"\n";
        
//        return "Pool Size: "+pool_.size()+"\n"
//                +"==============================================\n"+
//                pool_.toString()+"\n"
//                +"==============================================\n";
    }//toString
    
    
    private static final int DELAY=10000;
    private static final String FILENAME=
            "C:/Documents and Settings/paxosk/Desktop/projects/SQOSS/resources/logs/CommitPool.log";
}
