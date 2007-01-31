using System;
using System.Collections.Generic;
using System.IO;
using System.Diagnostics;
using System.Net;
using System.Text;
using System.Xml;
using System.Collections;

namespace Metrics.Common
{
	/// <summary>
	/// Represents a Subversion repository and performs all basic actions on it.
	/// </summary>
	/// <remarks>In order to operate it requires that there is a working subversion client installed and available
	/// in the PATH environment variable.</remarks>
	public class SvnRepository : Repository
	{
		private ProcessStartInfo psi;
		private Process process;
		private Dictionary<ArgumentType, string> arguments;
		private State state;
		StringBuilder response;
		private string actualLocalPath;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="path"></param>
		/// <param name="localpath"></param>
		/// <param name="credentials"></param>
		public SvnRepository(string path, string localpath, NetworkCredential credentials)
			: base(path, localpath, credentials)
		{
			//TODO: move this to a configuration setting
			executablePath = @"/usr/bin/svn"; //@"C:\Program Files\Subversion\bin\svn.exe";
			Revision = new Revision("HEAD");
			InitializeArguments();
			psi = new ProcessStartInfo();
			InitializeProcessStartInfo();
			process = new Process();
			process.StartInfo = psi;
			process.ErrorDataReceived += new DataReceivedEventHandler(process_ErrorDataReceived);
			//this was used to allow ASYNC handling of process output. A sync model is used now.
			//process.OutputDataReceived += new DataReceivedEventHandler(process_OutputDataReceived);
			state = State.Idle;
			response = new StringBuilder();
		}

		internal string ActualLocalPath
		{
			get { 
				if(string.IsNullOrEmpty(actualLocalPath))
					actualLocalPath = LocalPath + System.IO.Path.GetFileNameWithoutExtension(Path); 
				return actualLocalPath;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="rev"></param>
		public override void Checkout(Revision rev)
		{
			try
			{
				lock (sync)
				{
					state = State.Checkout;
					Revision = rev;
					arguments[ArgumentType.Revision] = Revision.Description;
					arguments[ArgumentType.Command] = "checkout";
					psi.Arguments = BuildArguments();
					//change the psi's parameters
					process.Start();
					process.BeginErrorReadLine();
					ReadProcessOutput(process.StandardOutput);
					process.WaitForExit();
					try //Mono bug workaround
					{
						process.CancelErrorRead();
					}
					catch {}
					process.Close();
				}
			}
			finally
			{
				state = State.Idle;
			}
			RefreshFileList(true);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="rev"></param>
		public override void Update(Revision rev)
		{
			try
			{
				lock (sync)
				{
					state = State.Update;
					Revision = rev;
					arguments[ArgumentType.Revision] = Revision.Description;
					arguments[ArgumentType.Command] = "update";
					psi.Arguments = BuildArguments();
					//change the psi's parameters
					process.Start();
					process.BeginErrorReadLine();
					ReadProcessOutput(process.StandardOutput);
					process.WaitForExit();
					try //Mono bug workaround
					{
						process.CancelErrorRead();
					}
					catch {}
					process.Close();
				}
			}
			finally
			{
				state = State.Idle;
			}
			RefreshFileList(true);
		}

		/// <summary>
		/// returns a Diff containing a list of all the files changed between the base revision and rev
		/// </summary>
		/// <param name="rev"></param>
		/// <returns></returns>
		public override Diff Diff(Revision rev)
		{
			return Diff(Revision, rev);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public override Diff Diff(Revision start, Revision end)
		{
			Diff result = new Diff();

			try
			{
				lock (sync)
				{
					state = State.Diff;
					arguments[ArgumentType.Command] = "diff";
					arguments[ArgumentType.Revision] = start.Description;
					arguments[ArgumentType.Revision2] = end.Description;
					arguments[ArgumentType.Generic] = "--summarize";
					psi.Arguments = BuildArguments();
					//change the psi's parameters
					process.Start();
					process.BeginErrorReadLine();
					ReadProcessOutput(process.StandardOutput);
					process.WaitForExit();
					try //Mono bug workaround
					{
						process.CancelErrorRead();
					}
					catch { }
					process.Close();
				}
			}
			finally
			{
				state = State.Idle;
			}

			List<SvnFileEntry> modified = SvnFileEntry.Parse(response.ToString(), this, FileEntry.InputDataFormat.Svn);
			if (modified != null)
			{
				foreach (FileEntry entry in modified)
				{
					result.Add(entry);
				}
			}
			return result;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="start"></param>
		/// <param name="end"></param>
		/// <returns></returns>
		public override CommitLog GetLog(Revision start, Revision end)
		{
			CommitLog result = new CommitLog(start, end);

			try
			{
				lock (sync)
				{
					state = State.Log;
					arguments[ArgumentType.Command] = "log";
					arguments[ArgumentType.Revision] = start.Description;
					arguments[ArgumentType.Revision2] = end.Description;
					arguments[ArgumentType.Generic] = "--xml";
					psi.Arguments = BuildArguments();
					//change the psi's parameters
					process.Start();
					process.BeginErrorReadLine();
					ReadProcessOutput(process.StandardOutput);
					process.WaitForExit();
					try //Mono bug workaround
					{
						process.CancelErrorRead();
					}
					catch { }
					process.Close();
				}
			}
			finally
			{
				state = State.Idle;
			}

			ParseInput(response.ToString(), FileEntry.InputDataFormat.SvnLogXml, result.Entries);
			return result;
		}

		#region Private methods

		private void InitializeProcessStartInfo()
		{
			psi.CreateNoWindow = true;
			psi.FileName = executablePath;
			psi.UseShellExecute = false;
			psi.RedirectStandardError = true;
			psi.RedirectStandardInput = true;
			psi.RedirectStandardOutput = true;
			psi.WorkingDirectory = this.LocalPath;
		}

		private void InitializeArguments()
		{
			arguments = new Dictionary<ArgumentType, string>();
			if (credentials != null)
			{
				string authinfo = "";
				if (!string.IsNullOrEmpty(credentials.UserName))
				{
					authinfo = string.Format(" --username {0}", credentials.UserName);
				}
				if (!string.IsNullOrEmpty(credentials.Password))
				{
					authinfo += string.Format(" --password {0}", credentials.Password);
				}
				arguments[ArgumentType.AuthInfo] = authinfo;
			}
			arguments[ArgumentType.Revision] = Revision.Description;
		}

		private string BuildArguments()
		{
			StringBuilder args = new StringBuilder();
			// the arguments must be passed to the external svn client in a specific order
			args.Append(arguments[ArgumentType.Command] + " ");
			args.Append(Path + " ");
			string arg = string.Empty;
			if (arguments.TryGetValue(ArgumentType.Revision, out arg))
			{
				args.Append(string.Format("-r {0}", arg));
			}
			arg = string.Empty;
			if (arguments.TryGetValue(ArgumentType.Revision2, out arg))
			{
				args.Append(string.Format(":{0}", arg));
			}
			arg = string.Empty;
			if (arguments.TryGetValue(ArgumentType.Generic, out arg))
			{
				args.Append(string.Format(" {0}", arg));
			}
			arg = string.Empty;
			if (arguments.TryGetValue(ArgumentType.ModuleName, out arg))
			{
				args.Append(string.Format(" {0}", arg));
			}
			//foreach (KeyValuePair<ArgumentType, string> kvp in arguments)
			//{
			//    switch (kvp.Key)
			//    {	
			//    }
			//}
			return args.ToString();
		}

		private void process_OutputDataReceived(object sender, DataReceivedEventArgs e)
		{
			Console.WriteLine(e.Data);
			//parse the line and print it to the console. 
			////This used to create FileEntry objects and add the result in a FileEntry
			if (state == State.List)
			{
				//put the data into the string builder, it will be used to generate the file list
				response.Append(e.Data);
			}
		}

		private void process_ErrorDataReceived(object sender, DataReceivedEventArgs e)
		{
			Console.WriteLine(e.Data);
			//TODO: perhaps set the object in an error state
		}

		private void ReadProcessOutput(StreamReader stream)
		{
			response.Length = 0;
			//read the output of the process into the stringbuilder
			while (!stream.EndOfStream)
			{
				string line = stream.ReadLine();
				response.AppendLine(line);
				switch (state)
				{
					case State.Checkout:
					case State.Update:
						Console.WriteLine(line);
						break;

					default:
						break;
				}
			}
		}

		private void RefreshFileList(bool local)
		{
			try
			{
				lock (sync)
				{
					state = State.List;
					response.Length = 0;
					arguments[ArgumentType.Revision] = Revision.Description;
					arguments[ArgumentType.Command] = "list --xml -R";
					arguments.Remove(ArgumentType.Revision2);
					arguments.Remove(ArgumentType.Generic);
					psi.Arguments = BuildArguments();
					//change the psi's parameters
					process.Start();
					process.BeginErrorReadLine();
					ReadProcessOutput(process.StandardOutput);
					process.WaitForExit();
					try //Mono bug workaround
					{
						process.CancelErrorRead();
					}
					catch {}
					process.Close();

					//parse the input xml
					ParseRepositoryListInfo(response.ToString());
					response.Length = 0;
				}
			}
			finally
			{
				state = State.Idle;
			}
		}

		/// <summary>
		/// Parses the xml produced by svn list --xml and loads the current object's revision with the list of files
		/// </summary>
		/// <param name="xml"></param>
		private void ParseRepositoryListInfo(string xml)
		{
			List<SvnFileEntry> entries = new List<SvnFileEntry>();
			ParseInput(xml, FileEntry.InputDataFormat.SvnXml, entries);
			Revision.Files.Clear();
			Revision.Files.AddRange(entries.ToArray()); //not so elegant
		}

		/// <summary>
		/// Parses the xml returned by svn list --xml
		/// 
		/// (a typical example)
		/// </summary>
		/// <param name="input"></param>
		/// <param name="format"></param>
		/// <returns></returns>
		private void ParseInput(string input, FileEntry.InputDataFormat format, IList result)
		{
			if (string.IsNullOrEmpty(input))
				return;

			switch (format)
			{
				case FileEntry.InputDataFormat.Svn:
					List<SvnFileEntry> entries = SvnFileEntry.Parse(input, this, FileEntry.InputDataFormat.Svn);
					if (entries != null)
					{
						foreach(SvnFileEntry entry in entries)
							result.Add(entry);
					}
					break;

				case FileEntry.InputDataFormat.SvnXml:

					XmlDocument document = new XmlDocument();
					try
					{
						document.LoadXml(input);
					}
					catch
					{
						break;
					}
					foreach (XmlNode list in document["lists"].ChildNodes)
					{
						//todo: add handling of multiple lists (haven't seen one in any repository)
						foreach (XmlNode entry in list.ChildNodes)
						{
							SvnFileEntry sfe = ParseSvnEntryXml(entry);
							//TODO: Parse the date
							result.Add(sfe);
						}
					}
					break;

				case FileEntry.InputDataFormat.SvnLogXml:
					
					XmlDocument xmldoc = new XmlDocument();
					try
					{
						xmldoc.LoadXml(input);
					}
					catch
					{
						break;
					}
					foreach (XmlNode entry in xmldoc["log"].ChildNodes)
					{
						CommitLogEntry logEntry = ParseSvnLogEntryXml(entry);
						result.Add(logEntry);
					}
					break;

				default:
					throw new InvalidOperationException();
			}
		}

		/// <summary>
		/// Parses an Xml representation of a file entry found in an svn repository, as returned
		/// by svn list --xml
		///	<lists>
		///		<list path=".">
		///			<entry kind="dir">
		///				<name>jalopy</name>
		///				<commit revision="309778">
		///					<author>username</author>
		///					<date>2003-03-18T20:25:32.000000Z</date>
		///				</commit>
		///			</entry>
		///		</list>
		/// </lists>
		/// </summary>
		/// <param name="entry"></param>
		/// <returns></returns>
		private static SvnFileEntry ParseSvnEntryXml(XmlNode entry)
		{
			string kind = entry.Attributes["kind"].Value;
			string name = entry["name"].InnerText;
			int size = (entry["size"] != null) ? int.Parse(entry["size"].InnerText) : 0;
			string revision = entry["commit"].Attributes["revision"].InnerText;
			string author = entry["commit"]["author"].InnerText;
			string date = entry["commit"]["date"].InnerText;
			SvnFileEntry sfe = new SvnFileEntry(name);
			sfe.Size = size;
			sfe.Revision = revision;
			sfe.Kind = FileEntry.ParseEntryKind(kind);
			sfe.Attributes["author"] = author;
			sfe.Attributes["date"] = date;
			return sfe;
		}

		/// <summary>
		/// Parses an SVN log entry in Xml format
		/// <logentry revision="140">
		///    <author>circular</author>
		///    <date>2007-01-11T03:07:40.522594Z</date>
		///    <msg>Initial import of source code for the Metrics project</msg>
		/// </logentry>
		/// </summary>
		/// <param name="entry">The Xml Node containing the entry</param>
		/// <returns></returns>
		private static CommitLogEntry ParseSvnLogEntryXml(XmlNode entry)
		{
			string revision = entry.Attributes["revision"].InnerText;
			string author = entry["author"].InnerText;
			DateTime date = DateTime.Parse(entry["date"].InnerText);
			string msg = entry["msg"].InnerText;
			CommitLogEntry result = new CommitLogEntry(author, msg, date, revision);
			return result;
		}

		#endregion

		#region Private Enumerations

		/// <summary>
		/// 
		/// </summary>
		private enum ArgumentType
		{
			Command,
			Revision,
			Revision2,
			ModuleName,
			AuthInfo,
			Generic
		}

		/// <summary>
		/// 
		/// </summary>
		private enum State
		{
			Idle,
			Checkout,
			Update,
			Diff,
			List,
			Log
		}

		#endregion

	}
}
