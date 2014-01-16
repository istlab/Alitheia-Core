package eu.sqooss.impl.service.webadmin.servlets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.*;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;

/**
 * This servlet is responsible for plugin properties.
 * It provides an editor form and actions to create, update or remove plugin properties
 */
@SuppressWarnings("serial")
public class PropertiesServlet extends AbstractWebadminServlet {

	private static final String paramPluginHash = "hash";

	private static final String ROOT_PATH = "/properties";
	private static final String PAGE_EDITOR = ROOT_PATH;
	private static final String ACTION_PROPERTIES = PAGE_EDITOR + "/action";

	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_EDITOR, "/properties.vm").build();

	private enum PROPERTIES_ACTIONS {
		CREATE, UPDATE, REMOVE, INVALID;
	}

	private final PluginAdmin sobjPA;

	public PropertiesServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjPA = core.getPluginAdmin();
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}


	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {
		// Switch over the URI
		switch(req.getRequestURI()) {
		case PAGE_EDITOR:
			return PageEditor(req, vc);
		case ACTION_PROPERTIES:
			// Convert the action argument to the enum and switch on it
			PROPERTIES_ACTIONS action;
			try {
				action = Enum.valueOf(PROPERTIES_ACTIONS.class, req.getParameter("action").toUpperCase());
			}
			catch(IllegalArgumentException | NullPointerException e) {
				action = PROPERTIES_ACTIONS.INVALID;
			}
			switch (action) {
			case CREATE:
				return createProperty(req, vc);
			case UPDATE:
				return updateProperty(req, vc);
			case REMOVE:
				return removeProperty(req, vc);
			default:
				return makeErrorMsg(vc, "No or invalid action ");
			}
		default:
			throw new PageNotFoundException();
		}
	}

	private Template PageEditor(HttpServletRequest req, VelocityContext vc) {
		// Load template
		Template t = loadTemplate(templates.get(PAGE_EDITOR));

		// Put the correct plugin
		PluginInfo plugin = getPluginFromHash(req);
		if (plugin == null)
			return makeErrorMsg(vc,
					"No plugin hash given or plugin does not exist");
		vc.put("plugin", plugin);

		// Put the correct property
		PluginConfiguration property = getPluginConfiguration(plugin, req);
		vc.put("property", property);

		// Put whether the editor is in update mode
		boolean update = property != null;
		vc.put("update", update);

		// Put all possible configuration types
		vc.put("types", ConfigurationType.values());

		return t;
	}

	private Template createProperty(HttpServletRequest req, VelocityContext vc) {
		try {
			// Get the correct plugin
			PluginInfo plugin = getPluginFromHash(req);
			if (plugin == null)
				return makeErrorMsg(vc,
						"No plugin hash given or plugin does not exist");
			
			// Get parameters from the request
			String name, descr, type, value;
			if (req.getParameter("reqParPropName") == null)
			    name = "";
			else
			    name = req.getParameter("reqParPropName");
			
			if (req.getParameter("reqParPropDescr") == null) 
			    descr = "";
			else
			    descr = req.getParameter("reqParPropDescr");
			
			if (req.getParameter("reqParPropType") == null)
			    type = "";
			else
			    type = req.getParameter("reqParPropType");
			
			if (req.getParameter("reqParPropValue") == null)
			    value = "";
			else
			    value = req.getParameter("reqParPropValue");
			
			// Try to add property
			if (plugin.addConfigEntry(
					sobjDB,
					name,
					descr,
					type,
					value)) {

				// Update the Plug-in Admin's information
				sobjPA.pluginUpdated(sobjPA.getPlugin(plugin));

				// Reload the PluginInfo object
				plugin = getPluginFromHash(req);

				return makeSuccessMsg(vc, "Property successfully created");
			} else
				return makeErrorMsg(vc, "Property creation has failed! Check log for details.");
		}
		catch (Exception ex) {
			return makeErrorMsg(vc, "An error occurred while creating the property: " + ex.getMessage());
		}
	}

	private Template updateProperty(HttpServletRequest req, VelocityContext vc) {
		try {
			// Get the correct plugin
			PluginInfo plugin = getPluginFromHash(req);
			if (plugin == null)
				return makeErrorMsg(vc,
						"No plugin hash given or plugin does not exist");

			// Get the correct property
			PluginConfiguration property = getPluginConfiguration(plugin, req);

			// Get parameters from the request
			String value;
	         if (req.getParameter("reqParPropValue") == null)
                value = "";
            else
                value = req.getParameter("reqParPropValue");
			
			// Try to update the property
			if (plugin.updateConfigEntry(
					sobjDB,
					property.getName(),
					value)) {

				// Update the Plug-in Admin's information
				sobjPA.pluginUpdated(sobjPA.getPlugin(plugin));

				// Reload the PluginInfo object
				plugin = getPluginFromHash(req);

				return makeSuccessMsg(vc, "Property successfully updated");
			} else
				return makeErrorMsg(vc, "Property update has failed! Check log for details.");
		}
		catch (Exception ex) {
			return makeErrorMsg(vc, "An error occurred while updating the property: " + ex.getMessage());
		}
	}

	private Template removeProperty(HttpServletRequest req, VelocityContext vc) {
		// FIXME: Plugin property removal does not work
		// This functionality did not work in the old webadmin.
		// We didn't manage to fix it
		return makeErrorMsg(vc, "Plugin property removal is not implemented");

		//        try {
		//            // Get the correct plugin
		//            PluginInfo plugin = getPluginFromHash(req);
		//            if (plugin == null)
		//                return makeErrorMsg(vc,
		//                        "No plugin hash given or plugin does not exist");
		//
		//            // Get the correct property
		//            PluginConfiguration property = getProperty(plugin, req);
		//
		//            // Try to remove the property
		//            if (plugin.removeConfigEntry(
		//                    sobjDB,
		//                    property.getName(),
		//                    property.getType())) {
		//
		//                // Update the Plug-in Admin's information
		//                sobjPA.pluginUpdated(sobjPA.getPlugin(plugin));
		//
		//                // Reload the PluginInfo object
		//                plugin = getPluginFromHash(req);
		//
		//                return makeSuccessMsg(vc, "Property successfully removed");
		//            }
		//            else {
		//                return makeErrorMsg(vc, "Property removal has failed! Check log for details.");
		//            }
		//        }
		//        catch (Exception ex) {
		//            return makeErrorMsg(vc, "An error occurred while removing the property: " + ex.getMessage());
		//        }
	}

	private PluginInfo getPluginFromHash(HttpServletRequest req) {
		if (req.getParameter(paramPluginHash) == null
				|| req.getParameter(paramPluginHash).isEmpty())
			return null;
		String hash = req.getParameter(paramPluginHash);
		PluginInfo plugin = sobjPA.getPluginInfo(hash);
		return plugin;
	}

	private PluginConfiguration getPluginConfiguration(PluginInfo plugin, HttpServletRequest req) {
		try {
			return getPluginConfigurationFromId(plugin, Long.parseLong(req.getParameter("propertyId")));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private PluginConfiguration getPluginConfigurationFromId(PluginInfo plugin, Long id) {
		for (PluginConfiguration property : plugin.getConfiguration())
			if (property.getId() == id)
				return property;
		return null;
	}
}
