using System;
using System.Collections.Generic;
using System.Reflection;
using Metrics.Plugins;
using Metrics.Plugins.Logging;

namespace Metrics.UI
{
	/// <summary>
	/// PluginController is a class that implements <see cref="IPluginHost"/>. It acts as a
	/// host for all the plugins that are loaded by the Metrics UI. The PluginController
	/// allows all the plugins to log their events without restrictions. It also features
	/// an event that allows the UI to be updated when the state of a plugin changes.
	/// </summary>
	public class PluginController : IPluginHost
	{
		#region Private variables

		private List<IPlugin> plugins;
		private int runningPlugins;
		private Dictionary<ILogger, LogLevel> loggers;

		#endregion

		#region Constructor

		/// <summary>
		/// Constructs a new instance of the <see cref="PluginController"/> class.
		/// </summary>
		public PluginController()
		{
			plugins = new List<IPlugin>();
			runningPlugins = 0;
			loggers = new Dictionary<ILogger, LogLevel>();
			FileLogger log = new FileLogger("Metrics.UI.log", true, false, "Metrics.UI");
			if(log!=null)
			{
				loggers.Add(log, LogLevel.Debug);
			}
		}

		#endregion

		#region IPluginHost Members

		/// <summary>
		/// Gets the number of Running Plugins.
		/// </summary>
		public int RunningPlugins
		{
			get
			{
				return runningPlugins;
			}
		}

		/// <summary>
		/// Gets a reference to the list of <see cref="IPlugin"/> objects currently loaded
		/// and managed by this instance of <see cref="PluginController"/>.
		/// </summary>
		public List<IPlugin> Plugins
		{
			get { return plugins; }
		}
        
		/// <summary>
		/// Stops all the loaded Plugins.
		/// </summary>
		public void StopAllPlugins()
		{
			foreach(IPlugin plugin in plugins)
			{
				try
				{
					(plugin).Stop();
					runningPlugins--;
				}
				catch(Exception e)
				{
					foreach(ILogger logger in loggers.Keys)
					{
						if(logger != null)
						{
							logger.LogWarning("StopAllPlugins failed to stop " + plugin.Name + ": " + e.ToString());
						}
					}
					continue;
				}
			}
		}

		/// <summary>
		/// Registers a <see cref="PluginBase"/> with this instance of <see cref="PluginController"/>.
		/// </summary>
		/// <param name="plugin">The Plugin to register.</param>
		public void Register(IPlugin plugin)
		{
			if(!IsPluginRegistered(plugin))
			{
				plugins.Add(plugin);
				plugin.Host = this;
				foreach(ILogger logger in loggers.Keys)
				{
					if(logger!=null)
					{
						plugin.AttachLogger(logger);
					}
				}
				((IPlugin)plugin).StateChanged+=new EventHandler(PluginController_StateChanged);
			}
			else
			{
				throw new ArgumentException("This Plugin is already loaded and registered.");
			}
		}

		/// <summary>
		/// Starts all the loaded Plugins.
		/// </summary>
		public void StartAllPlugins()
		{
			foreach(IPlugin plugin in plugins)
			{
				try
				{
					plugin.Start();
					runningPlugins++;
				}
				catch(Exception e)
				{
					foreach(ILogger logger in loggers.Keys)
					{
						if(logger!=null)
						{
							logger.LogWarning("StartAllPlugins failed to start " + plugin.Name + ": " + e.ToString());
						}
					}
					continue;
				}
			}
		}

		/// <summary>
		/// Manages reporting permissions for plugins.
		/// </summary>
		/// <param name="plugin">The plugin that wishes to report an event.</param>
		/// <returns>True if the Plugin is permitted to report, otherwise false.</returns>
		public bool PermitReport(IPlugin plugin)
		{
			// TODO:  Add PluginController.PermitReport implementation
			return true;
		}

		/// <summary>
		/// Pauses all the running plugins.
		/// </summary>
		public void PauseAllPlugins()
		{
			foreach(IPlugin plugin in plugins)
			{
				try
				{
					if(plugin.State == PluginState.Running)
					{
						plugin.Pause();
						runningPlugins --;
					}
				}
				catch(Exception e)
				{
					foreach(ILogger logger in loggers.Keys)
					{
						if(logger!=null)
						{
							logger.LogWarning("PauseAllPlugins failed to pause " + plugin.Name + ": " + e.ToString());
						}
					}
					continue;
				}
			}
		}

		/// <summary>
		/// Loads a plugin from disk and registers it with this instance of <see cref="PluginController"/>.
		/// </summary>
		/// <param name="fileName">The path of the file containing the Plugin to load.</param>
		/// <exception cref="CWUnsupportedPluginException">
		/// Thrown if the specified file does not contain a type that derives from 
		/// <see cref="PluginBase"/> and implements <see cref="IPlugin"/>.
		/// </exception>
		public void LoadPlugin(string fileName)
		{
			bool pluginFound = false;
			Assembly a = Assembly.LoadFile(fileName);
			System.Type []types = a.GetTypes();
			foreach(Type t in types)
			{
				if(t.GetInterface("IPlugin")!=null)
				{
					if (t.GetCustomAttributes(typeof(MetricPluginAttribute), false).Length > 0)
					{
						IPlugin plugin = (IPlugin)Activator.CreateInstance(t);
						Register(plugin);
						pluginFound = true;
					}
				}
			}
			if(!pluginFound)
			{
				throw new ArgumentException(fileName + " does not contain a valid plugin.");
			}
		}

		#endregion

		#region Public Events

		/// <summary>
		/// Occurs whenever the <see cref="PluginBase.State"/> of a loaded Plugin changes.
		/// </summary>
		public event EventHandler PluginStateChanged;

		#endregion

		#region Private methods

		/// <summary>
		/// Checks whether a <see cref="PluginBase">Plugin</see> is already registered with
		/// the controller.
		/// </summary>
		/// <param name="plugin">The plugin to check</param>
		/// <returns>True if the Plugin is already registered with the controller, false otherwise.</returns>
		private bool IsPluginRegistered(IPlugin plugin)
		{
			System.Type pt = plugin.GetType();
			foreach(IPlugin loadedPlugin in plugins)
			{
				if(pt == loadedPlugin.GetType())
				{
					return true;
				}
			}
			return false;
		}

		private void PluginController_StateChanged(object sender, EventArgs e)
		{
			OnPluginStateChanged(sender, e);
		}

		private void OnPluginStateChanged(object sender, EventArgs e)
		{
			if(PluginStateChanged != null)
			{
				PluginStateChanged(sender, e);
			}
		}

		#endregion

		#region Public methods

		/// <summary>
		/// Attaches a new logger to which the host must log all the events.
		/// </summary>
		/// <param name="logger">The logger to attach to the host</param>
		/// <param name="level">The level of significance for the messages to be logged</param>
		public void AttachLogger(ILogger logger, LogLevel level)
		{
			if(logger == null)
			{
				throw new ArgumentNullException("logger");
			}
			try
			{
				loggers.Add(logger, level);
			}
			catch
			{}
		}

		#endregion
	}
}
