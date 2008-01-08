package paxosk.sqoss.svn.activity.dbupdate;

import java.util.Date;

/**
 * Class representing a (num_of_commits, days_required) tuple. 
 * Basic functions performed on the tuple:
 * -{@link incCommit() incCommit} increase the number of commits by one
 * -{@link expandDateSpace(Date)} expand the days-space required
 */
public class CommitsDaysTuple
{
    //the number of days required for the commits below
    private long days_=0; 
    //the number of commits
    private long commits_=0;
    //earlier date of commits
    private Date start_=null;
    //later date of commits 
    private Date end_=null;

    
    public CommitsDaysTuple()
    {
        
    }    

    /**
     * Gets the number of commits
     * @return commit number
     */
    public long getCommits()
    {
        return commits_;
    }//getCommits


    /**
     * Increases by 1 the number of commits
     * @return long the number of commits
     */
    public long incCommit() {
            return ++commits_;
    }//incCommit
    
    
    /**
     * Directly sets the number of commits
     * @param num
     */
    public void setCommitsNumber(long num) {
        commits_=num;
    }


    /**
     * Checks if the new date is between the (start_,end_)
     * space. Yes? Do nothing. No? expand to the:
     * startingDate if newDate-before-startingDate
     * endingDate if newDate-after-endDate
     * @param newDate
     * @return long the number of days in the expanded (or not)
     * timespace
     *
     * If this is the first time the method is called the starting 
     * and ending date are initialized with newDate and 0 is returned.
     */
    public long expandDateSpace(java.sql.Timestamp stamp)
    {
        return expandDateSpace((Date) stamp);
    }//expandDateSpace
    
    
    /**
     * Checks if the new date is between the (start_,end_)
     * space. Yes? Do nothing. No? expand to the:
     * startingDate if newDate-before-startingDate
     * endingDate if newDate-after-endDate
     * @param newDate
     * @return long the number of days in the expanded (or not)
     * timespace
     *
     * If this is the first time the method is called the starting 
     * and ending date are initialized with newDate and 0 is returned.
     */
    public long expandDateSpace(Date newDate)
    {
        //if this is the first time we do an expand,
        //initialize the starting and ending date
        if ((start_==null) && (end_==null))
        {
            start_=end_=newDate;
            return 0;
        }
        else
        {
            //if the space has not been expanded, do not repeat
            //the calculation of the days again
            boolean dspaceExpanded=false;

            //newDate-before-startingDate
            if (newDate.before(start_)) {
                    start_=newDate;
                    dspaceExpanded=true;
            }

            //newDate-after-endDate
            if (newDate.after(end_)) {
                    end_=newDate;
                    dspaceExpanded=true;
            }

            //if the date has been expanded do the calculation
            if (dspaceExpanded) {
                days_=((end_.getTime()-start_.getTime())/86400000);
            }

            return days_;				    			
        }
    }//expandDateSpace


    
    
    public long getDays()
    {
        return days_;        
    }//getDays
    
    
    public long getCommitsPerDay()
    {
        return commits_/days_;
    }//getCommitsPerDay
    
    
    public String toString()
    {
            String s="";
            return "<"+days_+" "+commits_+">";
    }//toString



//        public static void main(String[] args)
//        {
//            CommitsDaysTuple cdt=new CommitsDaysTuple();
//            
//            for (int i=0; i<123; i++)
//                cdt.incCommit();
//            
//            cdt.expandDateSpace(new Date(2003,4,1));            
//            System.out.println(cdt);
//            
//            cdt.expandDateSpace(new Date(2003,1,1));
//            System.out.println(cdt);
//            
//            for (int i=0; i<123; i++)
//                cdt.incCommit();
//            
//            cdt.expandDateSpace(new Date(2003,3,1));
//            System.out.println(cdt);            
//            
//            cdt.expandDateSpace(new Date(2002,3,1));
//            System.out.println(cdt);                       
//        }
}//CommitsDaysTuple    
