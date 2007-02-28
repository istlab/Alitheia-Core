/*$Id: */
package eu.sqooss.vcs;

/**
 * An abstract repository representation. 
 */
public abstract class Repository {
    
    public String password;

    public String username;
    
    /* The path of the repository on the local end */
    protected String localPath;

    /* The path of the repository on the remote end */
    protected String serverPath;

    /* The current repository revision on the local end */
    protected Revision revision;
    
    /**
     * 
     */
    public Repository(String localPath, String serverPath, String username, String passwd) {
	this.localPath = localPath;
	this.serverPath = serverPath;
	this.username = username;
	this.password = passwd;
    }

    /**
     * Initialises a local copy of a repository, by checking out the current
     * revision of the repository server
     * 
     */
    public abstract void checkout();

    /**
     * Initialises a local copy of the repository, by checking out
     * 
     * 
     * @param rev
     */
    public abstract void checkout(Revision rev);

    /**
     * Fetches the latest revision from the main repository server
     * 
     * @param rev
     */
    public abstract void update(Revision rev);

    /**
     * Returns a diff between the current repository revision and an older
     * revision
     * 
     * @param rev
     * @return
     */
    public abstract Diff diff(Revision rev);

    /**
     * Returns a diff between the start and end revisions
     * 
     * @param start
     * @param end
     * @return
     */
    public abstract Diff diff(Revision start, Revision end);

    /**
     * Returns the commit log for all the commits between revisions start
     * and end
     * 
     * @param start
     * @param end
     * @return
     */
    public abstract CommitLog getLog(Revision start, Revision end);
    
    /**
     * Returns the current version of either the remote or local version of 
     * the repository
     * @return
     */
    public abstract String getCurrentVersion(boolean remote);
}
