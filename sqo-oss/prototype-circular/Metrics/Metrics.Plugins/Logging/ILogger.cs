using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// ILogger defines the interface for all classes that can be used to log events to
	/// different repositories.
	/// </summary>
	public interface ILogger
	{
		/// <summary>
		/// Defines the prototype for a method that logs an event of type Debug.
		/// </summary>
		/// <param name="msg"></param>
		void LogDebug(string msg);

		/// <summary>
		/// Defines the prototype for a method that logs an event of type Information.
		/// </summary>
		/// <param name="msg">The message to log.</param>
		void LogInfo(string msg);

		/// <summary>
		/// Defines the prototype for a method that logs an event of type Warning.
		/// </summary>
		/// <param name="msg">The message to log.</param>
		void LogWarning(string msg);

		/// <summary>
		/// Defines the prototype for a method that logs an event of type Error.
		/// </summary>
		/// <param name="msg">The message to log.</param>
		void LogError(string msg);

		/// <summary>
		/// Defines the prototype for a method that logs an event contained in an <see cref="EventLoggerEntry"/> object.
		/// </summary>
		/// <param name="entry">The <see cref="EventLoggerEntry"/> to log.</param>
		void LogEventEntry(LoggerEntry entry);

		/// <summary>
		/// Gets a string description of the event source.
		/// </summary>
		string EventSourceName { get; }

		/// <summary>
		/// Gets the string contained in the last Log Entry.
		/// </summary>
		string LastEntry { get;}

		/// <summary>
		/// Defines whether the Logger must remember the last entry logged.
		/// </summary>
		bool RememberLastEntry { get; set;}
	}
}
