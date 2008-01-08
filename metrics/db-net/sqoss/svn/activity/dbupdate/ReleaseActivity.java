package paxosk.sqoss.svn.activity.dbupdate;

import java.util.*;
import java.sql.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.sql.common.*;
import paxosk.log.loggers.*;
import paxosk.sqoss.svn.common.*;
import paxosk.classes.common.*;


/**
 *
 */
public class ReleaseActivity 
{
    //the id of the current project
    private int PROJECT_ID=0;
    //query requesting (version,stage,release_date) tuples
    private PreparedStatement psIN_RELEASES=null;
    //query requesting the set of commits for a given release
    private PreparedStatement psIN_COMMITS_RELEASE=null;
    //update command; for a (projid,version,stage) tuple will
    //update the (workdays,commits_num) tuple
    private PreparedStatement psOUT_RELEASE_WORKDAYS_COMMITS=null;
    
    //SQL connection
    private Connection con_=null;
    //log4j logger
    private Logger logger_=null;
    //the properties
    private PropertiesConfiguration props_=null;    
    
    /**
     * Constructor
     */
    public ReleaseActivity(Connection con, Logger logger, PropertiesConfiguration props)  
    {       
        logger_=logger;
        props_=props;
        con_=con;
        PROJECT_ID=props_.getInt("PROJECT_ID");
        
        try
        {
            psIN_RELEASES=con_.prepareStatement(props_.getString("IN_RELEASES"));
            psIN_COMMITS_RELEASE=con_.prepareStatement(props_.getString("IN_COMMITS_RELEASE"));
            psOUT_RELEASE_WORKDAYS_COMMITS=con_.prepareStatement(
                                props_.getString("OUT_RELEASE_WORKDAYS_COMMITS"));
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to prepare ReleaseActivity related statements! exiting..." +
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
    }//ReleaseActivity       
    
    
    /**
     *
     */
    public void calcActivity()
    {        
        //get the releases; set of (version,stage,release_date) tuples
        //create a CommitsDaysTuple to each, ie:
        //          (version,stage,release_date) --> (commits_num,days_num)
        //get an iterator over the releases
        ReleasePool pool=getReleases();                
        Hashtable<Release,CommitsDaysTuple> hCommits=getCommitsDaysTuple(pool);
        Iterator<Release> iReleases=pool.iterator();
        
        //for each release
        //      get the set of time(commit), for each commit of the release
        //      set the number of  commits to the number of the above set (1-1 correspondence)
        while (iReleases.hasNext())
        {
            Release r=iReleases.next();
            Set<Timestamp> stimes=getTimesOfCommits(r);   
            hCommits.get(r).setCommitsNumber(stimes.size());
            
            //for each time(commit) expand the time space
            //of the CommitsDaysTuple
            for (Timestamp t: stimes)
            {
                hCommits.get(r).expandDateSpace(t);
            }
        }
        
        try
        {
            for (Release r: hCommits.keySet())
            {
                CommitsDaysTuple t=hCommits.get(r);
                psOUT_RELEASE_WORKDAYS_COMMITS.setLong(1,t.getDays());
                psOUT_RELEASE_WORKDAYS_COMMITS.setLong(2,t.getCommits()); //the new updated date
                
                psOUT_RELEASE_WORKDAYS_COMMITS.setInt(3,PROJECT_ID);
                psOUT_RELEASE_WORKDAYS_COMMITS.setString(4,r.getVersion());
                psOUT_RELEASE_WORKDAYS_COMMITS.setString(5,r.getStage()); //the row identification ones
                
                psOUT_RELEASE_WORKDAYS_COMMITS.executeUpdate();
            }
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to update the Commit table!"+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
        
    }//calcActivity
    
    
    //////////////////////////////////////HELPERS////////////////////////////////////////////    
    private Set<Timestamp> getTimesOfCommits(Release r)
    {
        HashSet<Timestamp> times=new HashSet<Timestamp>();    
        
        try
        {
            psIN_COMMITS_RELEASE.setInt(1,PROJECT_ID);
            psIN_COMMITS_RELEASE.setString(2,r.getVersion());
            psIN_COMMITS_RELEASE.setString(3,r.getStage());

            ResultSet commits=psIN_COMMITS_RELEASE.executeQuery();        
            
            while (commits.next())
            {
                times.add(commits.getTimestamp(1));
            }
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to execute IN_COMMITS_RELEASE query "
                    +MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);            
        }
        
        return times;
    }//getTimesOfCommits
    
    
    
    /**
     * Given a set of Releases, returns a map with
     * keys: Release
     * value: {@link CommitsDaysTuple}
     * @returns {@link paxosk.sqoss.svn.activity.CommitsDaysTuple CommitsDaysTuple}
     */
    private Hashtable<Release,CommitsDaysTuple> getCommitsDaysTuple(ReleasePool pool)
    {
        Hashtable<Release,CommitsDaysTuple> hCommitsDaysTuple=
                                            new Hashtable<Release,CommitsDaysTuple>();
        Iterator<Release> iReleases=pool.iterator();
        while (iReleases.hasNext())
        {
            hCommitsDaysTuple.put(iReleases.next(),new CommitsDaysTuple());
        }
        
        return hCommitsDaysTuple;
    }//getCommitsDaysTuple
    
    
    
    /**
     * Returns the releases, that the given project; identified by 
     * PROJECT_ID has made.
     * @return {@link paxosk.sqoss.svn.common.ReleasePool ReleasePool}
     */
    private ReleasePool getReleases()
    {
        ReleasePool pool=new ReleasePool();
        
        try
        {            
            psIN_RELEASES.setInt(1,PROJECT_ID);
            ResultSet rs= psIN_RELEASES.executeQuery();
            
            while (rs.next())
            {
                String version=rs.getString(1);
                String stage=rs.getString(2);
                Timestamp releaseDate=rs.getTimestamp(3);
                new Release(pool,version,stage,releaseDate);
            }
            
            return pool;
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to retrieve releases from the db! exiting "+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
        
        return pool;
    }//getReleases
    //////////////////////////////////////HELPERS////////////////////////////////////////////
}
