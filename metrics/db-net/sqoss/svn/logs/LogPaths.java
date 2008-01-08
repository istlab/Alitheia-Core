package paxosk.sqoss.svn.logs;

import java.util.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;

public class LogPaths 
{
    private int iMakes_=0;
    private int iDevs_=0;
    private int iDocs_=0;
    private int iGraphs_=0;
    private int iPaths_=0;
    private String sPaths_="";
    
    public LogPaths() 
    {
    }
    
    public void exec(SVNLogEntry entry)
    {            	    
        Map<String,SVNLogEntryPath> mPaths=entry.getChangedPaths();
        
        //if no paths have been changed
        if (mPaths==null) {
            return;
        }
            
        iPaths_=mPaths.size();
        
        for (String path: mPaths.keySet())
        {
            sPaths_+=path+'\n';
                    
            //get the extension
            String[] tmp=path.split(".");
            String ext=tmp[tmp.length-1];

            //makes: CMakeLists.txt || Makefile.am || Makefile.in || *.cmake
            if (ext=="am" || ext=="in" || ext=="cmake") {
                iMakes_++;
            }

            //hardcore development
            if (ext=="h" || ext=="cpp" || ext=="cc" || ext=="php"
                                    || ext=="py" || ext=="idl" || ext=="sql") {
                iDevs_++;
            }

            //documentation-translation
            if (ext=="txt" || ext=="html" || ext=="dox" || ext=="docbook" 
                    || ext=="sgml" || ext=="changelog" || ext=="po") {
                    iDocs_++;
            }

            //graphics
            if (ext=="svg" || ext=="png" || ext=="themerc" || ext=="kcsrc" 
                    || ext=="lsm" || ext=="nif") {
                    iGraphs_++;
            }
        }
    }//exec
    
    
    protected int getGraphicsExtensions()
    {
        return iGraphs_;
    }//getGraphicsExtensions
    
    
    protected int getDevelExtensions()
    {
        return iDevs_;
    }//getDevelExtensions
    
    protected int getMakeExtensions()
    {
        return iMakes_;
    }//getMakeExtensions
    
    protected int getDocExtensions()
    {
        return iDocs_;
    }//getDocExtensions
    
    protected int getAllPathsNumber()
    {
        return iPaths_;
    }//getAllPathsNumber
       
    protected String getAllPathsString()
    {
        return sPaths_;
    }//getAllPathString
}
