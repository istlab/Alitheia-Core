package paxosk.date.common;

import java.util.*;

public class DateConsts 
{
    public static enum MONTH_SHORT {Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec};
    public static enum MONTH_LONG {January,February,March,April,May,June,July,August,
        September,October,November,December};
    public static enum DAYS_SHORT {Mon,Tue,Wed,Thu,Fri,Sat,Sun}
    public static enum DAYS_LONG {Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday}

    
    /**
     * Get the numeric representation of the given month name.
     * @param name the name of the month (either short or in long)
     * @return the numeric representation of the month starting from 0
     * and ending at 11, -1 if the naming is wrong
     */
    public static int getMonthNum(String name) 
    {
        try 
        {
            return Enum.valueOf(MONTH_SHORT.class,name).ordinal();
        }
        catch (IllegalArgumentException e)
        {
            try {
                return Enum.valueOf(MONTH_LONG.class,name).ordinal();
            } catch (IllegalArgumentException ex) {
                    System.err.println("DateConsts: month name "+name+" doesn't exist\n"+ex.getMessage());
            }            
        }
        
        return -1;
    }//getMonthNum
    
    
    /**
     * Get a comma seperated string list of the months
     * @return "Jan,Feb,..."
     */
    public static String getMonthsShortStringList()
    {
        String sList="";
        
        for (MONTH_SHORT c: MONTH_SHORT.values())
        {
            sList+=c.toString()+'|';
        }
        
        String newS=sList.substring(0,sList.length()-1);        
        return newS;
    }//getMonthsShortStringList
    
    
    /**
     * Get a comma seperated string list of the months
     * @return "January,February,..."
     */
    public static String getMonthsLongStringList()
    {
        String sList="";
        
        for (MONTH_LONG c: MONTH_LONG.values())
        {
            sList+=c.toString()+'|';
        }
        
        String newS=sList.substring(0,sList.length()-1);          
        return newS;        
    }//getMonthsLongStringList
    
    
    /**
     * Get a comma seperated string list of the days
     * @return "Mon,Tue,..."
     */
    public static String getDaysShortStringList()
    {
        String sList="";
        
        for (DAYS_SHORT c: DAYS_SHORT.values())
        {
            sList+=c.toString()+'|';
        }
        
        String newS=sList.substring(0,sList.length()-1);                  
        return newS;
    }//getDaysShortStringList
    
    
    /**
     * Get a comma seperated string list of the days
     * @return "Monday,Tuesday,..."
     */
    public static String getDaysLongStringList()
    {
        String sList="";
        
        for (DAYS_LONG c: DAYS_LONG.values())
        {
            sList+=c.toString()+'|';
        }

        String newS=sList.substring(0,sList.length()-1);        
        return newS;        
    }//getDaysLongStringList    
    
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        System.err.println(getMonthNum("April"));
//        System.err.println(getMonthsShortStringList());
//    }
}
