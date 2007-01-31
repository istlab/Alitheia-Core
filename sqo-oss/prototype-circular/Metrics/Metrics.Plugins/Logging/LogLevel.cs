using System;

namespace Metrics.Plugins.Logging
{
	/// <summary>
	/// LogLevel is an enumeration that defines the different error severity levels that
	/// can be logged using an <see cref="ILogger"/> implementation. This way it is easier
	/// to define what kinds of events will be stored in an Event Log.
	/// </summary>
	public enum LogLevel
	{
		/// <summary>
		/// Causes the <see cref="ILogger"/> to log Events of type Debug.
		/// </summary>
		Debug = 0,
		/// <summary>
		/// Causes the <see cref="ILogger"/> to log Events of every type except Debug.
		/// </summary>
		Info = 1,
		/// <summary>
		/// Causes the <see cref="ILogger"/> to log only events of type Warning and Error.
		/// </summary>
		Warning = 2,
		/// <summary>
		/// Causes the <see cref="ILogger"/> to log only events of type Error.
		/// </summary>
		Error = 3,
		/// <summary>
		/// Causes the <see cref="ILogger"/> to log no Events at all.
		/// </summary>
		Nothing = 4
	}
}
