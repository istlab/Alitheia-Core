package paxosk.sqoss.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


/**
 * Establishes an (anonymous or not) connection to an SVN repository, by using any possible protocol.
 * To establis a connection do:
 * SVNConnection([usr,pswd]).openConnection(url);
 *
 * One can also learn the head revision, as well as get a handle to the repository object 
 * (SVNRepository)
 *
 * NOTE:
 * =======
 * For one to be able to use this SVN package, the SVN repository must follow the generally
 * (trunk/, branches/, tags/) structure
 */
public class SVNConnection 
{
    //////////////////////////CONSTRUCTOR FIELDS/////////////////////////////////////	
    //the username, if required; anonymous by default
    private String username_="anonymous";

    //the password, if required; anonymous by default
    private String passwd_="anonymous"; 
    //////////////////////////CONSTRUCTOR FIELDS/////////////////////////////////////    
    
    //the URL of the repository
    private String url_="";	    
    
    //HEAD revision number
    private long headRevision_=-1;

    //the repository
    private SVNRepository repository_=null;	
    
    
    /**
     * Constructor:
     * setup the library for an anonymous connection
     */
    public SVNConnection()
    {
    	setupLibrary();
    }//SVNConnection
    
    
    /**
     * Constructor:
     * setup the library for a connection by using the following username and passwords
     * @param username the username for the connection
     * @param password the corresponding pass
     */
    public SVNConnection(String username, String password)
    {
    	setupLibrary();
    	username_=username;
    	passwd_=password;
    }//SVNConnection    
    
    
    /**
     * Initialize and open the repository for the given url by using the given username 
     * and password. BE CAREFUL! The url given is the URL to the base of the repository!
     * (where the branches,tags and trunk dirs can be found)
     * @url the URL to the base of the repository
     */
    public SVNRepository openConnection(String url)
    {
    	url_=url;
    	
        try 
        {
            repository_= SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url_));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username_, passwd_);
            repository_.setAuthenticationManager(authManager);
            findHeadRevision();
        } 
        catch (SVNException svne) 
        {
            System.err.println("Failed to open repository "+ url_ + ": " + svne.getMessage());
            System.exit(-1);
        }		
        
        return repository_;
    }//openRepository
    
    
    public long getHeadRevision()
    {
    	return headRevision_;
    }//getHeadRevision
    
    //////////////////////////////////////////////HELPERS/////////////////////////////////////////////////////////
    /**
     * Initializes the SVN library to work with a repository via 
     * different protocols.
     */
    private void setupLibrary() 
    {        
        //Use over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();

        //Use over file:///
        FSRepositoryFactory.setup();
    }//setupLibrary    
    
    
    /**
     * Finds and gets the head revision number
     * @return the head revision number
     */
    public long findHeadRevision()
    {
        try 
        {
            headRevision_ = repository_.getLatestRevision();
        } 
        catch (SVNException svne) 
        {
            System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
            System.exit(-1);
        }
        
        return headRevision_;        
    }//getHeadRevision    
    //////////////////////////////////////////////HELPERS/////////////////////////////////////////////////////////
    
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        SVNConnection sc=new SVNConnection();
//        sc.openConnection("svn://anonsvn.kde.org/home/kde/");
//        System.err.println(sc.getHeadRevision());
//        System.err.println(sc.findHeadRevision());
//    }//main
}
