/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.webadmin;

import static eu.sqooss.impl.service.webadmin.HTMLFormBuilder.POST;
import static eu.sqooss.impl.service.webadmin.HTMLFormBuilder.form;
import static eu.sqooss.impl.service.webadmin.HTMLInputBuilder.BUTTON;
import static eu.sqooss.impl.service.webadmin.HTMLInputBuilder.TEXT;
import static eu.sqooss.impl.service.webadmin.HTMLInputBuilder.input;
import static eu.sqooss.impl.service.webadmin.HTMLNodeBuilder.node;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.table;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.tableColumn;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.tableRow;
import static eu.sqooss.impl.service.webadmin.HTMLTextBuilder.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import eu.sqooss.impl.service.webadmin.HTMLTableBuilder.HTMLTableRowBuilder;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import eu.sqooss.service.util.StringUtils;

public class PluginsView extends AbstractView {
	protected static final String SUBMIT = "document.metrics.submit();";

	protected static final String ACT_VAL_UNINSTALL_PLUGIN = "uninstallPlugin";
	protected static final String ACT_VAL_CON_PROP = "confirmProperty";
	protected static final String ACT_VAL_CON_REM_PROP = "removeProperty";
	protected static final String ACT_VAL_SYNC_PLUGIN = "syncPlugin";
	protected static final String REQ_PAR_SHOW_ACTV = "showActivators";
	protected static final String ACT_VAL_INSTALL_PLUGIN = "installPlugin";
	protected static final String REQ_PAR_ACTION = "action";
    protected static final String REQ_PAR_PROP_VALUE = "propertyValue";
	protected static final String REQ_PAR_PROP_DESC = "propertyDescription";
	protected static final String REQ_PAR_PROP_TYPE = "propertyType";
	protected static final String REQ_PAR_PROP_NAME = "propertyName";
	protected static final String REQ_PAR_HASHCODE = "pluginHashcode";
	protected static final String REQ_PAR_SHOW_PROP = "showProperties";
	
	protected static final String ACT_VAL_REQ_ADD_PROP = "createProperty";
	protected static final String ACT_VAL_REQ_UPD_PROP = "updateProperty";

	public PluginsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Renders the various plug-in's views.
     * 
     * @param req the servlet's request object
     * 
     * @return The HTML presentation of the generated view.
     */
    public String render(HttpServletRequest req) {
    	// Indentation spacer
    	long in = 6;

        // Proceed only when at least one plug-in is registered
        if (getPluginAdmin().listPlugins().isEmpty()) {
            return normalFieldset(
                    "All plug-ins",
                    null,
                    new StringBuilder("<span>"
                            + "No plug-ins found!&nbsp;"
                            + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Refresh\""
                            + " onclick=\"javascript:"
                            + "window.location.reload(true);"
                            + "\" />"
                            + "</span>"),
                    in);
        }
        else {
        	// Stores the accumulated error messages
        	StringBuilder e = new StringBuilder();
        	
        	// Request values
        	String reqValAction        = "";
        	String reqValHashcode      = null;
        	String reqValPropName      = null;
        	String reqValPropDescr     = null;
        	String reqValPropType      = null;
        	String reqValPropValue     = null;
        	boolean reqValShowProp     = false;         // Show plug-in properties
        	boolean reqValShowActv     = false;         // Show plug-in activators
        	// Info object of the selected plug-in
        	PluginInfo selPI           = null;

        	
        	// ===============================================================
            // Parse the servlet's request object
            // ===============================================================
            if (req != null) {
                // DEBUG: Dump the servlet's request parameter

                // Retrieve the selected editor's action (if any)
                reqValAction = req.getParameter(REQ_PAR_ACTION);
                if (reqValAction == null) {
                    reqValAction = "";
                }
                // Retrieve the various display flags
                if ("true".equals(req.getParameter(REQ_PAR_SHOW_PROP))) {
                    reqValShowProp = true;
                }
                if ("true".equals(req.getParameter(REQ_PAR_SHOW_ACTV))) {
                    reqValShowActv = true;
                }
                // Retrieve the selected configuration property's values
                if ((ACT_VAL_CON_PROP.equals(reqValAction))
                        || (ACT_VAL_REQ_UPD_PROP.equals(reqValAction))
                        || (ACT_VAL_CON_REM_PROP.equals(reqValAction))) {
                    // Name, description, type and value
                    reqValPropName  = req.getParameter(REQ_PAR_PROP_NAME);
                    reqValPropDescr = req.getParameter(REQ_PAR_PROP_DESC);
                    reqValPropType  = req.getParameter(REQ_PAR_PROP_TYPE);
                    reqValPropValue = req.getParameter(REQ_PAR_PROP_VALUE);
                }
                // Retrieve the selected plug-in's hash code
                reqValHashcode = req.getParameter(REQ_PAR_HASHCODE);
                // Plug-in based actions
                if (reqValHashcode != null) {
                    // =======================================================
                    // Plug-in install request
                    // =======================================================
                    if (ACT_VAL_INSTALL_PLUGIN.equals(reqValAction)) {
                        if (getPluginAdmin().installPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be installed!"
                                    + " Check log for details.");
                        }
                        // Persist the DB changes
                        else {
                            PluginInfo pInfo =
                                getPluginAdmin().getPluginInfo(reqValHashcode);
                            getPluginAdmin().pluginUpdated(getPluginAdmin().getPlugin(pInfo));
                        }
                    }
                    // =======================================================
                    // Plug-in un-install request
                    // =======================================================
                    else if (ACT_VAL_UNINSTALL_PLUGIN.equals(reqValAction)) {
                        if (getPluginAdmin().uninstallPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be uninstalled."
                                    + " Check log for details.");
                        } else {
                            e.append("A job was scheduled to remove the plug-in");
                        }
                    } 
                }
                // Retrieve the selected plug-in's info object
                if (reqValHashcode != null) {
                    selPI = getPluginAdmin().getPluginInfo(reqValHashcode);
                }
                // Plug-in info based actions
                if ((selPI != null) && (selPI.installed)) {
                    // =======================================================
                    // Plug-in synchronize (on all projects) request
                    // =======================================================
                    if (ACT_VAL_SYNC_PLUGIN.equals(reqValAction)) {
                        getMetricActivator().syncMetrics(getPluginAdmin().getPlugin(selPI));
                    }
                    // =======================================================
                    // Plug-in's configuration property removal
                    // =======================================================
                    else if (ACT_VAL_CON_REM_PROP.equals(reqValAction)) {
                        if (selPI.hasConfProp(
                                reqValPropName, reqValPropType)) {
                            try {
                                if (selPI.removeConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropType)) {
                                    // Update the Plug-in Admin's information
                                    getPluginAdmin().pluginUpdated(
                                            getPluginAdmin().getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI = getPluginAdmin().getPluginInfo(
                                            reqValHashcode);
                                }
                                else {
                                    e.append("Property removal"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        else {
                            e.append ("Unknown configuration property!");
                        }
                        // Return to the update view upon error
                        if (e.toString().length() > 0) {
                            reqValAction = ACT_VAL_REQ_UPD_PROP;
                        }
                    }
                    // =======================================================
                    // Plug-in's configuration property creation/update
                    // =======================================================
                    else if (ACT_VAL_CON_PROP.equals(reqValAction)) {
                        // Check for a property update
                        boolean update = selPI.hasConfProp(
                                reqValPropName, reqValPropType);
                        // Update configuration property
                        if (update) {
                            try {
                                if (selPI.updateConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropValue)) {
                                    // Update the Plug-in Admin's information
                                    getPluginAdmin().pluginUpdated(
                                            getPluginAdmin().getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI =
                                        getPluginAdmin().getPluginInfo(reqValHashcode);
                                }
                                else {
                                    e.append("Property update"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        // Create configuration property
                        else {
                            try {
                                if (selPI.addConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropDescr,
                                        reqValPropType,
                                        reqValPropValue)) {
                                    // Update the Plug-in Admin's information
                                    getPluginAdmin().pluginUpdated(
                                            getPluginAdmin().getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI =
                                        getPluginAdmin().getPluginInfo(reqValHashcode);
                                }
                                else {
                                    e.append("Property creation"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        // Return to the create/update view upon error
                        if (e.toString().length() > 0) {
                            if (update) reqValAction = ACT_VAL_REQ_UPD_PROP;
                            else reqValAction = ACT_VAL_REQ_ADD_PROP;
                        }
                    }
                }
            }

            // Stores the assembled HTML content
            StringBuilder b = new StringBuilder("\n");
            if (req != null) {
            	if (DEBUG) {
            		b.append(debugRequest(req));
            	}
            }
            // ===============================================================
            // Create the form
            // ===============================================================
            renderForm(b, e, reqValAction, reqValHashcode, reqValPropName,
					reqValPropDescr, reqValPropType, reqValPropValue,
					reqValShowProp, reqValShowActv, selPI, in);
            
            return b.toString();
        }
    }

	protected void renderForm(StringBuilder b, StringBuilder e,
			String reqValAction, String reqValHashcode, String reqValPropName,
			String reqValPropDescr, String reqValPropType,
			String reqValPropValue, boolean reqValShowProp,
			boolean reqValShowActv, PluginInfo selPI, long in) {
		
		StringBuilder b_internal = new StringBuilder();
		
		// ===============================================================
		// "Create/update configuration property" editor
		// ===============================================================
		if ((selPI != null) && (selPI.installed)
		        && ((ACT_VAL_REQ_ADD_PROP.equals(reqValAction))
		                || (ACT_VAL_REQ_UPD_PROP.equals(reqValAction)))) {
		    renderPluginPropertyEditor(selPI, b_internal, in,
					reqValPropName, reqValPropDescr, reqValPropType,
					reqValPropValue);
		}
		// ===============================================================
		// Plug-in editor
		// ===============================================================
		else if (selPI != null) {
		    in = renderPluginEditor(selPI, in, b_internal);
		}
		// ===============================================================
		// Plug-ins list
		// ===============================================================
		else {
		    renderPluginList(reqValShowProp, reqValShowActv, in,
					b_internal);
		}

		// ===============================================================
		// INPUT FIELDS
		// ===============================================================
		// "Action type" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_ACTION + "\""
		        + " name=\"" + REQ_PAR_ACTION + "\""
		        + " value=\"\" />\n");
		// "Selected plug-in's hash code" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_HASHCODE + "\""
		        + " name=\"" + REQ_PAR_HASHCODE + "\""
		        + " value=\""
		        + ((reqValHashcode != null) ? reqValHashcode : "")
		        + "\" />\n");
		// "Configuration attribute's name" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_PROP_NAME + "\""
		        + " name=\"" + REQ_PAR_PROP_NAME + "\""
		        + " value=\""
		        + ((reqValPropName != null) ? reqValPropName : "")
		        + "\" />\n");
		// "Configuration attribute's description" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_PROP_DESC + "\""
		        + " name=\"" + REQ_PAR_PROP_DESC + "\""
		        + " value=\""
		        + ((reqValPropDescr != null) ? reqValPropDescr : "")
		        + "\" />\n");
		// "Configuration attribute's type" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_PROP_TYPE + "\""
		        + " name=\"" + REQ_PAR_PROP_TYPE + "\""
		        + " value=\""
		        + ((reqValPropType != null) ? reqValPropType : "")
		        + "\" />\n");
		// "Configuration attribute's value" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_PROP_VALUE + "\""
		        + " name=\"" + REQ_PAR_PROP_VALUE + "\""
		        + " value=\""
		        + ((reqValPropValue != null) ? reqValPropValue : "")
		        + "\" />\n");
		// "Show configuration properties" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_SHOW_PROP + "\""
		        + " name=\"" + REQ_PAR_SHOW_PROP + "\""
		        + " value=\""
		        + reqValShowProp
		        + "\" />\n");
		// "Show activators" input field
		b_internal.append(sp(in) + "<input type=\"hidden\""
		        + " id=\"" + REQ_PAR_SHOW_ACTV + "\""
		        + " name=\"" + REQ_PAR_SHOW_ACTV + "\""
		        + " value=\""
		        + reqValShowActv
		        + "\" />\n");
		
		// Create the form
		b.append(
			form().withId("metrics").withName("metrics").withMethod(POST).withAction("/index").with(
				// display the accumulated errors
				errorFieldsetBuilder(e),
				text(b_internal.toString())
			).build(in)
		);
	}

	protected void renderPluginList(boolean reqValShowProp,
			boolean reqValShowActv, long in, StringBuilder b) {
		StringBuilder b_internal = new StringBuilder();
		
		// Retrieve information for all registered metric plug-ins
		Collection<PluginInfo> l = getPluginAdmin().listPlugins();

		List<HTMLTableRowBuilder> pluginRows = new ArrayList<HTMLTableRowBuilder>();
		//------------------------------------------------------------
		// Display not-installed plug-ins first
		//------------------------------------------------------------
		for(PluginInfo i : l) {
		    if (i.installed == false) {
		    	pluginRows.add(
		    		tableRow()
						.withClass("edit")
						.withAttribute("onclick", doSetFieldAndSubmitString(REQ_PAR_HASHCODE, i.getHashcode()))
					.with(
						tableColumn().withClass("trans").with(
							node("img").withAttribute("src", "/edit.png").withAttribute("alt", "[Edit]"),
							text("&nbsp;"),
							text("Registered")
						),
						tableColumn().withClass("trans").with(
							text(i.getPluginName())
						),
						tableColumn().withClass("trans").with(
							text(StringUtils.join((String[]) (
		                        i.getServiceRef().getProperty(
		                                Constants.OBJECTCLASS)),","))
						),
						tableColumn().withClass("trans").with(
							text(i.getPluginVersion())
						)
					)
		    	);
				pluginRows.addAll(pluginAttributesBuilders(
						i, reqValShowProp, reqValShowActv));
		    }
		}
		//------------------------------------------------------------
		// Installed plug-ins
		//------------------------------------------------------------
		for(PluginInfo i : l) {
		    if (i.installed) {
		        b_internal.append(sp(in) + "<tr class=\"edit\""
		                + " onclick=\"javascript:"
		                + "document.getElementById('"
		                + REQ_PAR_HASHCODE + "').value='"
		                + i.getHashcode() + "';"
		                + "document.metrics.submit();\""
		                + ">\n");
		        // Plug-in state
		        b_internal.append(sp(++in) + "<td class=\"trans\">"
		                + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
		                + "&nbsp;Installed</td>\n");
		        // Plug-in name
		        b_internal.append(sp(in) + "<td class=\"trans\">"
		                + i.getPluginName()
		                + "</td>\n");
		        // Plug-in class
		        b_internal.append(sp(in) + "<td class=\"trans\">"
		                + StringUtils.join((String[]) (
		                        i.getServiceRef().getProperty(
		                                Constants.OBJECTCLASS)),",")
		                                + "</td>\n");
		        // Plug-in version
		        b_internal.append(sp(in) + "<td class=\"trans\">"
		                + i.getPluginVersion() + "</td>\n");
		        b_internal.append(sp(--in) + "</tr>\n");	
		        // Extended plug-in information
		        b_internal.append(renderPluginAttributes(
		                i, reqValShowProp, reqValShowActv, in));
		    }
		}
		
		//------------------------------------------------------------
		// Display flags
		//------------------------------------------------------------
		StringBuilder span = new StringBuilder();
		span.append(sp(in) + "<span>\n");
		span.append(sp(++in) + "<input"
		        + " type=\"checkbox\""
		        + ((reqValShowProp) ? "checked" : "")
		        + " onclick=\"javascript:"
		        + "document.getElementById('"
		        + REQ_PAR_SHOW_PROP + "').value = this.checked;"
		        + "document.getElementById('"
		        + REQ_PAR_HASHCODE + "').value='';"
		        + "document.metrics.submit();\""
		        + " />Display properties\n");
		span.append(sp(++in) + "<input"
		        + " type=\"checkbox\""
		        + ((reqValShowActv) ? "checked" : "")
		        + " onclick=\"javascript:"
		        + "document.getElementById('"
		        + REQ_PAR_SHOW_ACTV + "').value = this.checked;"
		        + "document.getElementById('"
		        + REQ_PAR_HASHCODE + "').value='';"
		        + "document.metrics.submit();\""
		        + " />Display activators\n");
		span.append(sp(--in) + "</span>\n");
		
		b.append(
			node("fieldset").with(
				node("legend").with(text("All plug-ins")),
				table().with(
					node("thead").with(
						tableRow().withClass("head").with(
							tableColumn().withClass("head").withStyle("width: 80px;").with(
								text("Status")
							),
							tableColumn().withClass("head").withStyle("width: 30%;").with(
								text("Name")
							),
							tableColumn().withClass("head").withStyle("width: 40%;").with(
								text("Class")
							),
							tableColumn().withClass("head").with(
								text("Version")
							)
						)
					),
					node("tbody").with(
						pluginRows.toArray(GenericHTMLBuilder.EMPTY_ARRAY)
					).with(
						text(b_internal.toString())
					)
				),
				text(span.toString())
			).build(in)
		);
	}

	protected long renderPluginEditor(PluginInfo selPI, long in,
			StringBuilder b) {
		StringBuilder b_internal = new StringBuilder();
		
		//------------------------------------------------------------
		// Create the plug-in info table
		//------------------------------------------------------------
		in = renderPluginInfoTable(selPI, in, b_internal);

		//------------------------------------------------------------
		// Registered metrics, activators and configuration 
		//------------------------------------------------------------
		if (selPI.installed) {
		    renderMetricsFieldSet(selPI, in, b_internal);
		    renderPropertiesFieldset(selPI, in, b_internal);
		}
		
		b.append(
			node("fieldset").with(
				node("legend").with(text(selPI.getPluginName())),
				text(b_internal.toString())
			).build(in)
		);
		return in;
	}

	protected void renderPropertiesFieldset(PluginInfo selPI, long in,
			StringBuilder b) {
		Set<PluginConfiguration> config = getPluginByHashcode(selPI.getHashcode()).getConfigurations();
		List<HTMLTableRowBuilder> propertyRows = new ArrayList<HTMLTableRowBuilder>();
		if ((config == null) || (config.isEmpty())) {
			propertyRows.add(
				tableRow().with(
					tableColumn().withColspan(3).withClass("noattr").with(
						text("This plug-in has no configuration properties.")
					)
				)
			);
		}
		else {
		    for (PluginConfiguration param : config) {
		    	propertyRows.add(
		    		tableRow()
			    		.withClass("edit")
			    		.withAttribute("onclick",
			    				"javascript:"
			    				+ doSetString(REQ_PAR_ACTION, ACT_VAL_REQ_UPD_PROP)
			    				+ doSetString(REQ_PAR_PROP_NAME, param.getName())
			    				+ doSetString(REQ_PAR_PROP_TYPE, param.getType())
			    				+ doSetString(REQ_PAR_PROP_DESC, param.getMsg())
			    				+ doSetString(REQ_PAR_PROP_VALUE, param.getValue())
			    				+ SUBMIT)
			    	.with(
			    		tableColumn()
			    			.withClass("trans")
			    			.withAttribute("title",
			    					param.getMsg() == null
			    						? "No description available."
			    						: param.getMsg())
			    		.with(
			    			node("img").withAttribute("src", "/edit.png").withAttribute("alt", "[Edit]"),
			    			text("&nbsp;"),
			    			text(param.getName())
			    		),
			    		tableColumn().withClass("trans").with(
			    			text(param.getType())
			    		),
			    		tableColumn().withClass("trans").with(
		    				text(param.getValue())
	    				)
			    	)
			    );
		    }
		}
		b.append(
			node("fieldset").with(
				node("legend").with(
					text("Configuration properties")
				),
				table().with(
					node("thead").with(
						tableRow().withClass("head").with(
							tableColumn().withClass("head").withStyle("width: 30%;").with(
								text("Name")
							),
							tableColumn().withClass("head").withStyle("width: 20%;").with(
								text("Type")
							),
							tableColumn().withClass("head").withStyle("width: 50%;").with(
								text("Value")
							)
						)
					),
					node("tbody").with(
						propertyRows.toArray(GenericHTMLBuilder.EMPTY_ARRAY)
					).with(
						// toolbar
						tableRow().with(
							tableColumn().withColspan(3).with(
								input()
									.withType(BUTTON)
									.withClass("install")
									.withStyle("width: 100px;")
									.withValue("Add property")
									.withAttribute("onclick", doSetActionAndSubmitString(ACT_VAL_REQ_ADD_PROP))
							)
						)
					)
				)
			).build(in)
		);
	}

	protected void renderMetricsFieldSet(PluginInfo selPI, long in,
			StringBuilder b) {
		// Get the list of supported metrics
		List<Metric> metrics = getPluginAdmin().getPlugin(selPI).getAllSupportedMetrics();
		List<HTMLTableRowBuilder> metricRows = new ArrayList<HTMLTableRowBuilder>();
		if ((metrics == null) || (metrics.isEmpty())) {
			metricRows.add(
				tableRow().with(
					tableColumn().withColspan(4).withClass("noattr").with(
						text("This plug-in does not support metrics.")
					)
				)
			);
		}
		else {
		    for (Metric metric: metrics) {
		    	metricRows.add(
		    		tableRow().with(
		    			tableColumn().with(
		    				text(Long.toString(metric.getId()))
		    			),
		    			tableColumn().with(
		    				text(metric.getMnemonic())
		    			),
		    			tableColumn().with(
		    				text(metric.getMetricType().getType())
		    			),
		    			tableColumn().with(
		    				text(metric.getDescription())
		    			)
		    		)
		    	);
		    }
		}
		
		b.append(
			node("fieldset").with(
				node("legend").with(text("Supported metrics")),
				table().with(
					node("thead").with(
						tableRow().withClass("head").with(
							tableColumn().withClass("head").withStyle("width: 10%").with(
								text("Id")
							),
							tableColumn().withClass("head").withStyle("width: 25%").with(
								text("Name")
							),
							tableColumn().withClass("head").withStyle("width: 25%").with(
								text("Type")
							),
							tableColumn().withClass("head").withStyle("width: 40%").with(
								text("Description")
							)
						)
					),
					node("tbody").with(
						metricRows.toArray(GenericHTMLBuilder.EMPTY_ARRAY)
					)
				)
			).build(in)
		);
	}

	protected long renderPluginInfoTable(PluginInfo selPI, long in,
			StringBuilder b) {
		b.append(
			table().with(
				// header
				node("thead").with(
					tableRow().withClass("head").with(
						tableColumn().withClass("head").withStyle("width: 80px;").with(
							text("Status")
						),
						tableColumn().withClass("head").withStyle("width: 30%;").with(
							text("Name")
						),
						tableColumn().withClass("head").withStyle("width: 40%;").with(
							text("Class")
						),
						tableColumn().withClass("head").with(
							text("Version")
						)
					)
				),
				// body
				node("tbody").with(
					// plugin info
					tableRow().with(
						tableColumn().with(
							selPI.installed
								? text("Installed")
								: text("Registered")
						),
						tableColumn().with(text(
							selPI.getPluginName()
						)),
						tableColumn().with(text(
							 StringUtils.join((String[]) (
				                selPI.getServiceRef().getProperty(
			                        Constants.OBJECTCLASS)),",")
						)),
						tableColumn().with(text(
							selPI.getPluginVersion()
						))
					),
					// toolbar buttons
					tableRow().with(
						tableColumn().withColspan(4).with(
							input()
								.withType(BUTTON)
								.withClass("install")
								.withStyle("width: 100px;")
								.withValue("Plug-ins list")
								.withAttribute("onclick", doSetFieldAndSubmitString(REQ_PAR_HASHCODE, "")),
							selPI.installed
								? input()
										.withType(BUTTON)
										.withClass("install")
										.withStyle("width: 100px;")
										.withValue("Uninstall")
										.withAttribute("onclick", doSetActionAndFieldAndSubmitString(ACT_VAL_UNINSTALL_PLUGIN, REQ_PAR_HASHCODE, selPI.getHashcode()))
								: null,
							selPI.installed
							? input()
									.withType(BUTTON)
									.withClass("install")
									.withStyle("width: 100px;")
									.withValue("Synchronise")
									.withAttribute("onclick", doSetActionAndFieldAndSubmitString(ACT_VAL_SYNC_PLUGIN, REQ_PAR_HASHCODE, selPI.getHashcode()))
									: null,
							selPI.installed
							? null
							: input()
									.withType(BUTTON)
									.withClass("install")
									.withStyle("width: 100px;")
									.withValue("Install")
									.withAttribute("onclick", doSetActionAndFieldAndSubmitString(ACT_VAL_INSTALL_PLUGIN, REQ_PAR_HASHCODE, selPI.getHashcode()))
						)
					)
				)
			).build(in)
		);
		
		return in;
	}

	protected void renderPluginPropertyEditor(PluginInfo selPI,
			StringBuilder b, long in, String reqValPropName,
			String reqValPropDescr, String reqValPropType,
			String reqValPropValue) {
		
		// Check for a property update request
		boolean update = selPI.hasConfProp(reqValPropName, reqValPropType);
		
		// Property's name
		String propNameValue = ((reqValPropName != null) ? reqValPropName : "");
		HTMLTableRowBuilder propertyName = propertyInfoOrUpdaterRowBuilder(update, REQ_PAR_PROP_NAME, "Name", propNameValue);
		
		// Property's description
		String propDescValue = ((reqValPropDescr != null) ? reqValPropDescr : "");
		HTMLTableRowBuilder propertyDescription = propertyInfoOrUpdaterRowBuilder(update, REQ_PAR_PROP_DESC, "Description", propDescValue);
		
		// Property's type
		String propTypeValue = ((reqValPropType != null) ? reqValPropType : "");
		GenericHTMLBuilder<?> propertyType =
			tableRow().with(
				tableColumn().withClass("borderless").withStyle("width: 100px;").with(
					node("b").with(text("Type"))
				),
				tableColumn().withClass("borderless").with(
					update
						? text("value")
						: node("select")
								.withClass("form")
								.withId(REQ_PAR_PROP_TYPE)
								.withName(REQ_PAR_PROP_TYPE).with(
									generateOptions(ConfigurationType.values(),propTypeValue)
										.toArray(GenericHTMLBuilder.EMPTY_ARRAY)
								)
				)
			);
		
		// Property's value
		String propValue = ((reqValPropValue != null) ? reqValPropValue : "");
		GenericHTMLBuilder<?> propertyValue = propertyInfoOrUpdaterRowBuilder(false, REQ_PAR_PROP_VALUE, "Value", propValue);
		
		// Command tool-bar
		String command = ((update) ? "Update" : "Create");
		GenericHTMLBuilder<?> toolbar =
			tableRow().with(
				tableColumn().withColspan(2).withClass("borderless").with(
					input()
						.withType(BUTTON)
						.withClass("install")
						.withStyle("width: 100px;")
						.withValue(command)
						.withAttribute("onclick", doSetActionAndSubmitString(ACT_VAL_CON_PROP)),
					text("&nbsp;")
				).with(
					update
						? new GenericHTMLBuilder<?>[] {
							input()
								.withType(BUTTON)
								.withClass("install")
								.withStyle("width: 100px;")
								.withValue("Remove")
								.withAttribute("onclick", doSetActionAndSubmitString(ACT_VAL_CON_REM_PROP)),
							text("&nbsp;")
						}
						: GenericHTMLBuilder.EMPTY_ARRAY
				).with(
					input()
						.withType(BUTTON)
						.withClass("install")
						.withStyle("width: 100px;")
						.withValue("Cancel")
						.withAttribute("onclick", doSubmitString())
					
				)
			);
		
		b.append(
			node("fieldset").with(
				node("fieldset").with(text(
					(update
						? "Update property of "
						: "Create property for ") + selPI.getPluginName()
				)),
				table().withClass("borderless").with(
					propertyName,
					propertyDescription,
					propertyType,
					propertyValue,
					toolbar
				)
			).build(in)
		);
	}

	protected Collection<GenericHTMLBuilder<?>> generateOptions(
			Object[] values, String selected) {
		Collection<GenericHTMLBuilder<?>> typeOptions = new ArrayList<GenericHTMLBuilder<?>>();
		for (Object type : values) {
			HTMLNodeBuilder option = 
				node("option").withAttribute("value", type.toString()).with(
					text(type.toString())
				);
			if (type.toString().equals(selected)) {
				option = option.withAttribute("selected", "selected");
			}
			typeOptions.add(option);
		}
		return typeOptions;
	}

	protected HTMLTableRowBuilder propertyInfoOrUpdaterRowBuilder(boolean update,
			String propertyName, String propertyLabel, String propertyValue) {
		return tableRow().with(
			tableColumn().withClass("borderless").withStyle("width: 100px;").with(
				node("b").with(text(propertyLabel))
			),
			tableColumn().withClass("borderless").with(
				update
					? text(propertyValue)
					: input()
						.withType(TEXT)
						.withClass("form")
						.withId(propertyName)
						.withName(propertyName)
						.withValue(propertyValue)
			)
		);
	}

	protected MetricActivator getMetricActivator() {
		return compMA;
	}

	protected Plugin getPluginByHashcode(String hashcode) {
		return Plugin.getPluginByHashcode(hashcode);
	}

	protected PluginAdmin getPluginAdmin() {
		return sobjPA;
	}

    /**
     * Creates a set of table rows populated with the plug-in properties and
     * activators, as found in the given <code>PluginInfo</code> object
     * 
     * @param pluginInfo the plug-in's <code>PluginInfo</code> object
     * @param showProperties display flag
     * @param showActivators display flag
     * @param in indentation value for the generated HTML content
     * 
     * @return The table as HTML presentation.
     */
    protected static String renderPluginAttributes(
            PluginInfo pluginInfo,
            boolean showProperties,
            boolean showActivators,
            long in) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder();
        // List the metric plug-in's configuration properties
        ArrayList<HTMLTableRowBuilder> rows = pluginAttributesBuilders(
				pluginInfo, showProperties, showActivators);
        
        for (HTMLTableRowBuilder builder : rows) {
        	b.append(builder.build(in));
        }
        return b.toString();
    }

	protected static ArrayList<HTMLTableRowBuilder> pluginAttributesBuilders(
			PluginInfo pluginInfo, boolean showProperties,
			boolean showActivators) {
		ArrayList<HTMLTableRowBuilder> rows = new ArrayList<HTMLTableRowBuilder>();
        if (showProperties) {
            rows.addAll(pluginPropertiesBuilders(pluginInfo));
        }
        // List the metric plug-in's activator types
        if (showActivators) {
			rows.addAll(pluginActivatorBuilders(pluginInfo));
        }
		return rows;
	}

	protected static ArrayList<HTMLTableRowBuilder> pluginActivatorBuilders(
			PluginInfo pluginInfo) {
		Set<Class<? extends DAObject>> activators =
		    pluginInfo.getActivationTypes();
		// Skip if this plug-ins has no activators
		ArrayList<HTMLTableRowBuilder> activatorRow = new ArrayList<HTMLTableRowBuilder>();
		if (activators != null) {
		    for (Class<? extends DAObject> activator : activators) {
		    	activatorRow.add(
		    		tableRow().with(
		    			tableColumn().with(
		    				text("&nbsp;")
		    			),
		    			tableColumn().withColspan(3).withClass("attr").with(
							node("b").with(text("Activator:")),
							text(activator.getName())
		    			)
		    		)
		    	);
		    }
		}
		return activatorRow;
	}
	
	protected static String doSetFieldAndSubmitString(String field, String value) {
		return "javascript:" + doSetString(field, value) + SUBMIT;
	}

	protected static String doSubmitString() {
		return "javascript:" + SUBMIT;
	}
	
	protected static String doSetActionAndSubmitString(String action) {
		return doSetFieldAndSubmitString(REQ_PAR_ACTION, action);
	}
	
	private static String doSetActionAndFieldAndSubmitString(String action, String field, String value) {
		return "javascript:" + doSetString(REQ_PAR_ACTION, action) + doSetString(field, value) + SUBMIT;
	}
	
	private static String doSetString(String field, String value) {
		return "document.getElementById('" + field + "').value='" + value + "';";
	}

	protected static ArrayList<HTMLTableRowBuilder> pluginPropertiesBuilders(
			PluginInfo pluginInfo) {
		Set<PluginConfiguration> configurations =
		    pluginInfo.getConfiguration();
		// Skip if this plug-ins has no configuration
		ArrayList<HTMLTableRowBuilder> propertyRows = new ArrayList<HTMLTableRowBuilder>();
		if ((configurations != null) && !configurations.isEmpty()) {
		    for (PluginConfiguration config : configurations) {
		    	propertyRows.add(
		    		tableRow().with(
			    		tableColumn().with(
			    			text("&nbsp;")
			    		),
			    		tableColumn().withColspan(3).withClass("attr").with(
			    			node("b").with(text("Property:")),
			    			text(config.getName()),
			    			text("&nbsp;"),
			    			node("b").with(text("Type:")),
			    			text(config.getType()),
			    			text("&nbsp;"),
			    			node("b").with(text("Value:")),
			    			text(config.getValue())
			    		)
			    	)
		    	);
		    }
		}
		return propertyRows;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
