package eu.sqooss.vcs;

/**
 * Describes a source code repository that is located either on the web
 * or in a specific path on disk
 */
public abstract class Repository {

	protected String executablePath; //this should be configurable
	private String path;
	private String localPath;
	private Revision revision;
	protected AuthCredentials credentials;
	protected Object sync; //used for locking operations

	/// <summary>
	/// Gets the local path of the repository
	/// </summary>
	public String getLocalPath()
	{
		return localPath;
	}

	/// <summary>
	/// Gets the remote path of the repository
	/// </summary>
	public String getPath()
	{
		return path;
	}

	/// <summary>
	/// Gets or sets the current revision of the repository
	/// </summary>
	public Revision getRevision()
	{
		return revision; 	
	}
	
	protected void setRevision(Revision rev)
	{
		if (rev == null)
		{
			throw new IllegalArgumentException();
		}
		revision = rev;
		//TODO: if the repository is already loaded and it is checked out on the disk
		// then we should update to the revision that has just been set
	}

	/// <summary>
	/// Creates a new instance of the <see cref="Repository"/> class
	/// </summary>
	/// <param name="path">The repository path - usually a Url</param>
	/// <param name="localPath">The path of the working copy</param>
	public Repository(String path, String localPath)
	{
		this.path = path;
		this.localPath = localPath;
		executablePath = "";
		sync = new Object();
	}

	/// <summary>
	/// Creates a new instance of the <see cref="Repository"/> class
	/// </summary>
	/// <param name="path">The repository path</param>
	/// <param name="localPath">The path of the working copy</param>
	/// <param name="credentials">The credentials required to access the repository</param>
	public Repository(String path, String localPath, AuthCredentials credentials)
	{
		this(path, localPath);
		this.credentials = credentials;
	}

	/// <summary>
	/// Checks out the given revision of a repository/module
	/// </summary>
	/// <param name="rev">The revision to check out</param>
	public abstract void checkout(Revision rev);

	/// <summary>
	/// Updates the working copy of the repository to the given revision
	/// </summary>
	/// <param name="rev">The revision to update to</param>
	public abstract void update(Revision rev);

	/// <summary>
	/// Diffs current repository version with revision rev
	/// </summary>
	/// <param name="rev"></param>
	/// <returns></returns>
	public abstract Diff diff(Revision rev);

	/// <summary>
	/// Returns diff between two different revisions of the repository
	/// </summary>
	/// <param name="start"></param>
	/// <param name="end"></param>
	/// <returns></returns>
	public abstract Diff diff(Revision start, Revision end);

	/// <summary>
	/// Gets the log from a project's repository between the given versions
	/// </summary>
	/// <param name="start"></param>
	/// <param name="end"></param>
	/// <returns></returns>
	public abstract CommitLog getLog(Revision start, Revision end);
	
}
