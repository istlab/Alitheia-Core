package paxosk.sqoss.svn.common;

import java.util.*;

public class ReleasePool
{
    TreeSet<Release> rSet_=new TreeSet<Release>();
            
    public ReleasePool() 
    {
    }//ReleasePool    
    
    public boolean add(Release r)
    {
        return rSet_.add(r);
    }//add
    
    public boolean remove(Release r)
    {
        return rSet_.remove(r);
    }//remove
    
    public Iterator<Release> iterator()
    {
        return rSet_.iterator();
    }//iterator    
    
    public Collection<Release> getCollection()
    {
        return rSet_;
    }//getCollection
    
    
    public List<Release> getList()
    {
        Vector<Release> rList=new Vector<Release>();
        rList.addAll(rSet_);
        
        return  rList;
    }//getList
}
