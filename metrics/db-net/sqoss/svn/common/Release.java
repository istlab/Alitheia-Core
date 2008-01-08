package paxosk.sqoss.svn.common;

import java.util.*;
import java.sql.*;
/**
 * Class storing a (version,stage) tuple; a release in other words. The 
 * release is internally transformed, processed and represented in the 
 * following form:
 * (step1) the version must be three numbers sperated by '.'; if not it's
 * transformed by adding 0 at the end, for example "3.4" will become 
 * "3.4.0"
 * (step2) all numbers of a version must have 2 digits; if not a zero is
 * appended to the front of each number, except the major (first) one, for
 * example "3.4.0" will eventually become "3.40.00".
 * The reason for this is the right hashing, which also leads to the right
 * ordering of the (version,stage) tuples.
 *
 * HOWEVER, the (version,stage) tuple is also stored in its original form
 * in the {@link #versionOrig_ versionOrig_} and {@link #stageOrig_ stageOrig_}
 * fields.
 * The {@link @getVersionOrig getVersionOrig} and {@link @getStageOrig getStageOrig} 
 * are the corresponding to the 
 * {@link @getVersion getVersion} and {@link @getStage getStageOrig}, but return the
 * fields in their original unprocessed form.
 */
public class Release extends ReleaseBasic implements Comparable 
{    
    private Timestamp timestamp_=null; //the release date timestamp        
    
    /** the collection of Releases, this Release is enlisted to */
    private ReleasePool pool_=null;
    
    /////////////////////////////////////CONSTRUCTORS///////////////////////////////////////
    public Release(String tag,Timestamp timestamp, ReleasePool pool)
    {
        super(tag);        
        timestamp_=timestamp;        
        pool.add(this);
        pool_=pool;
    }//Release
    
    
    public Release(ReleasePool pool, String versionOfficial, 
            String stageOfficial,Timestamp timestamp)
    {
        super("");
        version_=versionOfficial;
        stage_=stageOfficial;
        timestamp_=timestamp;
        pool_=pool;
        pool.add(this);
    }//Release
    
    
    public Release(String tag, ReleasePool pool)
    {        
        //timestamp HHMMSSDDMMYYYY
        this(tag,new Timestamp(0),pool);        
        timestamp_=new Timestamp(0);        
    }//Release
    /////////////////////////////////////CONSTRUCTORS///////////////////////////////////////      
    
    /**
     * Overrides clones and performs deep
     * cloning
     * @return Object the cloned VersionStage object
     */
    public Object clone()
    {
        if (!(this instanceof Release)) {
            return null;
        }
        
        return new Release(tag_,timestamp_,pool_);
    }//clone


    /**
     * Implements the Comparable interface method and returns:
     * @param o the Object this will be compared to
     * @return int  0 if this==o, -1 if this<o, 1 if this>o
     */
    public int compareTo(Object o)
    {
        if (!(o instanceof Release)) {
            System.err.println("Object asked to compare not of VersionStage type");
            return 0;
        }

        Release vs=(Release)o;        
        int vo=version2Int(vs.version_);
        int so=stage2Int(vs.stage_);
        
        int vthis=version2Int(this.version_);        
        int sthis=stage2Int(this.stage_);		
        

        //if stage and version are equal
        if ((vo==vthis) && (so==sthis))
        {
                return 0;
        }
        //if version o is higher than version this
        else if (vo>vthis) 
        {
                return -1;
        }
        //if version o is lower than version this
        else if (vo<vthis) 
        {
                 return 1;
        }
        //if versions are equal AND if stage o is larger than stage this
        else if ((vo==vthis) && (so>sthis))
        {
                return -1;
        }
        //if versions are equal AND if stage o is lower than stage this
        else if ((vo==vthis) && (so<sthis))
        {
                return 1;
        }
        else
        {
                System.err.println("Unexpected relation between version-stage relations");
                return 0;
        }
    }//compareTo


    /**
     * Overrides the {@link java.lang.Object Object} "equals" method
     * @param o the Object this will be compared to
     * @return boolean true if they are equal and false otherwise
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof Release)) {
                return false;
        }

        if (o==this) {
                return true;
        }

        Release vs=(Release)o;
        if  ((vs.version_.equals(this.version_)) &&
                        (vs.stage_.equals(this.stage_)))
        {
                return true;
        }

        return  false;
    }//equals


    /**
     * Overrides the {@link java.lang.Object Object} "hashCode" method
     * @return int the hash code
     */
    public int hashCode()
    {
        String s=new String(version_+stage_);
        return s.hashCode();
    }//hashCode

    
    //////////////////////////////////////////GETTERS/////////////////////////////////////////////
    /**
     * Accessor for the processed representation of the version
     * @return String the processed representation of the version
     */
    public String getVersion()
    {
        return version_;
    }//getVersion


    /**
     * Accessor for the processed representation of the stage
     * @return String the processed representation of the stage
     */
    public String getStage()
    {
        return stage_;
    }//getStage


    /**
     * Accessor for the UNprocessed representation of the version
     * @return String the UNprocessed representation of the version
     */    
    public String getVersionOrig()
    {
        return versionOrig_;
    }//getVersion


    /**
     * Accessor for the UNprocessed representation of the stage
     * @return String the UNprocessed representation of the stage
     */    
    public String getStageOrig()
    {
        return stageOrig_;
    }//getStage
	        
    
    public Timestamp getTimestamp()
    {
        return timestamp_;
    }//getTimestamp
    //////////////////////////////////////////GETTERS/////////////////////////////////////////////
    
    //////////////////////////////////////////SETTERS/////////////////////////////////////////////
    public void setStage(String stage)
    {
        stage_=stage;
    }//setStage
    
    public void setVersion(String version)
    {
        version_=version;
    }//setVersion   
    
    public void setTimestamp(Timestamp tstamp)
    {
        timestamp_=tstamp;
    }//setTimestamp
    //////////////////////////////////////////SETTERS/////////////////////////////////////////////
    
   
    public String toStringOrig()
    {
        String s=versionOrig_+stageOrig_;
        return s;
    }//toStringOrig
    
    
    public String toStringProcessed()
    {
        return toString();
    }//toStringProcessed
    
    
    /**
     * Overrides the {@link java.lang.Object Object} "toString" method
     * @return String the string representation of the (version,stage) tuple
     */    
    public String toString()
    {		
            return version_+" "+stage_; 
    }//toString
           
    ////////////////////////////////////////////HELPERS/////////////////////////////////////////////
    /**
     * Maps a (version,stage) tuple to an int (a hash)
     * @return int; the hash for the (version,stage) tuple
     */
    private int versionStage2Int()
    {
            String[] s=version_.split("\\.");
            int j=0; 

            for (int i=s.length-1; i>=0; i--)
            {			
                    j+=Integer.parseInt(""+s[i])*Math.pow(10,(s.length-i));
            }

            j+=stage2Int(stage_);

            return j;
    }//versionStage2Int


    /**
     * Maps a version to an int
     * @param v the version
     * @return int the hash for the given version
     */
    private  int version2Int(String v)
    {
        char[] car=v.toCharArray();
        int i=0;
        String s="";

        for (char c:car) {
                if (c!='.') {
                   //i+=Integer.valueOf(c);
                   s+=c;
                }
        }

        return Integer.parseInt(s);
        //return i;
    }//convertVersion2Int

    
    /**
     * Maps a stage to an int
     * @param stage the stage
     * @return int the hash for the stage
     */
    private int stage2Int(String stage)
    {
        return hStageInt_.get(stage);
    }//stage2Int
             
    //////////////////////////////////////////HELPERS/////////////////////////////////////////////

	
//    public static void main(String[] args)
//    {
//            HashSet<VersionStage> hvs=new HashSet<VersionStage>();
//            VersionStage a=new VersionStage("3.2.1","Stable");
//            hvs.add(a);
//            VersionStage b=new VersionStage("3.2.1","Alpha");
//            hvs.add(b);
//            VersionStage c=new VersionStage("3.2.2","Stable");
//            hvs.add(c);
//            System.out.println(VersionStage.orderVersionStage(hvs));
//            System.out.println(a.getVersion()+" "+a.getStage());
//    }//main
}