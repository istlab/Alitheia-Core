using System;
using System.Collections.Generic;
using System.Text;

namespace Metrics.Plugins
{
	/// <summary>
	/// Defines the interface for a Plugin Host, a class that will control the operation
	/// of one or more plugins.
	/// </summary>
	public interface IPluginHost
	{
		/// <summary>
		/// Gets an integer indicating the number of plugins that are currently active
		/// </summary>
		int RunningPlugins
		{
			get;
		}

		/// <summary>
		/// Gets an <see cref="List{IPlugin}"/> containing all the plugins hosted by the host
		/// </summary>
		List<IPlugin> Plugins
		{
			get;
		}

		/// <summary>
		/// Allows an Plugin to register itself in the plugin host.
		/// </summary>
		/// <param name="plugin">The Plugin to be registered.</param>
		void Register(IPlugin plugin);

		/// <summary>
		/// Pauses the operation of all the plugins.
		/// </summary>
		void PauseAllPlugins();

		/// <summary>
		/// Signals all the registered plugins to start operating.
		/// </summary>
		void StartAllPlugins();

		/// <summary>
		/// Signals all the registered plugins to stop operating.
		/// </summary>
		void StopAllPlugins();

		/// <summary>
		/// Checks if a plugin can report an event.
		/// </summary>
		/// <param name="plugin">The <see cref="IPlugin"/> that requests permission to report.</param>
		/// <returns>A <see cref="Boolean"/> value indicating whether the plugin can report.</returns>
		bool PermitReport(IPlugin plugin);

		/// <summary>
		/// Loads a plugin dynamically and registers it with the <see cref="IPluginHost"/>.
		/// </summary>
		/// <param name="fileName">The file containing the plugin to be loaded.</param>
		void LoadPlugin(string fileName);
	}
}
