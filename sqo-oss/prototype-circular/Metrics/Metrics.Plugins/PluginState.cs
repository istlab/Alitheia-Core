using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Plugins
{
	/// <summary>
	/// PluginStatus specifies the different states in which a <see cref="IPlugin"/> object
	/// can be at any given time.
	/// </summary>
	public enum PluginState
	{
		/// <summary>
		/// Indicates that the plugin has paused its process
		/// </summary>
		Paused,
		/// <summary>
		/// Indicates that the plugin has been started and is active
		/// </summary>
		Running,
		/// <summary>
		/// Indicates that the plugin has just been initialized or stopped and is inactive.
		/// </summary>
		Stopped
	}
}
