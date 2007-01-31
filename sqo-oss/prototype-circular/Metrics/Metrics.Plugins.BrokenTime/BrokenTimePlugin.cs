using System;
using System.Collections.Generic;
using System.Text;
using Metrics.Plugins.Logging;
using Metrics.Common;

namespace Metrics.Plugins.BrokenTime
{
	/// <summary>
	/// 
	/// </summary>
	[MetricPlugin]
	class BrokenTimePlugin : PluginBase, IPlugin
	{
		#region Private variables

		Dictionary<Revision, TimeSpan> brokenRevisions; 
		//will hold a list of the revisions that were broken
		//as well as the time it took to commit a revision that repairs each issue
		//there may be overlapping periods, when the project is broken for different reasons
		//e.g. on different platforms, so we need to be able to discern between them

		#endregion

		#region IPlugin Members

		public void Pause()
		{
			throw new Exception("The method or operation is not implemented.");
		}

		public void Resume()
		{
			throw new Exception("The method or operation is not implemented.");
		}

		public void Start()
		{
			throw new Exception("The method or operation is not implemented.");
		}

		public void Stop()
		{
			throw new Exception("The method or operation is not implemented.");
		}

		public void AttachLogger(ILogger logger)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		public void DetachLogger(ILogger logger)
		{
			throw new Exception("The method or operation is not implemented.");
		}

		#endregion

		#region Private Methods

		#endregion
	}
}
