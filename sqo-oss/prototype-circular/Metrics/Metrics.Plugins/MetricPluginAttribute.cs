using System;

namespace Metrics.Plugins
{
	/// <summary>
	/// MetricPluginAttribute is an attribute that must be used on all the plugins that
	/// are not statically referenced by a class implementing <see cref="IPluginHost"/>
	/// and will be dynamically loaded by it.
	/// </summary>
	[AttributeUsage(AttributeTargets.Class)]
	public class MetricPluginAttribute : Attribute
	{

		/// <summary>
		/// Constructs a new instance of the MetricPluginAttribute class.
		/// </summary>
		public MetricPluginAttribute()
			: base()
		{ }
	}
}
