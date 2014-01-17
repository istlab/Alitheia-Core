package eu.sqooss.impl.service.webadmin.servlets;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.LazyInitializationException;

import com.google.common.collect.ImmutableMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

/**
 * This Servlet is responsible for listing, adding and removing plugins.
 */
@SuppressWarnings("serial")
public class PluginsServlet extends AbstractWebadminServlet {

	private static final String ROOT_PATH = "/plugins";

	private static final String PAGE_PLUGINSLIST = ROOT_PATH;
	private static final String PAGE_PLUGIN = ROOT_PATH + "/plugin";
	private static final String ACTION_PLUGIN = ROOT_PATH + "/plugin/action";

	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_PLUGINSLIST, "/plugins/pluginlist.vm")
			.put(PAGE_PLUGIN, "/plugins/plugin.vm").build();

	private final PluginAdmin sobjPA;
	private final MetricActivator compMA;

	public PluginsServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjPA = core.getPluginAdmin();
		compMA = core.getMetricActivator();
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}

	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {
		// Delegate the request to the correct method
		switch (req.getRequestURI()) {
		case PAGE_PLUGINSLIST:
			return PagePluginsList(req, vc);
		case PAGE_PLUGIN:
			return PagePlugin(req, vc);
		case ACTION_PLUGIN:
			// Convert the action argument to the enum and switch on it
			PLUGIN_ACTIONS action;
			try {
				action = Enum.valueOf(PLUGIN_ACTIONS.class, req.getParameter("action").toUpperCase());
			}
			catch(IllegalArgumentException | NullPointerException e) {
				action = PLUGIN_ACTIONS.INVALID;
			}
			switch (action) {
			case INSTALL:
				return installPlugin(req, vc);
			case UNINSTALL:
				return uninstallPlugin(req, vc);
			case SYNCHRONIZE:
				return synchronizePlugin(req, vc);
			default:
				return makeErrorMsg(vc, "No or invalid action ");
			}
		default:
			throw new PageNotFoundException();
		}
	}

	private Template PagePluginsList(HttpServletRequest req, VelocityContext vc) {
		// Load the template
		Template t = loadTemplate(templates.get(PAGE_PLUGINSLIST));

		boolean showProperties = req.getParameter("showProperties") != null
				&& req.getParameter("showProperties").equals("true");
		boolean showActivators = req.getParameter("showActivators") != null
				&& req.getParameter("showActivators").equals("true");

		// Whether to show properties and activators
		vc.put("showProperties", showProperties);
		vc.put("showActivators", showActivators);

		// The list of plugins
		Collection<PluginInfo> pluginList = sobjPA.listPlugins();
		if (pluginList == null) {
			pluginList = new ArrayList<PluginInfo>();
			getLogger().warn(
					"Could not get plugin information from PluginAdmin");
		}

		// FIXME: The Set<PluginConfiguration> in a PluginInfo initializes not properly
		// and gives LazyInitializationExceptions when invoking methods on this object.
		// The set works properly when adding a new PluginConfiguration via the webadmin
		// form. (why?). Now the PluginConfiguration are loaded on a safe way to prevent
		// the displaying of errors, but this means that the PluginConfiguration are
		// not showed before the Set is initialized on a proper way.

		// Properties for each plugin (failsafe)
		Map<String, Set<PluginConfiguration>> configurations = new HashMap<String, Set<PluginConfiguration>>();
		for (PluginInfo p : pluginList) {
			try {
				Set<PluginConfiguration> c = p.getConfiguration();
				if (c != null && !c.isEmpty()) {
					configurations.put(p.getHashcode(), c);
				} else {
					configurations.put(p.getHashcode(), new HashSet<PluginConfiguration>());
				}
			} catch (LazyInitializationException e) {
				configurations.put(p.getHashcode(), new HashSet<PluginConfiguration>());
				getLogger().warn("LazyInitializationException while loading plugin configurations: " + e.getMessage());
			}
		}
		vc.put("configurations", configurations);

		// Put plugin list
		vc.put("pluginList", pluginList);

		return t;
	}

	private Template PagePlugin(HttpServletRequest req, VelocityContext vc) {
		// Load the template
		Template t = loadTemplate(templates.get(PAGE_PLUGIN));

		// Get the correct plugin
		PluginInfo plugin = getPluginFromHash(req);
		if (plugin == null)
			return makeErrorMsg(vc,
					"No plugin hash given or plugin does not exist");


		// Provide the variables to the template
		vc.put("plugin", plugin);
		if (plugin.isInstalled()) {
			// Add metrics
			vc.put("metrics", sobjPA.getPlugin(plugin).getAllSupportedMetrics());

			// FIXME: The Set<PluginConfiguration> in a PluginInfo initializes not properly
			// and gives LazyInitializationExceptions when invoking methods on this object.
			// The set works properly when adding a new PluginConfiguration via the webadmin
			// form. (why?). Now the PluginConfiguration are loaded on a safe way to prevent
			// the displaying of errors, but this means that the PluginConfiguration are
			// not showed before the Set is initialized on a proper way.

			// Add properties (failsafe loading)
			Set<PluginConfiguration> configurations = new HashSet<PluginConfiguration>();
			try {
				configurations = plugin.getConfiguration();
				if (configurations == null || configurations.isEmpty()) {
					configurations = new HashSet<PluginConfiguration>();
				}
			} catch (LazyInitializationException e) {
				configurations = new HashSet<PluginConfiguration>();
				getLogger().warn("LazyInitializationException while loading plugin configurations: " + e.getMessage());
			}
			vc.put("configPropList", configurations);
		}

		return t;
	}

	private PluginInfo getPluginFromHash(HttpServletRequest req) {
		if (req.getParameter("hash") == null
				|| req.getParameter("hash").isEmpty())
			return null;
		String hash = req.getParameter("hash");
		PluginInfo plugin = sobjPA.getPluginInfo(hash);
		return plugin;
	}

	/**
	 * Valid actions for a plugin. These are the valid values for the "action"
	 * parameter (when lowercase)
	 */
	private enum PLUGIN_ACTIONS {
		INSTALL, UNINSTALL, SYNCHRONIZE, INVALID;
	}

	private Template installPlugin(HttpServletRequest req, VelocityContext vc) {
		PluginInfo plugin = getPluginFromHash(req);
		if (plugin == null)
			return makeErrorMsg(vc,
					"No plugin hash given or plugin does not exist");

		if (plugin.isInstalled())
			return makeErrorMsg(vc, "Plugin is already installed");

		if (sobjPA.installPlugin(plugin.getHashcode()) == false) {
			getLogger().warn("Could not install plugin " + plugin.toString());
			return makeErrorMsg(vc,
					"Plug-in can not be installed! Check log for details.");
		}
		// Persist the DB changes
		else {
			sobjPA.pluginUpdated(sobjPA.getPlugin(plugin));
			return makeSuccessMsg(vc, "Plugin successfully installed",
					PAGE_PLUGIN + "?hash=" + plugin.getHashcode());
		}
	}

	private Template uninstallPlugin(HttpServletRequest req, VelocityContext vc) {
		PluginInfo plugin = getPluginFromHash(req);
		if (plugin == null)
			return makeErrorMsg(vc,
					"No plugin hash given or plugin does not exist");

		if (!plugin.isInstalled())
			return makeErrorMsg(vc, "Plugin isn't installed");

		if (sobjPA.uninstallPlugin(plugin.getHashcode()) == false) {
			getLogger().warn("Could not uninstall plugin " + plugin.toString());
			return makeErrorMsg(vc,
					"Plug-in can not be uninstalled! Check log for details.");
		} else
			return makeSuccessMsg(vc,
					"A job was scheduled to remove the plug-in", PAGE_PLUGIN
					+ "?hash=" + plugin.getHashcode());
	}

	private Template synchronizePlugin(HttpServletRequest req,
			VelocityContext vc) {
		PluginInfo plugin = getPluginFromHash(req);
		if (plugin == null)
			return makeErrorMsg(vc,
					"No plugin hash given or plugin does not exist");

		if (!plugin.isInstalled())
			return makeErrorMsg(vc, "Plugin isn't installed");

		compMA.syncMetrics(sobjPA.getPlugin(plugin));
		return makeSuccessMsg(vc,
				"Jobs are scheduled to run the plugin over all projects");
	}
}
