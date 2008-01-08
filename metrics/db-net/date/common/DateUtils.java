package paxosk.date.common;

import paxosk.string.common.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

/**
 * Utility class used for transformations between different forms of dates and
 * times and timestamps
 */
public class DateUtils 
{
    /**
     * Transforms to the timestamp representation (YYYYMMDDHHMMSS) a string 
     * of the from "Tue, 16 Jan 2007 09:20:17 -0500" or of the form 
     * "19 Jan 2007 17:15:41 -0000"
     * (string is acquired from the "Date" header of MIME e-mail messages)
     * @param s the date string in its original form
     * @return the transformed string
     */
    public static String getMimeMessageTimestamp(String s)
    {
        String months=DateConsts.getMonthsShortStringList(); //get a months disjunctive list
        String days=DateConsts.getDaysShortStringList();     //get a days disjunctive list   
        s=s.replace("  "," "); //replace double spaces with single space
          
        //ALL of the patterns below can track dates, where their individual parts are
        //considered to be separated by either tabs or any number of whitespaces
        
        //try to track the "Tue, 16 Jan 2007 09:20:17 -0500" pattern 
        //or the
        //"Tue, 16 Jan 2007 9:20:17 -0500" pattern --> hour with one digit only
        String datepattern1="("+days+"),\\s\\d{1,2}\\s("+months+")\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}.*";
        Pattern pattern1=Pattern.compile(datepattern1);
        Matcher matcher1=pattern1.matcher((CharSequence)s);
        boolean found1=matcher1.find();
        
        //try to track the "19 Jan 2007 17:15:41 -0000" pattern 
        String datepattern2="\\d{1,2}\\s("+months+")\\s\\d{4}\\s\\d{2}:\\d{2}:\\d{2}.*";
        Pattern pattern2=Pattern.compile(datepattern2);
        Matcher matcher2=pattern2.matcher((CharSequence)s);
        boolean found2=matcher2.find();        

        //try to track the "19 Jan 2007 17:15:41 -0000 (PST)" pattern 
        String datepattern3="("+days+"),\\s\\d{1,2}\\s("+months+")\\s\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s(.*)\\s\\(\\w+\\).*";
        Pattern pattern3=Pattern.compile(datepattern3);
        Matcher matcher3=pattern3.matcher((CharSequence)s);
        boolean found3=matcher3.find();                
        
        String[] stmpDate=s.split(" "); //split based upon single whitespace        

        String snew="";
        
        //"Tue, 16 Jan 2007 09:20:17 -0500"
        //or the
        //"Tue, 16 Jan 2007 9:20:17 -0500" pattern --> hour with one digit only        
        if (found1)
        {
            snew+=StringUtils.getZeroPadded(stmpDate[3],4)+ //append year
                    StringUtils.getZeroPadded(String.valueOf(DateConsts.getMonthNum(stmpDate[2])+1),2)+ //append month; add 1, because in
                                                                                            //original [0,11], but in SQL [1,12]
                        StringUtils.getZeroPadded(stmpDate[1],2); //append days

            String[] stmpTime=stmpDate[4].split(":");
            snew+=StringUtils.getZeroPadded(stmpTime[0],2)
                        +StringUtils.getZeroPadded(stmpTime[1],2)
                            +StringUtils.getZeroPadded(stmpTime[2],2);
        }
        //found2:"19 Jan 2007 17:15:41 -0000" 
        else if (found2)
        {
            snew+=StringUtils.getZeroPadded(stmpDate[2],4)+ //append year
                    StringUtils.getZeroPadded(String.valueOf(DateConsts.getMonthNum(stmpDate[1])+1),2)+ //append month; add 1, because in
                                                                                            //original [0,11], but in SQL [1,12]
                        StringUtils.getZeroPadded(stmpDate[0],2); //append days

            String[] stmpTime=stmpDate[3].split(":");
            System.out.println(Arrays.toString(stmpTime));
            snew+=stmpTime[0]+stmpTime[1]+stmpTime[2];            
        }
        //found3:"Sun, 7 Jan 2007 06:56:10 -0800 (PST)"
        else if (found3)
        {
            snew+=StringUtils.getZeroPadded(stmpDate[3],4)+ //append year
                    StringUtils.getZeroPadded(String.valueOf(DateConsts.getMonthNum(stmpDate[2])+1),2)+ //append month; add 1, because in
                                                                                            //original [0,11], but in SQL [1,12]
                        StringUtils.getZeroPadded(stmpDate[1],2); //append days

            String[] stmpTime=stmpDate[4].split(":");
            snew+=stmpTime[0]+stmpTime[1]+stmpTime[2];             
        }
        else
        {
            System.err.println("Unknown date pattern has been detected in the case of date: "+s);
        }
        
        return snew;
    }//getMimeMessageTimestamp
    
    
    /** 
     * Transforms a date string of the form "year.month.day" to a timestamp of
     * fixed time 12:00:00
     * @param s the date string in its original form
     * @return the transformed form of the date
     */
    public static String getSimpleTimestamp(String s)
    {
        String snew="";
        String[] stmp=s.split("\\.");
        String year=stmp[0];
        //add 1, because in original [0,11], but in SQL [1,12]
        String month=StringUtils.getZeroPadded(String.valueOf(Integer.valueOf(stmp[1])+1),2); 
        String day=StringUtils.getZeroPadded(stmp[2],2);
        
        snew+=year+month+day;
        snew+="120000";
                
        return snew;
    }//getSimpleTimestamp
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        String s="Sun, 7 Jan 2007 06:56:10 -0800 (PST)";
//        System.err.println(getMimeMessageTimestamp(s));
//    }//main
}
