package paxosk.sqoss.svn.common;

import java.util.*;


public class ReleaseBasic 
{
    /** stores the original unprocessed value */    
    protected String tag_="";
    
    /** a table, that maps stage (coded(?)) stage namings, that are internally used  
     * in the tags dir to standard naming used in our application */
    protected static Hashtable<String,String> stageNamings=new Hashtable<String,String>();
    
    /** maps the official stage naming to a unique value */
    protected static Hashtable<String,Integer> hStageInt_=new Hashtable<String,Integer>();    
    
    /** the fields storing the unprocessed version and stage values 
     * originating from the original value */
    protected String versionOrig_=""; //3.4       
    protected String stageOrig_=""; //"a","b","beta","rc1",etc    
    
    
    /** the fields storing the processed values */
    protected String version_=""; //3.04.00
    protected String stage_=""; //Stable,Alpha, Beta, RC1,RC2, RC3        
    
    /////////////////////////////////CONSTRUCTORS/////////////////////////////////////
    public ReleaseBasic(String tag) 
    {
        init();
        tag_=tag;
        
        //it may be, that we are trying to create Release objects by existing
        //processed (version_official,stage_official) tuples, WITHOUT having 
        //the original tags; if we do not have the original tags, do not try
        //to calculate un-/official version and stages!
        if (!tag.equals(""))
        {
            versionOrig_=calcVersion(tag);
            stageOrig_=calcStage(tag);
            //initialization: the processed form
            stage_=calcOfficialStage(stageOrig_);
            version_=calcOfficialVersion(versionOrig_);            
        }                
    }//ReleaseBasic       
    /////////////////////////////////CONSTRUCTORS/////////////////////////////////////
    
    
    public static void init()
    {
        initStageTable();    
        initStageOfficialTable();        
    }//init
    
    
    //////////////////////////////////HELPERS//////////////////////////////////////////
    /**
     * Given an unofficial stage naming, returns the official one
     * @param s the unofficial stage naming
     * @return the official stage naming
     */
    private String calcOfficialStage(String s)
    {
    	if (stageNamings.containsKey(s)) {
    		return stageNamings.get(s);
    	}
    	else {
    		return "";
    	}
    }//calcOfficialStage    
    
    
    private String calcOfficialVersion(String s)
    {
        String version="";
        
        String versionOrigCP=new String(s);
        //if version is x.y do it x.y.0
        int vlen=versionOrigCP.split("\\.").length;
        if (vlen==2) {
            versionOrigCP+=".0";                
        }

        //if version is now x.y.z or x.y.0, turn it into
        //x.y.z or x.y.00, where x,y,z have 2 digits            
        String[] versDigits=versionOrigCP.split("\\.");
        version=versDigits[0];
        for (int i=1; i<versDigits.length; i++)
        {
            if (versDigits[i].length()==1) {
                version+=".0"+versDigits[i];
            } else {
                version+='.'+versDigits[i];
            }
        }             
        
        return version;
    }//calcOfficialVersion
    
    
    /**
     * Extracts the version string from a version-stage mixed string
     * @param s the mixed version-stage string
     * @return String the version
     */
    private String calcVersion(String s)
    {    	
    	char[] car=s.toCharArray();
    	int i=0;
    	String version="";
    	
    	while (!Character.isDigit(car[i])) {
    		i++;
    		//do not continue if we have reached the end
    		if (i==s.length()) {
    			return version;
    		}    		    	
    	}    	
    	
    	while  (i<car.length)
    	{
    		if ((!Character.isDigit(car[i])) && (car[i]!='.')) {
    			return version;
    		}
    		else {
    			version+=car[i];
    		}   
    		
    		i++;
    	}
    	
    	return version;
    }//calcVersion  
    
    
    
    /**
     * Extracts the stage string from a version-stage mixed string
     * 
     * @param s the mixed version-stage string
     * @return String the stage (not the official); must call 
     * (@link calcOfficialStage(String) calcOfficialStage) afterwards
     */
    private String calcStage(String s)
    {
    	char[] car=s.toCharArray();
    	int i=0;
    	String stage="";
    	
        //overpass all number and '.' characters representing versions
    	while (Character.isDigit(car[i]) || car[i]=='.') {    		
    		i++;
    		//do not continue if we have reached the end; return ""
    		if (i==s.length()) {
    			return stage;
    		}
    	}    	
    	
        
        //overpass
    	while  (i<car.length)
    	{
            stage+=car[i];
            i++;
    	}
    	
    	return stage;    
    }//calcStage        
    
    
    /**
     * Finds out whether s is a number 
     * @param s the number string representation (maybe)
     * @return boolean true: is number, false: isn't
     */
    private boolean isNumber(String s)
    {
    	try {
    		Integer.parseInt(s);
    	}
    	catch (NumberFormatException e) {
    		return false;
    	}
    	
    	return true;    		
    }//isNumber       
    
    
    /**
     * Initialize the tables, which maps all the possible unofficial stage 
     * namings to official ones
     */
    private static void initStageTable()
    {
        stageNamings.put("Alpha","Alpha");
        stageNamings.put("alpha","Alpha");
        stageNamings.put("a","Alpha");
    	stageNamings.put("A","Alpha");
     	stageNamings.put("APLHA","Alpha");
        stageNamings.put("Beta","Beta");
        stageNamings.put("beta","Beta");
        stageNamings.put("b","Beta");
        stageNamings.put("B","Beta");
        stageNamings.put("BETA","Beta");
        stageNamings.put("","Stable");
        stageNamings.put("s","Stable");
        stageNamings.put("S","Stable");
        stageNamings.put("stable","Stable");
        stageNamings.put("Stable","Stable");
        stageNamings.put("STABLE","Stable");
        stageNamings.put("rc","RC1");
        stageNamings.put("rc1","RC1");
        stageNamings.put("RC1","RC1");        
        stageNamings.put("rc2","RC2");
        stageNamings.put("RC2","RC2");
        stageNamings.put("rc3","RC3");
        stageNamings.put("RC3","RC3");
    }//initStageTable    
    
    
    private static void initStageOfficialTable()
    {
        //initialization: stages and the corresponding values
        hStageInt_.put("Stable",6);
        hStageInt_.put("RC3",5);
        hStageInt_.put("RC2",4);
        hStageInt_.put("RC1",3);
        hStageInt_.put("Beta",2);
        hStageInt_.put("Alpha",1);                
    }//initStageOfficialTable       
    //////////////////////////////////HELPERS//////////////////////////////////////////    
}
