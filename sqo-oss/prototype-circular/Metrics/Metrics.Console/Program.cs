using System;
using System.Collections.Generic;
using System.Text;
using Metrics.Common;

namespace Metrics.Console
{
	class Program
	{
		static void Main(string[] args)
		{
			SvnRepository repos = new SvnRepository("http://svn.mysql.com/svnpublic/connector-j/trunk/connector-j", @"/home/circular/projects/Metrics/bin/test", null); //@"C:\svn\test\"
			repos.Checkout(new Revision("HEAD"));

			foreach (FileEntry entry in repos.Revision.Files)
			{
				System.Console.WriteLine(entry.Name);
			}
			System.Console.Read();

			Diff diff = repos.Diff(new Revision(6292));
			foreach (FileEntry entry in diff)
			{
				System.Console.WriteLine(entry.Name);
			}
			System.Console.Read();

			CommitLog log = repos.GetLog(new Revision(6200), new Revision("HEAD"));

			foreach (CommitLogEntry entry in log)
			{
				System.Console.WriteLine(string.Format("{0}\t{1}\n{2}\n", entry.Date, entry.Author, entry.Comment));
			}
		}
	}
}
