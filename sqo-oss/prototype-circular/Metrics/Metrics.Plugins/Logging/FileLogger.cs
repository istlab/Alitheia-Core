using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.IO;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// FileLogger is a simple thread-safe Event Logger that uses a flat text file as
	/// the log entries' repository. It implements the <see cref="ILogger"/> interface and
	/// provides some extra functionality, such as Backup and automatic log truncation.
	/// </summary>
	public class FileLogger : LoggerBase, ILogger, IDisposable
	{
		#region Private variables

		private string fileName;
		private bool autoTruncate;
		private bool shared;
		private object sync;
		private StreamWriter stream;
		private string lastEntry;
		private static int maxSize = 5242880;
		private static int minEntries = 1000;

		#endregion

		#region Constructors

		/// <summary>
		/// The FileEventLogger constructor. Initializes the File Streams and the thread
		/// safety / synchronization mechanism. If the appropriate flag has been set and
		/// the size of the log file has exceeded a certain limit it performs truncation.
		/// </summary>
		/// <param name="fileName">The full path of the file that will be used as a log.</param>
		/// <param name="autoTruncate">Indicates whether the log file will be truncated automatically.</param>
		/// <param name="Shared">Indicates whether the log file will be opened in shared mode, thus allowing two or more processes to use it.</param>
		/// <param name="eventSourceName">The description of the event source application.</param>
		/// <exception cref="ArgumentException">It is thrown if the given fileName is null or empty.</exception>
		/// <exception cref="Exception">It is thrown if the Logger fails to open the given file in append mode.</exception>
		/// <remarks>
		/// If the <b>Shared</b> parameter is set to True then there may occur corruption of
		/// th log file, since each instance of the class will hold it's own copy of the file
		/// stream and each instance will overwrite the other's log. It must be used with great
		/// care, unless only the first instance is used for logging and the others are only
		/// reading the log file.
		/// </remarks>
		public FileLogger(string fileName, bool autoTruncate, bool shared, string eventSourceName)
			: base(eventSourceName)
		{
			if ((fileName == null) || (fileName.Length == 0))
			{
				throw new ArgumentException("The FileName cannot be empty.");
			}
			sync = new object();
			this.autoTruncate = autoTruncate;
			this.shared = shared;
			lastEntry = string.Empty;
			this.fileName = fileName;
			PreprocessLog(fileName);
			if (autoTruncate)
			{
				Truncate();
			}
			try
			{
				if (shared)
				{
					stream = new StreamWriter(File.Open(fileName, FileMode.Append, FileAccess.Write, FileShare.ReadWrite));
				}
				else
				{
					stream = File.AppendText(fileName);
				}
			}
			catch (Exception e)
			{
				throw new Exception("FileLogger could not open file '" + fileName + "' for Appending: " + e.Message);
			}
		}

		/// <summary>
		/// The FileEventLogger constructor. Initializes the File Streams and the thread
		/// safety / synchronization mechanism. If the appropriate flag has been set and
		/// the size of the log file has exceeded a certain limit it performs truncation.
		/// </summary>
		/// <param name="fileName">The full path of the file that will be used as a log.</param>
		/// <param name="autoTruncate">Indicates whether the log file will be truncated automatically.</param>
		/// <param name="eventSourceName">The description of the event source application.</param>
		/// <exception cref="ArgumentException">It is thrown if the given filename is null or empty.</exception>
		/// <exception cref="Exception">It is thrown if the Logger fails to open the given file in append mode.</exception>
		public FileLogger(string fileName, bool autoTruncate, string eventSourceName)
			: this(fileName, autoTruncate, false, eventSourceName)
		{ }

		/// <summary>
		/// The FileEventLogger constructor. Initializes the File Streams and the thread
		/// safety / synchronization mechanism. This version assumes autoTruncate is off.
		/// </summary>
		/// <param name="fileName">The full path of the file that will be used as a log.</param>
		/// <param name="eventSourceName">The description of the event source application.</param>
		/// <exception cref="ArgumentException">It is thrown if the given fileName is null or empty.</exception>
		/// <exception cref="Exception">It is thrown if the Logger fails to open the given file in append mode.</exception>
		public FileLogger(string fileName, string eventSourceName)
			: this(fileName, false, false, eventSourceName)
		{ }

		/// <summary>
		/// The FileEventLogger constructor. Initializes the File Streams and the thread
		/// safety / synchronization mechanism. This version assumes autoTruncate is off
		/// and the eventSourceName is an empty string.
		/// </summary>
		/// <param name="fileName">The full path of the file that will be used as a log.</param>
		/// <exception cref="ArgumentException">It is thrown if the given fileName is null or empty.</exception>
		/// <exception cref="Exception">It is thrown if the Logger fails to open the given file in append mode.</exception>
		public FileLogger(string fileName)
			: this(fileName, false, false, string.Empty)
		{ }

		#endregion

		#region ILogger Members

		/// <summary>
		/// Creates a Log Entry of type Debug in the log file in a thread-safe manner.
		/// </summary>
		/// <param name="msg">
		/// The log Message
		/// </param>
		public void LogDebug(string msg)
		{
			lock (sync)
			{
				try
				{
					lastEntry = FormatMessage(DateTime.Now, msg, LogLevel.Debug);
					stream.WriteLine(lastEntry);
					stream.Flush();
				}
				catch
				{ }
			}
		}

		/// <summary>
		/// Creates a Log Entry of type Information in the log file in a thread-safe manner.
		/// </summary>
		/// <param name="msg">
		/// The log Message
		/// </param>
		public void LogInfo(string msg)
		{
			lock (sync)
			{
				try
				{
					lastEntry = FormatMessage(DateTime.Now, msg, LogLevel.Info);
					stream.WriteLine(lastEntry);
					stream.Flush();
				}
				catch
				{ }
			}
		}

		/// <summary>
		/// Creates a Log Entry of type Warning in the log file in a thread-safe manner.
		/// </summary>
		/// <param name="msg">
		/// The log Message
		/// </param>
		public void LogWarning(string msg)
		{
			lock (sync)
			{
				try
				{
					lastEntry = FormatMessage(DateTime.Now, msg, LogLevel.Warning);
					stream.WriteLine(lastEntry);
					stream.Flush();
				}
				catch
				{ }
			}
		}

		/// <summary>
		/// Creates a Log Entry of type Error in the log file in a thread-safe manner.
		/// </summary>
		/// <param name="msg">
		/// The log Message
		/// </param>
		public void LogError(string msg)
		{
			lock (sync)
			{
				try
				{
					lastEntry = FormatMessage(DateTime.Now, msg, LogLevel.Error);
					stream.WriteLine(lastEntry);
					stream.Flush();
				}
				catch
				{ }
			}
		}

		/// <summary>
		/// Creates a Log Entry in the log file according to the type of the entry's event.
		/// </summary>
		/// <param name="entry">The <see cref="EventLoggerEntry"/> to log.</param>
		public void LogEventEntry(LoggerEntry entry)
		{
			lock (sync)
			{
				try
				{
					lastEntry = FormatMessage(entry.EventDate, entry.EventMessage, entry.EventType);
					stream.WriteLine(lastEntry);
					stream.Flush();
				}
				catch
				{ }
			}
		}

		/// <summary>
		/// Gets the message of the last Event Log entry.
		/// </summary>
		public string LastEntry
		{
			get
			{
				//no locking required
				return lastEntry;
			}
		}

		#endregion

		#region IDisposable Members

		/// <summary>
		/// Cleans up resources used by the File Event Logger, closes any open File Streams
		/// and releases all synchronization mechanisms.
		/// </summary>
		public void Dispose()
		{
			try
			{
				stream.Close();
			}
			catch
			{ }
		}

		#endregion

		#region Extended functionality methods

		/// <summary>
		/// Backs up the current version of the log file to another file with the same name
		/// and a .bak extension. Not thread-safe.
		/// </summary>
		public void Backup()
		{
			try
			{
				File.Copy(fileName, fileName + ".bak", true);
			}
			catch
			{ }
		}

		/// <summary>
		/// Restores the log file from another file with the same name and a .bak extension.
		/// Not thread-safe.
		/// </summary>
		public void Restore()
		{
			try
			{
				File.Copy(fileName + ".bak", fileName, true);
			}
			catch
			{ }
		}

		/// <summary>
		/// Performs the truncation of the Log File, according to the values defined for
		/// the maximum file size and the number of entries to save.
		/// </summary>
		private void Truncate()
		{
			if (File.Exists(fileName))
			{
				FileInfo f = new FileInfo(fileName);
				if (f.Length > maxSize)
				{
					try
					{
						Backup();
						StreamReader input = new StreamReader(fileName);
						string text = input.ReadToEnd();
						input.Close();
						input = null;
						int entries = 0, offset = text.Length;
						while ((offset = text.LastIndexOf("\n[", offset - 1, offset)) != -1)
						{
							entries++;
							if (entries == minEntries)
							{
								break;
							}
						}
						StreamWriter output = new StreamWriter(fileName, false);
						output.Write(text.Substring(offset + 1));
						output.Close();
						output = null;
					}
					catch
					{
						Restore();
					}
					finally
					{
						GC.Collect();
					}
				}
			}
		}

		/// <summary>
		/// Allows the logger and its child classes to preprocess the log files in order to perform tasks such as 
		/// truncation, rolling, backup, etc.
		/// </summary>
		protected virtual void PreprocessLog(string fileName)
		{

		}

		/// <summary>
		/// Gets/sets the Automatic Truncate property, which determines whether the log
		/// file will be truncated down to a certain number of entries when it grows up
		/// to a certain size.
		/// </summary>
		public bool AutoTruncate
		{
			get { return autoTruncate; }
			set { autoTruncate = value; }
		}

		#endregion
	}
}
