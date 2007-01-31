using System;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// EventLoggerEntry is a class that holds information related to an event that can or
	/// must be logged by a class implementing the <see cref="ILogger"/> interface. It is
	/// particularly useful for classes implementing the <see cref="IPlugin"/> interface,
	/// or whenever a class needs to buffer some events before writing them to a log.
	/// </summary>
	[Serializable]
	public class LoggerEntry
	{
		/// <summary>
		/// The <see cref="LogLevel"/> type of the event.
		/// </summary>
		public readonly LogLevel EventType;
		/// <summary>
		/// The date and time when the event occured.
		/// </summary>
		public readonly DateTime EventDate;
		/// <summary>
		/// The message of the event.
		/// </summary>
		public readonly string EventMessage;

		/// <summary>
		/// Constructs a new instance of the <see cref="LoggerEntry"/> class with the
		/// data provided.
		/// </summary>
		/// <param name="eventType">The type of the event.</param>
		/// <param name="eventDate">The date and time the event took place.</param>
		/// <param name="eventMsg">The message related to the event.</param>
		public LoggerEntry(LogLevel eventType, DateTime eventDate, string eventMsg)
		{
			EventType = eventType;
			EventDate = eventDate;
			EventMessage = eventMsg;
		}
	}
}
