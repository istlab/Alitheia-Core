using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Common
{
	public class CvsRepository : Repository
	{
		/// <summary>
		/// 
		/// </summary>
		/// <param name="rev"></param>
		public override void Checkout(Revision rev)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="rev"></param>
		public override void Update(Revision rev)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="rev"></param>
		/// <returns></returns>
		public override Diff Diff(Revision rev)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public override Diff Diff(Revision start, Revision end)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public override CommitLog GetLog(Revision start, Revision end)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		/// <summary>
		/// Creates a new instance of the <see cref="CvsRepository"/> class
		/// </summary>
		/// <param name="path">The repository path - usually a Url</param>
		/// <param name="localPath">The path of the working copy</param>
		public CvsRepository(string path, string localPath) : base(path, localPath)
		{
			executablePath = @"/usr/bin/cvs";
		}

		//to get a list of files for cvs and their revision numbers, use cvs status -R

	}
}
