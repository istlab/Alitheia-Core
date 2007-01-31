using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace Metrics.Common
{
	/// <summary>
	/// Holds information about changes between revisions of a project 
	/// </summary>
	public class CommitLog : IEnumerable<CommitLogEntry>
	{
		private Revision start, end;
		private List<CommitLogEntry> entries;

		public Revision Start
		{
			get { return start; }
		}

		public Revision End
		{
			get { return end; }
		}

		internal List<CommitLogEntry> Entries
		{
			get { return entries; }
		}

		public CommitLog(Revision start, Revision end)
		{
			if (start == null || end == null)
			{
				throw new ArgumentNullException();
			}
			this.start = start;
			this.end = end;
			entries = new List<CommitLogEntry>();
		}

		public void Add(CommitLogEntry entry)
		{
			entries.Add(entry);
		}

		public bool Remove(CommitLogEntry entry)
		{
			return entries.Remove(entry);
		}

		public void Clear()
		{
			entries.Clear();
		}

		public int Count
		{
			get { return entries.Count; }
		}

		#region IEnumerable<CommitLogEntry> Members

		public IEnumerator<CommitLogEntry> GetEnumerator()
		{
			return entries.GetEnumerator();
		}

		#endregion

		#region IEnumerable Members

		IEnumerator IEnumerable.GetEnumerator()
		{
			return entries.GetEnumerator();
		}

		#endregion
	}
}
