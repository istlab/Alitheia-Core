using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Common
{
	/// <summary>
	/// 
	/// </summary>
	public abstract class FileEntry
	{
		private string name;
		private string fullPath;
		int size;
		string revision;
		EntryKind kind;
		private Dictionary<string, string> attributes;

		/// <summary>
		/// 
		/// </summary>
		public string Name
		{
			get { return name; }
			internal set { name = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public string FullPath
		{
			get { return fullPath; }
			internal set { fullPath = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public Dictionary<string, string> Attributes
		{
			get { return attributes; }
		}

		/// <summary>
		/// 
		/// </summary>
		public int Size
		{
			get { return size; }
			internal set { size = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public string Revision
		{
			get { return revision; }
			internal set { revision = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		public EntryKind Kind
		{
			get { return kind; }
			internal set { kind = value; }
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="name"></param>
		public FileEntry(string name)
		{
			this.name = name;
			fullPath = string.Empty;
			attributes = new Dictionary<string, string>();
			kind = EntryKind.Unknown;
		}

		public static EntryKind ParseEntryKind(string kind)
		{
			switch (kind.ToLower())
			{
				case "file":
					return EntryKind.File;

				case "dir":
					return EntryKind.Dir;

				default:
					return EntryKind.Unknown;
			}
		}

		public enum InputDataFormat
		{
			Plain,
			Svn,
			SvnXml,
			SvnLogXml,
			Cvs
		}

		public enum EntryKind
		{
			Unknown,
			File,
			Dir
		}
	}
}
