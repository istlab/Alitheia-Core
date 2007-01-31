using System;
using System.Collections.Generic;
using System.Text;
using Metrics.Plugins.Logging;

namespace Metrics.Plugins
{
	/// <summary>
	/// Defines the interface of all the plugins that will be hosted in an <see cref="IPluginHost"/>
	/// and will be used for processing data.
	/// </summary>
	public interface IPlugin
	{
		/// <summary>
		/// Gets the name of the plugin
		/// </summary>
		string Name
		{
			get;
		}

		/// <summary>
		/// Gets the description of the plugin.
		/// </summary>
		string Description
		{
			get;
		}

		/// <summary>
		/// Gets the plugin's version.
		/// </summary>
		Version Version
		{
			get;
		}

		/// <summary>
		/// Provides access to a collection of settings that can be used to configure the plugin's operation
		/// </summary>
		PluginSettings Settings
		{
			get;
		}

		/// <summary>
		/// Gets a <see cref="PluginState"/> value indicating the plugin's current state.
		/// </summary>
		PluginState State
		{
			get;
		}

		/// <summary>
		/// Sets the <see cref="IPluginHost"/> that is hosting the plugin. Can only be called
		/// by an <see cref="IPluginHost"/> object.
		/// </summary>
		IPluginHost Host
		{
			set;
		}

		/// <summary>
		/// Pauses the plugin's process temporarily.
		/// </summary>
		void Pause();

		/// <summary>
		/// Resumes the plugin's process if it has been paused. 
		/// </summary>
		void Resume();

		/// <summary>
		/// Starts the plugin's process.
		/// </summary>
		void Start();

		/// <summary>
		/// Stops the plugin's process.
		/// </summary>
		void Stop();

		/// <summary>
		/// Loads the last saved configuration of the plugin's settings
		/// </summary>
		void LoadSettings();

		/// <summary>
		/// Saves the plugin's settings in a persistent form
		/// </summary>
		void SaveSettings();

		/// <summary>
		/// Attaches an <see cref="ILogger"/> to the plugin, so that messages can be logged to it
		/// </summary>
		/// <param name="logger">The logger where the plugin can log its messages</param>
		/// <remarks>A plugin implementing<see cref="IPlugin"/> must be able to handle multiple loggers simultaneously</remarks>
		void AttachLogger(ILogger logger);

		/// <summary>
		/// Detaches an <see cref="ILogger"/> from the plugin, so that the plugin will stop logging messages to it
		/// </summary>
		/// <param name="logger">The logger to be removed from the list of log message targets</param>
		void DetachLogger(ILogger logger);

		/// <summary>
		/// Raised when the <see cref="PluginState"/> of the plugin changes.
		/// </summary>
		event EventHandler StateChanged;

		/// <summary>
		/// Raised when the configuration settings of the plugin are being saved
		/// </summary>
		event EventHandler SettingsChanged;
	}
}
