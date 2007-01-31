using System;
using System.Collections.Generic;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// LoggerBase serves as a base class for classes implementing ILogger. It provides basic functionality
	/// such as formatting of log messages.
	/// </summary>
	public abstract class LoggerBase
	{
		private string eventSourceName;
		private bool rememberLastEntry;
		private string dateFormat;
		//holds a mapping between entry types and their descriptions
		private static Dictionary<LogLevel, string> entryTypeDescriptions;

		#region Constructor and initialization

		/// <summary>
		/// Creates a new instance of the class
		/// </summary>
		/// <param name="eventSourceName">The name of the event source</param>
		public LoggerBase(string eventSourceName)
		{
			lock (entryTypeDescriptions)
			{
				if (entryTypeDescriptions == null)
				{
					Initialize();
				}
			}
			this.eventSourceName = eventSourceName;
			this.rememberLastEntry = false;
			this.dateFormat = "r";
		}

		/// <summary>
		/// Initializes the mapping between log entry types and their descriptions
		/// </summary>
		private static void Initialize()
		{
			entryTypeDescriptions = new Dictionary<LogLevel, string>();
			entryTypeDescriptions.Add(LogLevel.Debug, "DEBUG");
			entryTypeDescriptions.Add(LogLevel.Info, "INFO");
			entryTypeDescriptions.Add(LogLevel.Warning, "WARNING");
			entryTypeDescriptions.Add(LogLevel.Error, "ERROR");
		}

		#endregion

		#region Public properties

		/// <summary>
		/// Gets the name of the event source.
		/// </summary>
		public string EventSourceName
		{
			get { return eventSourceName; }
		}

		/// <summary>
		/// Gets or sets a Boolean value indicating whether the logger must remember the last
		/// entry.
		/// </summary>
		public bool RememberLastEntry
		{
			get { return rememberLastEntry; }
			set { rememberLastEntry = value; }
		}

		#endregion

		#region Protected methods and properties

		/// <summary>
		/// Gets the description of an <see cref="EntryLoggerType"/>
		/// </summary>
		/// <param name="type">The type whose description is requested</param>
		/// <returns>The description of <paramref name="type"/></returns>
		protected static string Description(LogLevel type)
		{
			return entryTypeDescriptions[type];
		}

		/// <summary>
		/// Formats a message that must be logged
		/// </summary>
		/// <param name="date">The <see cref="DateTime"/> when the event being logged occured</param>
		/// <param name="msg">The message to be logged</param>
		/// <param name="type">The type of the event being logged</param>
		/// <returns>The formatted message</returns>
		protected string FormatMessage(DateTime date, string msg, LogLevel type)
		{

			if (eventSourceName == String.Empty)
			{

				return string.Format("[{0}][{1}]\t{2}", date.ToString(dateFormat), Description(type), msg);
			}
			else
			{
				return string.Format("[{0}][{1}]\t{2}: {3}", date.ToString(dateFormat), Description(type), eventSourceName, msg);
			}
		}

		/// <summary>
		/// Gets or sets the format used for the date and time of events
		/// </summary>
		public string DateFormat
		{
			get { return dateFormat; }
			protected set { dateFormat = value; }
		}

		#endregion
	}
}
