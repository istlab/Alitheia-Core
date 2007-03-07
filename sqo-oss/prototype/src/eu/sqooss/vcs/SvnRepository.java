/*$Id: */
package eu.sqooss.vcs;

import java.io.File;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.admin.SVNWCAccess;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

/**
 * @author circular
 * 
 * Implements the functionality required to access a SVN repository
 * 
 */
public class SvnRepository extends Repository {

    private SVNRepository repository;
    private ISVNAuthenticationManager authManager;
    private static SVNClientManager clientManager;
    //private ISVNEventHandler wcEventHandler;

    public SvnRepository(String localPath, String serverPath, String username,
            String passwd) {
        super(localPath, serverPath, username, passwd);
        initializeFactories();
        repository = null;
    }

    @Override
    public void checkout() {

    }

    @Override
    public void checkout(Revision rev) {

    }

    @Override
    public void update(Revision rev) {

    }

    @Override
    public Diff diff(Revision rev) {

        return null;
    }

    @Override
    public Diff diff(Revision start, Revision end) {

        return null;
    }

    @Override
    public CommitLog getLog(Revision start, Revision end) {

        return null;
    }

    @Override
    public String getCurrentVersion(boolean remote) {
        initializeRepository();
        if(remote) {          
            try {
                revision = new Revision(repository.getLatestRevision());
            }
            catch (SVNException svne) {
                revision = new Revision(-1);
            }
        } else {
            ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
            clientManager = SVNClientManager.newInstance(options, authManager);
            try {
                SVNInfo info = clientManager.getWCClient().doInfo(
                        new File(localPath), SVNRevision.WORKING);
                
                revision = new Revision(info.getRevision().getNumber());
                
            } catch (SVNException svne) {
                System.err.println("Error while retrieving info for the "
                        + "working copy of '" + serverPath + "' at "
                        + localPath + ": " + svne.getMessage());
            }
        }
        
        return revision.getDescription();
    }

    /**
     * Initializes the SVNKit library to work with dirrerent repository
     * remote access methods
     */
    private static void initializeFactories() {

        // for using over http:// and https://
        DAVRepositoryFactory.setup();

        //for using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();

        //for using over file:///
        FSRepositoryFactory.setup();
    }

    private void initializeRepository() {
        if(repository != null) {
            return;
        }
        
        try {
            SVNURL url = SVNURL.parseURIDecoded(serverPath);
            repository = SVNRepositoryFactoryImpl.create(url);

            /* 
             * Default authentication manager first attempts to use provided
             * user name and password and then falls back to the credentials
             * stored in the default Subversion credentials storage that is 
             * located in Subversion configuration area. We dont need / like
             * this kind of behaviour, so we use BasicAuthenticationManager.
             * If anonymous access is requested the authentication is skipped.
             */
            if((username.length() > 0) && (password.length() > 0)) {
                authManager = new 
                    BasicAuthenticationManager(username, password);

                repository.setAuthenticationManager(authManager);
            }
        } catch (SVNException svne){
            //Probably a malformed URL was provided
            System.err.println("Error while creating an SVNRepository for '"
                    + serverPath + "': " + svne.getMessage());
        }
    }
}
