using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.IO;
using System.Xml;

namespace Metrics.Common
{
	public class SvnFileEntry : FileEntry
	{
		private static Regex simpleRegex;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="name"></param>
		public SvnFileEntry(string name)
			: base(name)
		{ }

		/// <summary>
		/// 
		/// </summary>
		/// <param name="input"></param>
		/// <param name="repository"></param>
		/// <returns></returns>
		public static List<SvnFileEntry> Parse(string input, Repository repository, FileEntry.InputDataFormat format)
		{
			if (simpleRegex == null)
			{
				simpleRegex = new Regex(@"^\s*(\w{1})\s*([^\s]+)$", RegexOptions.IgnoreCase | RegexOptions.Multiline);
			}
			if (string.IsNullOrEmpty(input))
				return null;
			
			switch (format)
			{
				case InputDataFormat.Svn:

					if ((input.StartsWith("Checked out revision")) || (input.StartsWith("At revision")))
					{
						string[] tokens = input.Split(' ', '\t');
						string rev = tokens[tokens.Length - 1];
						repository.Revision.Number = int.Parse(rev.Substring(0, rev.Length -1));
						return null;
					}
					else
					{
						string[] lines = input.Split(new string[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
						if (lines.Length > 0)
						{
							List<SvnFileEntry> result = new List<SvnFileEntry>();
							foreach (string line in lines)
							{
								Match m = simpleRegex.Match(line);
								if (m.Success)
								{
									//we have a winner! the input is of the form (A|U|D|G|C|M) filename
									string status = m.Groups[0].Value;
									string filename = m.Groups[2].Value;
									SvnFileEntry entry = new SvnFileEntry(filename);
									entry.Attributes["status"] = status;
									entry.FullPath = ((SvnRepository)repository).ActualLocalPath + filename.Substring(repository.Path.Length).Replace('/', Path.DirectorySeparatorChar);
									result.Add(entry);
								}
							}
							return result;
						}
						return null;
					}

				case InputDataFormat.SvnXml:
					//this should parse an Xml Node (<entry ... />)
					return null;

				default:
					throw new InvalidOperationException();
			}
		}
	}
}
