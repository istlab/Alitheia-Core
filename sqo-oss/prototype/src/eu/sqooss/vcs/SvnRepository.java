package eu.sqooss.vcs;

/**
 * @author circular
 *
 */
public class SvnRepository extends Repository {

	/**
	 * @param path
	 * @param localPath
	 */
	public SvnRepository(String path, String localPath) {
		super(path, localPath);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param path
	 * @param localPath
	 * @param credentials
	 */
	public SvnRepository(String path, String localPath,
			AuthCredentials credentials) {
		super(path, localPath, credentials);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see sqooss.prototype.common.Repository#checkout(sqooss.prototype.common.Revision)
	 */
	@Override
	public void checkout(Revision rev) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sqooss.prototype.common.Repository#update(sqooss.prototype.common.Revision)
	 */
	@Override
	public void update(Revision rev) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sqooss.prototype.common.Repository#diff(sqooss.prototype.common.Revision)
	 */
	@Override
	public Diff diff(Revision rev) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sqooss.prototype.common.Repository#diff(sqooss.prototype.common.Revision, sqooss.prototype.common.Revision)
	 */
	@Override
	public Diff diff(Revision start, Revision end) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sqooss.prototype.common.Repository#getLog(sqooss.prototype.common.Revision, sqooss.prototype.common.Revision)
	 */
	@Override
	public CommitLog getLog(Revision start, Revision end) {
		// TODO Auto-generated method stub
		return null;
	}

}
