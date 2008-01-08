package paxosk.classes.common;


public class MyClassUtils 
{   
    //TODO:check it by calling from other classes
    public static String getCallerClassName()
    {
        return new Exception().getStackTrace()[2].getClassName();        
    }//getCallerClassName
    

    public static String getStackTraceTabbed(Exception e)
    {
        String s='\t'+e.getMessage()+'\n';
        StackTraceElement[] ste=e.getStackTrace();
        for (StackTraceElement el: ste)
        {
            s+='\t'+el.toString()+'\n';
        }
        
        return s;
    }//getStackTrace
    
    
    public static String getStackTrace(Exception e)
    {
        String s=e.getMessage()+'\n';
        StackTraceElement[] ste=e.getStackTrace();
        for (StackTraceElement el: ste)
        {
            s+=el.toString()+'\n';
        }
        
        return s;
    }//getStackTrace
    
//    //sample use
//    public static void main(String[] args)
//    {
//        System.err.println(getStackTrace(new Exception()));
//    }
}
