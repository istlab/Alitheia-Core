using System;
using System.Collections.Generic;
using System.Text;
using System.Net;

namespace Metrics.Common
{
	/// <summary>
	/// Describes a source code repository that is located either on the web or in a specific path on disk
	/// </summary>
	[Serializable]
	public abstract class Repository
	{
		protected string executablePath; //this should be configurable
		private string path;
		private string localPath;
		private Revision revision;
		protected NetworkCredential credentials;
		protected object sync;

		#region Public properties

		/// <summary>
		/// Gets the local path of the repository
		/// </summary>
		public string LocalPath
		{
			get { return localPath; }
		}

		/// <summary>
		/// Gets the remote path of the repository
		/// </summary>
		public string Path
		{
			get { return path; }
		}

		/// <summary>
		/// Gets or sets the current revision of the repository
		/// </summary>
		public Revision Revision
		{
			get { return revision; }
			protected set 
			{
				if (value == null)
				{
					throw new ArgumentNullException();
				}
				revision = value;
				//TODO: if the repository is already loaded and it is checked out on the disk
				// then we should update to the revision that has just been set
			}
		}

		#endregion

		/// <summary>
		/// Creates a new instance of the <see cref="Repository"/> class
		/// </summary>
		/// <param name="path">The repository path - usually a Url</param>
		/// <param name="localPath">The path of the working copy</param>
		public Repository(string path, string localPath)
		{
			this.path = path;
			this.localPath = localPath;
			executablePath = string.Empty;
			sync = new object();
		}

		/// <summary>
		/// Creates a new instance of the <see cref="Repository"/> class
		/// </summary>
		/// <param name="path">The repository path</param>
		/// <param name="localPath">The path of the working copy</param>
		/// <param name="credentials">The credentials required to access the repository</param>
		public Repository(string path, string localPath, NetworkCredential credentials)
			: this(path, localPath)
		{
			this.credentials = credentials;
		}

		/// <summary>
		/// Checks out the given revision of a repository/module
		/// </summary>
		/// <param name="rev">The revision to check out</param>
		public abstract void Checkout(Revision rev);

		/// <summary>
		/// Updates the working copy of the repository to the given revision
		/// </summary>
		/// <param name="rev">The revision to update to</param>
		public abstract void Update(Revision rev);

		/// <summary>
		/// Diffs current repository version with revision rev
		/// </summary>
		/// <param name="rev"></param>
		/// <returns></returns>
		public abstract Diff Diff(Revision rev);

		/// <summary>
		/// Returns diff between two different revisions of the repository
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public abstract Diff Diff(Revision start, Revision end);

		/// <summary>
		/// Gets the log from a project's repository between the given versions
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public abstract CommitLog GetLog(Revision start, Revision end);
	}
}
