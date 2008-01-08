package paxosk.string.common;

import java.util.*;

public class StringUtils 
{
    //system wide properties
    private static Properties sysProps_=System.getProperties();
    
    /**
     * Get a string padded with zeroes at the beginning
     * @param s the string, that is to be padded
     * @param length the length the string must have in the end
     */
    public static String getZeroPadded(String s, int length)
    {
        String news="";
        
        if (s.length()>=length)
        {
            return s;
        }
        else {
            int zeroNum=length-s.length();
            for (int i=0; i<zeroNum; i++)
            {
                news+='0';
            }
            
            news+=s;
        }
        
        return news;
    }//getZeroPadded                
    
    
    /**
     * Get a string padded with zeroes at the beginning
     * @param s the int, that is to be padded
     * @param length the length the string must have in the end
     */    
    public static String getZeroPadded(int i,int length)
    {
        return getZeroPadded(String.valueOf(i),length);
    }//getZeroPadded
    
    
    
    public static String getFullYear(int i)
    {
        return String.valueOf(i+1900);           
    }//getFullYear
    
    
    /**
     * Count the lines of a given text
     * @param txt the string representation of the text
     * @return the number of lines
     */
    public static int countLines(String txt)
    {
        char deliner='\n';
        //String sep=sysProps_.getProperty("line.separator");        
        int iLines=0;        
        
        char[] carray=txt.toCharArray();
        for (int i=0; i<carray.length; i++)
        {
            if (carray[i]==deliner) {
                iLines++;
            }
        }       
        
        return iLines;
    }//countLines
    
    
//    //sample use    
//    public static void main(String[] args)
//    {
//        //System.err.println(getZeroPadded("123",5));
//        System.out.println(countLines("dwsD\nasdf\nasdf"));
//    }
}
