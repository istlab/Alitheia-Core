using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Common
{
	/// <summary>
	/// Holds information about files that changed between two revisions
	/// </summary>
	public class Diff : IEnumerable<FileEntry>
	{
		//hold a list of files that changed between the two revisions, not the file differences
		private Dictionary<string, FileEntry> changedFiles;

		public Diff()
		{
			changedFiles = new Dictionary<string, FileEntry>();
		}

		internal void Add(FileEntry item)
		{
			changedFiles.Add(item.Name, item);
		}

		internal void Clear()
		{
			changedFiles.Clear();
		}

		internal bool Remove(FileEntry item)
		{
			return changedFiles.Remove(item.Name);
		}

		public FileEntry this[string name]
		{
			get
			{
				return changedFiles[name];
			}
		}

		#region IEnumerable<FileEntry> Members

		public IEnumerator<FileEntry> GetEnumerator()
		{
			return changedFiles.Values.GetEnumerator();
		}

		#endregion

		#region IEnumerable Members

		System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
		{
			return changedFiles.Values.GetEnumerator();
		}

		#endregion
	}
}
