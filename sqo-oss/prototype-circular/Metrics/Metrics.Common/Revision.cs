using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Common
{
	/// <summary>
	/// Represents a Version Control System revision
	/// </summary>
	[Serializable]
	public class Revision
	{
		private int number;
		private string description;
		//TODO: Change this with a custom collection
		private List<FileEntry> files;

		#region Properties

		/// <summary>
		/// 
		/// </summary>
		public int Number
		{
			get { return number; }
			set { number = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public string Description
		{
			get { return description; }
			set { description = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public List<FileEntry> Files
		{
			get { return files; }
		}

		#endregion

		/// <summary>
		/// 
		/// </summary>
		/// <param name="number">0 for HEAD (latest)</param>
		public Revision(int number)
		{
			if (number < 0)
			{
				throw new ArgumentOutOfRangeException();
			}
			this.number = number;
			this.description = number.ToString();
			files = new List<FileEntry>();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="description"></param>
		public Revision(string description)
		{
			if (string.IsNullOrEmpty(description))
			{
				throw new ArgumentException();
			}
			this.description = description;
			//parse revision number from description if possible
			files = new List<FileEntry>();
		}
	}
}
