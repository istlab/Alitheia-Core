using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Xml.Serialization;
using Metrics.Common;
using Metrics.Plugins.Logging;
using System.IO;

namespace Metrics.Plugins
{
	/// <summary>
	/// PluginBase provides the base class that all the plugins must inherit from.
	/// </summary>
	/// <remarks>
	/// A plugin that is not statically referenced by another application and is meant to be
	/// loaded dynamically must be attributed with the <see cref="MetricPluginAttribute"/>
	/// attribute, otherwise a <see cref="UnsupportedPluginException"/> will be thrown.
	/// </remarks>
	public class PluginBase
	{
		#region Protected variables

		/// <summary>
		/// The plugin's name.
		/// </summary>
		protected string name;
		/// <summary>
		/// The plugin's description.
		/// </summary>
		protected string description;
		/// <summary>
		/// The plugin's version.
		/// </summary>
		protected Version version;
		/// <summary>
		/// The plugin's <see cref="PluginState"/>
		/// </summary>
		protected PluginState state;
		/// <summary>
		/// Indicates whether the plugin is enabled.
		/// </summary>
		protected bool enabled;
		/// <summary>
		/// Contains the path of the directory where the plugin can store its settings.
		/// </summary>
		protected string settingsPath = String.Empty;
		/// <summary>
		/// Holds the configuration settings of the plugin
		/// </summary>
		protected PluginSettings settings;
		/// <summary>
		/// The percentage of work already completed by the plugin.
		/// </summary>
		protected int percent = 0;
		/// <summary>
		/// Holds the loggers where the plugin will be logging its messages and associates each logger
		/// with the level of verbosity that will determine which messages will be logged.
		/// </summary>
		protected Dictionary<ILogger, LogLevel> loggers;
		/// <summary>
		/// A queue logger holding a queue of messages that the plugin was not permitted to log at a
		/// specific moment and which will be logged when the <see cref="IPluginHost"/> hosting this
		/// plugin will permit it.
		/// </summary>
		protected QueueLogger eventQueue;
		/// <summary>
		/// The repository the plugin is working on
		/// </summary>
		protected Repository repository;
		/// <summary>
		/// A reference to the <see cref="IPluginHost"/> to which the plugin is attached.
		/// </summary>
		protected IPluginHost host = null;

		#endregion

		#region Public Properties

		/// <summary>
		/// Gets a string containing the plugin's name.
		/// </summary>
		public string Name
		{
			get { return name; }
		}

		/// <summary>
		/// Gets a string containing a description for the plugin.
		/// </summary>
		public string Description
		{
			get { return description; }
		}

		/// <summary>
		/// Gets the plugin's version.
		/// </summary>
		public Version Version
		{
			get { return version; }
		}

		/// <summary>
		/// Gets or sets a <see cref="Boolean"/> value indicating if the plugin is enabled.
		/// </summary>
		public bool Enabled
		{
			get { return enabled; }
			set { enabled = value; }
		}

		/// <summary>
		/// Gets or sets the <see cref="Repository"/> the plugin is working on.
		/// </summary>
		public Repository Repository
		{
			get { return repository; }
			set { repository = value; }
		}

		/// <summary>
		/// Sets the path of the directory where the plugin can store its settings.
		/// </summary>
		public string SettingsPath
		{
			set
			{
				settingsPath = value;
				LoadSettings();
			}
		}

		/// <summary>
		/// Gets the plugin's settings collection
		/// </summary>
		public PluginSettings Settings
		{
			get { return settings; }
		}

		/// <summary>
		/// Gets an integer value indicating the percentage of process that has been completed.
		/// </summary>
		public int Percent
		{
			get { return percent; }
		}

		/// <summary>
		/// Gets a <see cref="PluginState"/> value indicating the plugin's current state.
		/// </summary>
		public PluginState State
		{
			get { return state; }
		}

		/// <summary>
		/// Sets the <see cref="IPluginHost"/> that is hosting the plugin. Can only be called
		/// by an <see cref="IPluginHost"/> object.
		/// </summary>
		public IPluginHost Host
		{
			set { host = value; }
		}

		#endregion

		#region Public Events

		/// <summary>
		/// Occurs when the internal state of the plugin changes
		/// </summary>
		public event EventHandler StateChanged;

		/// <summary>
		/// Occurs when the settings of the plugin are saved
		/// </summary>
		public event EventHandler SettingsChanged;

		#endregion

		#region Protected Methods

		/// <summary>
		/// Creates a new instance of the plugin
		/// </summary>
		protected PluginBase()
		{
			name = String.Empty;
			description = String.Empty;
			version = new Version();
			state = PluginState.Stopped;
			enabled = true;
			settingsPath = String.Empty;
			settings = new PluginSettings();
			percent = 0;
			repository = null;
			host = null;
			loggers = new Dictionary<ILogger, LogLevel>();
		}

		/// <summary>
		/// Causes the plugin to report its status or errors to all <see cref="ILogger"/> objects attached to it,
		/// after requesting permission from the <see cref="IPluginHost"/> hosting it. If no host is hosting the
		/// plugin the report is performed anyway.
		/// </summary>
		protected virtual void Report()
		{
			try
			{
				if (host != null)
				{
					if (!host.PermitReport((IPlugin)this))
					{
						return;
					}
				}
				lock (eventQueue)
				{
					while (eventQueue.Count > 0)
					{
						LoggerEntry entry = (LoggerEntry)eventQueue.Dequeue();
						foreach (KeyValuePair<ILogger, LogLevel> kvp in loggers)
						{
							if ((int)entry.EventType <= (int)kvp.Value)
							{
								kvp.Key.LogEventEntry(entry);
							}
						}
					}
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Enqueues an event message to the internal event queue.
		/// </summary>
		/// <param name="eventType">The <see cref="LogLevel"/> of the event.</param>
		/// <param name="msg">The message related to the event.</param>
		protected virtual void AddToReportQueue(LogLevel eventType, string msg)
		{
			try
			{
				if (eventQueue != null)
				{
					lock (eventQueue)
					{
						eventQueue.Enqueue(new LoggerEntry(eventType, DateTime.Now, name + ": " + msg));
					}
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Enqueues a <see cref="LoggerEntry"/> to the internal event queue.
		/// </summary>
		/// <param name="entry">The entry to enqueue.</param>
		protected virtual void AddToReportQueue(LoggerEntry entry)
		{
			try
			{
				if (eventQueue != null)
				{
					lock (eventQueue)
					{
						eventQueue.Enqueue(entry);
					}
				}
			}
			catch
			{ }
		}

		/// <summary>
		/// Raises the StateChanged event
		/// </summary>
		protected virtual void OnStateChanged()
		{
			if (StateChanged != null)
				StateChanged(this, EventArgs.Empty);
		}

		/// <summary>
		/// Raises the SettingsChanged event
		/// </summary>
		protected virtual void OnSettingsChanged()
		{
			if (SettingsChanged != null)
				SettingsChanged(this, EventArgs.Empty);
		}

		/// <summary>
		/// Determines the plugin's path and determines the location where its settings will be stored.
		/// </summary>
		protected virtual void BuildSettingsPath()
		{
			if(string.IsNullOrEmpty(name))
				throw new InvalidOperationException("The plugin's name has not been initialized");

			string path = string.Empty;
			try
			{
				path = System.IO.Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) + System.IO.Path.DirectorySeparatorChar;
			}
			catch
			{
				//it didn't work, so assume it's the current drectory
				path = Environment.CommandLine.Trim('"', '\'');
			}
			try
			{
				path = System.IO.Path.GetDirectoryName(path);
			}
			catch
			{
				//if every attempt fails assume it's the working directory
				path = "." + System.IO.Path.DirectorySeparatorChar;
			}

			settingsPath = System.IO.Path.Combine(path, name) + ".xml";
		}

		/// <summary>
		/// Allows plugins to define default settings
		/// </summary>
		protected virtual void LoadDefaultSettings()
		{
			settings.Clear();
		}

		#endregion

		#region Public Methods

		/// <summary>
		/// Loads the plugin's settings. Must be overriden in the derived classes if their
		/// settings are stored on a persistent storage and XML serialization is not enough.
		/// </summary>
		public virtual void LoadSettings()
		{
			try
			{
				if (string.IsNullOrEmpty(settingsPath))
					BuildSettingsPath();

				if (!File.Exists(settingsPath))
				{
					//perhaps the file does not exist - probably because it has not been
					//created yet. In this case just let the class get default values.
					LoadDefaultSettings();
					return;
				}
				Stream ReadStream = File.Open(settingsPath, FileMode.Open);
				XmlSerializer serializer = new XmlSerializer(typeof(PluginSettings));
				settings = (PluginSettings)serializer.Deserialize(ReadStream);
				ReadStream.Close();
			}
			catch
			{ }
		}

		/// <summary>
		/// Saves the plugin's settings. Must be overriden in the derived classes if their
		/// settings are stored on a persistent storage and XML serialization is not enough.
		/// </summary>
		public virtual void SaveSettings()
		{
			if(string.IsNullOrEmpty(settingsPath))
				BuildSettingsPath();

			try
			{
				Stream WriteStream = File.Open(settingsPath, FileMode.Create);
				XmlSerializer serializer = new XmlSerializer(typeof(PluginSettings));
				serializer.Serialize(WriteStream, settings);
				WriteStream.Close();

				//notify the world that the settings have changed
				OnSettingsChanged();
			}
			catch(Exception e)
			{
				throw new ApplicationException("The plugin failed to save its settings: " + e.Message, e);
			}
		}


		#endregion

	}
}
