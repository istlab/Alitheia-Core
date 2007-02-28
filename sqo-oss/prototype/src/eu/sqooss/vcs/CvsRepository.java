/*$Id: */
package eu.sqooss.vcs;

/**
 * @author circular
 *
 */
public class CvsRepository extends Repository {


    public CvsRepository(String localPath, String serverPath, String username, String passwd) {
	super(localPath, serverPath, username, passwd);
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
    public void checkout() {

    }

    @Override
    public String getCurrentVersion(boolean remote) {
	return null;
    }
    
}
