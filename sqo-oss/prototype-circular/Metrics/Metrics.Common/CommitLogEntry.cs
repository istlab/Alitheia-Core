using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Common
{
	/// <summary>
	/// Represents a log entry containing comments, author and date/time information
	/// </summary>
	/// <remarks>If one of the fields of a commit log entry could be used as a unique ID (the date is not
	/// a safe option, perhaps a hash/combination of author and date could do) then the CommitLog could
	/// hold a list of entries sorted by this ID to get a chronologically ordered list.</remarks>
	[Serializable]
	public class CommitLogEntry
	{
		/// <summary>
		/// Information about the author who performed a commit
		/// </summary>
		public readonly string Author;
		/// <summary>
		/// The comment logged by a commiter
		/// </summary>
		public readonly string Comment;
		/// <summary>
		/// The date and time when the commit was performed
		/// </summary>
		public readonly DateTime Date;
		/// <summary>
		/// The identifier of the revision / commit
		/// </summary>
		public readonly string Revision;

		/// <summary>
		/// Constructs a new instance of the class
		/// </summary>
		/// <param name="author"></param>
		/// <param name="comment"></param>
		/// <param name="date"></param>
		public CommitLogEntry(string author, string comment, DateTime date, string revision)
		{
			Author = author;
			Comment = comment;
			Date = date;
			Revision = revision ?? string.Empty;
		}

		//todo: add code to parse a string into an instance of the object
	}
}
