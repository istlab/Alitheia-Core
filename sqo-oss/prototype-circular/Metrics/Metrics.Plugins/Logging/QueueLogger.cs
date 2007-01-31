using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// QueueLogger is a simple thread-safe Event Logger that buffers events into a
	/// Queue from which the clients can later on dequeue the <see cref="LoggerEntry"/>
	/// objects. It implements <see cref="ILogger"/> and offers some extra functionality
	/// such as setting an upper limit in the number of events that the queue will hold and
	/// clearing the queue of events.
	/// </summary>
	public class QueueLogger : LoggerBase, ILogger
	{
		#region Private variables

		private string lastEntry;
		private int maxSize;
		private Queue<LoggerEntry> events;

		#endregion

		#region Constructors

		/// <summary>
		/// Creates a new instance of <see cref="QueueLogger"/> with default values.
		/// </summary>
		public QueueLogger()
			: this(string.Empty, 100)
		{ }

		/// <summary>
		/// Creates a new instance of <see cref="QueueLogger"/> with the given Event
		/// Source name.
		/// </summary>
		/// <param name="eventSourceName">The Event Source name.</param>
		public QueueLogger(string eventSourceName)
			: this(eventSourceName, 100)
		{ }

		/// <summary>
		/// Creates a new instance of <see cref="QueueLogger"/> with the given capacity.
		/// </summary>
		/// <param name="maxSize">The capacity of the Event Queue.</param>
		/// <exception cref="ArgumentOutOfRangeException">Thrown if a negative value is supplied for maxSize.</exception>
		public QueueLogger(int maxSize)
			: this(string.Empty, maxSize)
		{ }

		/// <summary>
		/// Creates a new instance of <see cref="QueueLogger"/> with the given Event
		/// Source Name and Queue capacity.
		/// </summary>
		/// <param name="eventSourceName">The Event Source name.</param>
		/// <param name="maxSize">The capacity of the Event Queue.</param>
		/// <exception cref="ArgumentOutOfRangeException">Thrown if a negative value is supplied for maxSize.</exception>
		public QueueLogger(string eventSourceName, int maxSize) : base(eventSourceName)
		{
			if (maxSize <= 0)
			{
				throw new ArgumentOutOfRangeException("MaxSize", "MaxSize must be a positive value.");
			}
			lastEntry = String.Empty;
			this.maxSize = maxSize;
			events = new Queue<LoggerEntry>(maxSize);
		}

		#endregion

		#region Public Properties

		/// <summary>
		/// Gets or sets the maximum number of <see cref="EventLoggerEntry"/> objects that
		/// the <see cref="QueueLogger"/> will hold.
		/// </summary>
		/// <exception cref="ArgumentOutOfRangeException">Thrown if a negative value is supplied for maxSize.</exception>
		public int MaxSize
		{
			get { return maxSize; }
			set
			{
				if (value <= 0)
				{
					throw new ArgumentOutOfRangeException("MaxSize", "MaxSize must be a positive value.");
				}
				else
				{
					maxSize = value;
				}
			}
		}

		/// <summary>
		/// Gets the number of events logged in the <see cref="QueueLogger"/>.
		/// </summary>
		public int Count
		{
			get { return events.Count; }
		}

		/// <summary>
		/// Provides access to the internal Queue used by the <see cref="QueueLogger"/>.
		/// </summary>
		public Queue<LoggerEntry> EventQueue
		{
			get { return events; }
		}

		#endregion

		#region Public Methods

		/// <summary>
		/// Enqueues an <see cref="LoggerEntry"/> object in the <see cref="QueueLogger"/>.
		/// </summary>
		/// <param name="entry">The <see cref="LoggerEntry"/> to be logged.</param>
		public void Enqueue(LoggerEntry entry)
		{
			if (events.Count == maxSize)
			{
				events.Dequeue();
			}
			events.Enqueue(entry);
		}

		/// <summary>
		/// Dequeues the first <see cref="EventLoggerEntry"/> logged by the <see cref="QueueLogger"/>.
		/// </summary>
		/// <returns>An <see cref="EventLoggerEntry"/> containing the first event logged.</returns>
		/// <exception cref="InvalidOperationException">Thrown if the method is called when QueueLogger contains no entries.</exception>
		public LoggerEntry Dequeue()
		{
			if (events.Count == 0)
			{
				throw new InvalidOperationException("The QueueEventLogger contains no events.");
			}
			else
			{
				return events.Dequeue();
			}
		}

		#endregion

		#region ILogger Members

		/// <summary>
		/// Logs an event of type Debug
		/// </summary>
		/// <param name="msg">The message related to the event to be logged.</param>
		public void LogDebug(string msg)
		{
			try
			{
				LoggerEntry entry = new LoggerEntry(LogLevel.Debug, DateTime.Now, FormatMessage(msg));
				EnqueueEntry(entry);
				if (RememberLastEntry)
				{
					lastEntry = entry.EventMessage;
				}
			}
			catch
			{ }
		}

		

		/// <summary>
		/// Logs an event of type Info
		/// </summary>
		/// <param name="msg">The message related to the event to be logged.</param>
		public void LogInfo(string msg)
		{
			try
			{
				LoggerEntry entry = new LoggerEntry(LogLevel.Info, DateTime.Now, FormatMessage(msg));
				EnqueueEntry(entry);
				if (RememberLastEntry)
				{
					lastEntry = entry.EventMessage;
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Logs an event of type Warning
		/// </summary>
		/// <param name="msg">The message related to the event to be logged.</param>
		public void LogWarning(string msg)
		{
			try
			{
				LoggerEntry entry = new LoggerEntry(LogLevel.Warning, DateTime.Now, FormatMessage(msg));
				EnqueueEntry(entry);
				if (RememberLastEntry)
				{
					lastEntry = entry.EventMessage;
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Logs an event of type Error
		/// </summary>
		/// <param name="msg">The message related to the event to be logged.</param>
		public void LogError(string msg)
		{
			try
			{
				LoggerEntry entry = new LoggerEntry(LogLevel.Error, DateTime.Now, FormatMessage(msg));
				EnqueueEntry(entry);
				if (RememberLastEntry)
				{
					lastEntry = entry.EventMessage;
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Creates a log according to the type of the entry's event
		/// </summary>
		/// <param name="entry">The <see cref="LoggerEntry"/> to log.</param>
		public void LogEventEntry(LoggerEntry entry)
		{
			try
			{
				EnqueueEntry(entry);
				if (RememberLastEntry)
				{
					lastEntry = entry.EventMessage;
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Gets the message of the last Event Log entry.
		/// </summary>
		public string LastEntry
		{
			get
			{
				lock (events)
				{
					if (events.Count > 0)
					{
						LoggerEntry entry = events.Peek();
						return entry.EventMessage;
					}
					else
					{
						return String.Empty;
					}
				}
			}
		}

		#endregion

		#region Private methods

		/// <summary>
		/// Formats a message that must be logged
		/// </summary>
		/// <param name="msg">The message to be logged</param>
		/// <returns>The formatted message</returns>
		private string FormatMessage(string msg)
		{
			if (EventSourceName != String.Empty)
			{
				return String.Format("[{0}]: {1}", EventSourceName, msg);
			}
			else
			{
				return msg;
			}
		}

		/// <summary>
		/// Adds an entry to the queue in a thread safe manner
		/// </summary>
		/// <param name="entry">The entry to add to the queue</param>
		private void EnqueueEntry(LoggerEntry entry)
		{
			lock (events)
			{
				if (events.Count > maxSize)
				{
					events.Dequeue();
				}
				events.Enqueue(entry);
			}
		}

		#endregion
	}

}
