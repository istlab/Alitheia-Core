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
import static eu.sqooss.impl.service.webadmin.HTMLInputBuilder.HIDDEN;
import static eu.sqooss.impl.service.webadmin.HTMLInputBuilder.input;
import static eu.sqooss.impl.service.webadmin.HTMLNodeBuilder.node;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.table;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.tableColumn;
import static eu.sqooss.impl.service.webadmin.HTMLTableBuilder.tableRow;
import static eu.sqooss.impl.service.webadmin.HTMLTextBuilder.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.HTMLTableBuilder.HTMLTableRowBuilder;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class ProjectsView extends AbstractView {
    // Script for submitting this page
    protected static String SUBMIT = "document.projects.submit();";

    // Action parameter's values
    protected static String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    protected static String ACT_CON_ADD_PROJECT   = "conAddProject";
    protected static String ACT_REQ_REM_PROJECT   = "reqRemProject";
    protected static String ACT_CON_REM_PROJECT   = "conRemProject";
    protected static String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    protected static String ACT_CON_UPD_ALL       = "conUpdateAll";
    protected static String ACT_CON_UPD           = "conUpdate";
    protected static String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    protected static String REQ_PAR_ACTION        = "reqAction";
    protected static String REQ_PAR_PROJECT_ID    = "projectId";
    protected static String REQ_PAR_PRJ_NAME      = "projectName";
    protected static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    protected static String REQ_PAR_PRJ_CONT      = "projectContact";
    protected static String REQ_PAR_PRJ_BUG       = "projectBL";
    protected static String REQ_PAR_PRJ_MAIL      = "projectML";
    protected static String REQ_PAR_PRJ_CODE      = "projectSCM";
    protected static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    protected static String REQ_PAR_UPD           = "reqUpd";
    
    /**
     * Instantiates a new projects view.
     *
     * @param bundlecontext the <code>BundleContext</code> object
     * @param vc the <code>VelocityContext</code> object
     */
    public ProjectsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Renders the various project's views.
     *
     * @param req the servlet's request object
     *
     * @return The HTML presentation of the generated view.
     */
    public String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        int in = 6;

        // Initialize the resource bundles with the request's locale
        initializeResources(req);

        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;

        // Selected project
        StoredProject selProject = null;

        // ===============================================================
        // Parse the servlet's request object
        // ===============================================================
        if (req != null) {
            // DEBUG: Dump the servlet's request parameter
            if (DEBUG) {
                b.append(debugRequest(req));
            }

            // Retrieve the selected editor's action (if any)
            reqValAction = (req.getParameter(REQ_PAR_ACTION) != null) ? req.getParameter(REQ_PAR_ACTION) : "";
            
            // Retrieve the selected project's DAO (if any)
            reqValProjectId = fromString(req.getParameter(REQ_PAR_PROJECT_ID));
            if (reqValProjectId != null) {
                selProject = getProjectById(reqValProjectId);
            }
            
            if (ACT_CON_ADD_PROJECT.equals(reqValAction)) {
            	selProject = addProject(e, req, in);
            } else if (ACT_CON_REM_PROJECT.equals(reqValAction)) {
            	selProject = removeProject(e, selProject, in);
            } else if (ACT_CON_UPD.equals(reqValAction)) {
            	triggerUpdate(e, selProject, in, req.getParameter(REQ_PAR_UPD));
            } else if (ACT_CON_UPD_ALL.equals(reqValAction)) {
            	triggerAllUpdate(e, selProject, in);
            } else if (ACT_CON_UPD_ALL_NODE.equals(reqValAction)) {
            	triggerAllUpdateNode(e, selProject, in);
            } else {
            	// Retrieve the selected plug-in's hash-code
        		String reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);
        		syncPlugin(e, selProject, reqValSyncPlugin);
            }
        }
        createForm(b, e, selProject, reqValAction, in);
        return b.toString();
    }

	protected StoredProject getProjectById(long reqValProjectId) {
		return sobjDB.findObjectById(
		        StoredProject.class, reqValProjectId);
	}

	protected void initializeResources(HttpServletRequest req) {
		initResources(req.getLocale());
	}
  
    protected StoredProject addProject(StringBuilder e, HttpServletRequest r, int indent) {
        AdminService as = getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", r.getParameter(REQ_PAR_PRJ_CODE));
    	aa.addArg("name", r.getParameter(REQ_PAR_PRJ_NAME));
    	aa.addArg("bts", r.getParameter(REQ_PAR_PRJ_BUG));
    	aa.addArg("mail", r.getParameter(REQ_PAR_PRJ_MAIL));
    	aa.addArg("web", r.getParameter(REQ_PAR_PRJ_WEB));
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
            return null;
    	} else { 
            getVelocityContext().put("RESULTS", aa.results());
            return getProjectByName(r.getParameter(REQ_PAR_PRJ_NAME));
    	}
    }

	protected StoredProject getProjectByName(String parameter) {
		return StoredProject.getProjectByName(parameter);
	}
    
    // ---------------------------------------------------------------
    // Remove project
    // ---------------------------------------------------------------
    protected StoredProject removeProject(StringBuilder e, 
    		StoredProject selProject, int indent) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				getScheduler().enqueue(pdj);
			} catch (SchedulerException e1) {
				e.append(sp(indent)).append(getErr("e0034")).append("<br/>\n");
			}
			selProject = null;
		} else {
			e.append(sp(indent) + getErr("e0034") + "<br/>\n");
		}
    	return selProject;
    }

	protected ProjectDeleteJob createProjectDeleteJob(StoredProject selProject) {
		return new ProjectDeleteJob(sobjCore, selProject);
	}

	protected Scheduler getScheduler() {
		return sobjSched;
	}

	// ---------------------------------------------------------------
	// Trigger an update
	// ---------------------------------------------------------------
	protected void triggerUpdate(StringBuilder e,
			StoredProject selProject, int indent, String mnem) {
		AdminService as = getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", selProject.getId());
		aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
        } else { 
            getVelocityContext().put("RESULTS", aa.results());
        }
	}

	protected VelocityContext getVelocityContext() {
		return vc;
	}

	protected AdminService getAdminService() {
		return AlitheiaCore.getInstance().getAdminService();
	}

	// ---------------------------------------------------------------
	// Trigger update on all resources for that project
	// ---------------------------------------------------------------
	protected void triggerAllUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
	    AdminService as = getAdminService();
        AdminAction aa = as.create(UpdateProject.MNEMONIC);
        aa.addArg("project", selProject.getId());
        as.execute(aa);

        if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
        } else {
            getVelocityContext().put("RESULTS", aa.results());
        }
	}
	
	// ---------------------------------------------------------------
	// Trigger update on all resources on all projects of a node
	// ---------------------------------------------------------------
    protected void triggerAllUpdateNode(StringBuilder e,
			StoredProject selProject, int in) {
		Set<StoredProject> projectList = getThisNodeProjects();
		
		// RENG: would this not only put the results of the last update in velocity?
		for (StoredProject project : projectList) {
			triggerAllUpdate(e, project, in);
		}
	}
	
	// ---------------------------------------------------------------
	// Trigger synchronize on the selected plug-in for that project
	// ---------------------------------------------------------------
    protected void syncPlugin(StringBuilder e, StoredProject selProject, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (selProject != null)) {
			PluginInfo pInfo = getPluginAdmin().getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = getPluginAdmin().getPlugin(pInfo);
				if (pObj != null) {
					getMetricActivator().syncMetric(pObj, selProject);
					getLogger().debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + selProject.getName() + ").");
				}
			}
		}
    }

	protected Logger getLogger() {
		return sobjLogger;
	}

	protected MetricActivator getMetricActivator() {
		return compMA;
	}

	protected PluginAdmin getPluginAdmin() {
		return sobjPA;
	}
	
    protected void createForm(StringBuilder b, StringBuilder e, 
    		StoredProject selProject, String reqValAction, int in) {

    	// Get the complete list of projects stored in the SQO-OSS framework
        Set<StoredProject> projects = getThisNodeProjects();
        Collection<PluginInfo> metrics = getPluginAdmin().listPlugins();

        GenericHTMLBuilder<?> output = null;
        // ===================================================================
        // "Show project info" view
        // ===================================================================
        if ((ACT_REQ_SHOW_PROJECT.equals(reqValAction))
                && (selProject != null)) {
            output = 
            	node("fieldset").with(
            		node("legend")
        				.appendContent("Project information")
        		).with(
    				table().withClass("borderless").with(
    					normalInfoRowBuilder("Project name", selProject.getName()),
    					normalInfoRowBuilder("Homepage", selProject.getWebsiteUrl()),
    					normalInfoRowBuilder("Contact e-mail", selProject.getContactUrl()),
    					normalInfoRowBuilder("Bug database", selProject.getBtsUrl()),
    					normalInfoRowBuilder("Mailing list", selProject.getMailUrl()),
    					normalInfoRowBuilder("Source code", selProject.getScmUrl()),
    					// toolbar
    					tableRow().with(
    						tableColumn().withColspan(2).withClass("borderless")
    						.with(defaultButton()
    							.withValue(getLbl("btn_back"))
    							.withAttribute("onclick", doSubmitString())
    						)
    					)
    				)
    			);
        }
        // ===================================================================
        // "Add project" editor
        // ===================================================================
        else if (ACT_REQ_ADD_PROJECT.equals(reqValAction)) {
            output =
            	table().withClass("borderless").withStyle("width: 100%").with(
		        	// input rows
		        	normalInputRowBuilder("Project name", REQ_PAR_PRJ_NAME, ""),
		        	normalInputRowBuilder("Homepage", REQ_PAR_PRJ_WEB, ""),
		        	normalInputRowBuilder("Contact e-mail", REQ_PAR_PRJ_CONT, ""),
		        	normalInputRowBuilder("Bug database", REQ_PAR_PRJ_BUG, ""),
		        	normalInputRowBuilder("Mailing list", REQ_PAR_PRJ_MAIL, ""),
		        	normalInputRowBuilder("Source code", REQ_PAR_PRJ_CODE, ""),
		        	// toolbar
		        	tableRow().with(
		        		tableColumn().withColspan(2).withClass("borderless").with(
		        			defaultButton()
		        				.withValue(getLbl("project_add"))
		        				.withAttribute("onclick", doSetActionAndSubmitString(ACT_CON_ADD_PROJECT)),
		        			defaultButton()
		        				.withValue(getLbl("cancel"))
		        				.withAttribute("onclick", doSubmitString())
		        		)
		        	)
            	);
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((ACT_REQ_REM_PROJECT.equals(reqValAction))
                && (selProject != null)) {
            output =
            	node("fieldset").with(
	            	node("legend").with(
	            		text(getLbl("l0059") + ": " + selProject.getName())),
	            	table().withClass("borderless").with(
	            		// confirmation message
	            		tableRow().with(
            				tableColumn().withClass("borderless").with(
            					node("b").with(text(getMsg("delete_project")))
            				)
	            		),
            			// toolbar
	            		tableRow().with(
	            			tableColumn().withClass("borderless").with(
	            				defaultButton()
	            					.withValue(getLbl("l0006"))
	            					.withAttribute("onclick", doSetActionAndSubmitString(ACT_CON_REM_PROJECT)),
	            				defaultButton()
		            				.withValue(getLbl("l0004"))
		            				.withAttribute("onclick", doSubmitString())
	            			)
	            		)
	            	)
            	);
        }
        // ===================================================================
        // Projects list view
        // ===================================================================
        else {
        	Collection<GenericHTMLBuilder<?>> rows = new ArrayList<GenericHTMLBuilder<?>>();
            if (projects.isEmpty()) {
            	rows.add(
            		tableRow().with(
            			tableColumn().withColspan(6).withClass("noattr").with(text(
            				getMsg("no_projects")
            			))
            		)
            	);
            }
            else {
                //------------------------------------------------------------
                // Create the content rows
                //------------------------------------------------------------
                for (StoredProject currentProject : projects) {
                	boolean selected = (selProject != null) && (selProject.getId() == currentProject.getId());
                    
                    String projectId = Long.toString(currentProject.getId());
                    ProjectVersion lastProjectVersion = getLastProjectVersion(currentProject);
					MailMessage lastMailMessage = getLastMailMessage(currentProject);
					Bug lastBug = getLastBug(currentProject);
					ClusterNode clusterNode = currentProject.getClusternode();
					
					rows.add(
                    	tableRow().withClass(selected ? "selected" : "edit")
                    		.withAttribute("onclick", doSetFieldAndSubmitString(REQ_PAR_PROJECT_ID, selected ? "" : projectId))
                    	.with(
                    		// project ID
                    		tableColumn().withClass("trans").with(text(projectId)),
                    		// project Name
                    		tableColumn().withClass("trans").with(
                    			selected
                    				? defaultButton().withValue(getLbl("btn_info")).withAttribute("onclick", doSetActionAndSubmitString(ACT_REQ_SHOW_PROJECT))
                    				: node("img").withAttribute("src", "edit.png").withAttribute("alt", "[Edit]"),
                    			text("&nbsp;"),
                    			text(currentProject.getName())
                    		),
                    		// project Last Version
                    		tableColumn().withClass("trans").with(
                    			lastProjectVersion == null
                    				? text(getLbl("l0051"))
                    				: text(String.valueOf(lastProjectVersion.getSequence()) + "(" + lastProjectVersion.getRevisionId() + ")") 
                    		),
                    		// Date of last mail
                    		tableColumn().withClass("trans").with(
                    			lastMailMessage == null
                    				? text(getLbl("l0051"))
                    				: text(lastMailMessage.getSendDate().toString())
                    		),
                    		// ID of the last known bug entry
                    		tableColumn().withClass("trans").with(
                    			lastBug == null
                    				? text(getLbl("l0051"))
                    				: text(lastBug.getBugID())
                    		),
                    		// Evaluation state
                    		tableColumn().withClass("trans").with(
                    			currentProject.isEvaluated()
                    				? text(getLbl("project_is_evaluated"))
                    				: text(getLbl("project_not_evaluated"))
                    		),
                    		// Cluster node
                    		tableColumn().withClass("trans").with(
                    			clusterNode == null
                    				? text("(local)")
                    				: text(clusterNode.getName())
                    		)
                    	)
                    );

					if (selected && (metrics.isEmpty() == false)) {
						rows.addAll(lastAppliedVersion(currentProject, metrics));
					}
                }
            }
            output =
            	table().with(
            		headerRow(),
            		node("tbody").with(
            			rows.toArray(GenericHTMLBuilder.EMPTY_ARRAY)
            		).with(
            			toolbar(selProject).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
            		)
            	);
        }

        // ===============================================================
        // Create the form
        // ===============================================================
        b.append(
        	form().withId("projects").withName("projects").withMethod(POST).withAction("/projects").with(
        		// display accumulated error messages
        		errorFieldsetBuilder(e)
        	).with(
        		(output == null)
					? GenericHTMLBuilder.EMPTY_ARRAY
					: new GenericHTMLBuilder<?>[]{output}
        		)
        	// input fields
        	.with(
        		hiddenFields(selProject).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
        	).build()
        );
    }

	protected static String doSetFieldAndSubmitString(String field, String value) {
		return "javascript:document.getElementById('" + field + "').value='" + value + "';" + SUBMIT;
	}

	protected static String doSubmitString() {
		return "javascript:" + SUBMIT;
	}
	
	protected static String doSetActionAndSubmitString(String action) {
		return doSetFieldAndSubmitString(REQ_PAR_ACTION, action);
	}

	protected static HTMLInputBuilder defaultButton() {
		return input()
			.withType(BUTTON)
			.withClass("install")
			.withStyle("width: 100px;");
	}

	protected Bug getLastBug(StoredProject project) {
		return Bug.getLastUpdate(project);
	}

	protected MailMessage getLastMailMessage(StoredProject project) {
		return MailMessage.getLatestMailMessage(project);
	}

	protected ProjectVersion getLastProjectVersion(StoredProject project) {
		return ProjectVersion.getLastProjectVersion(project);
	}

	protected Set<StoredProject> getThisNodeProjects() {
		return ClusterNode.thisNode().getProjects();
	}

	protected Collection<HTMLInputBuilder> hiddenFields(StoredProject project) {
		ArrayList<HTMLInputBuilder> list = new ArrayList<HTMLInputBuilder>();
		
		String projectId = (project == null) ? "" : Long.toString(project.getId());
		list.addAll(Arrays.asList(
			input()
				.withType(HIDDEN)
				.withId(REQ_PAR_ACTION)
				.withName(REQ_PAR_ACTION)
				.withValue(""),
			input()
				.withType(HIDDEN)
				.withId(REQ_PAR_PROJECT_ID)
				.withName(REQ_PAR_PROJECT_ID)
				.withValue(projectId),
			input()
				.withType(HIDDEN)
				.withId(REQ_PAR_SYNC_PLUGIN)
				.withName(REQ_PAR_SYNC_PLUGIN)
				.withValue("")
		));
		
		return list;
	}
	
    protected Collection<HTMLTableRowBuilder> toolbar(StoredProject project) {
    	String projectID = (project != null) ? "?" + REQ_PAR_PROJECT_ID + "=" + project.getId() : "";
		return Arrays.asList(
    		tableRow().withClass("subhead").with(
    			tableColumn().with(text("View")),
    			tableColumn().withColspan(6).with(
    				defaultButton()
    					.withValue(getLbl("l0008"))
    					.withAttribute("onclick",
    							"javascript:window.location='/projects" + projectID + "';")
    			)
    		),
    		tableRow().withClass("subhead").with(
    			tableColumn().with(text("Manage")),
    			tableColumn().withColspan(6).with(
    				defaultButton()
    					.withValue(getLbl("add_project"))
    					.withAttribute("onclick", doSetActionAndSubmitString(ACT_REQ_ADD_PROJECT)),
    				defaultButton()
	    				.withValue(getLbl("l0059"))
	    				.withAttribute("onclick", doSetActionAndSubmitString(ACT_REQ_REM_PROJECT))
	    				.withDisabled(project == null)
    			)
    		),
    		tableRow().withClass("subhead").with(
    			tableColumn().with(text("Update")),
    			tableColumn().withColspan(4)
    				.with(
    					(project == null) ? GenericHTMLBuilder.EMPTY_ARRAY : new GenericHTMLBuilder<?>[]{updaterSelector(project)}
    				).with(
    					input()
		    				.withType(BUTTON)
		    				.withClass("install")
		    				.withValue("Run Updater")
		    				.withAttribute("onclick", doSetActionAndSubmitString(ACT_CON_UPD))
		    				.withDisabled(project == null),
						input()
		    				.withType(BUTTON)
		    				.withClass("install")
		    				.withValue("Run All Updaters")
		    				.withAttribute("onclick", doSetActionAndSubmitString(ACT_CON_UPD_ALL))
		    				.withDisabled(project == null)
	    			),
	    		tableColumn().withColspan(2).withAttribute("align", "right").with(
    				input()
	    				.withType(BUTTON)
	    				.withClass("install")
	    				.withValue("Update all on " + getClusterNodeName())
	    				.withAttribute("onclick", doSetActionAndSubmitString(ACT_CON_UPD_ALL_NODE))
	    		)
    		)
    	);
    }
    
    private GenericHTMLBuilder<?> updaterSelector(StoredProject project) {
		return
			node("select").withName(REQ_PAR_UPD).withId(REQ_PAR_UPD).with(
				node("optgroup").withAttribute("label", "Import Stage").with(
					getUpdaterOptions(project, UpdaterStage.IMPORT).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
				),
				node("optgroup").withAttribute("label", "Parse Stage").with(
					getUpdaterOptions(project, UpdaterStage.PARSE).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
				),
				node("optgroup").withAttribute("label", "Inference Stage").with(
					getUpdaterOptions(project, UpdaterStage.INFERENCE).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
				),
				node("optgroup").withAttribute("label", "Default Stage").with(
					getUpdaterOptions(project, UpdaterStage.DEFAULT).toArray(GenericHTMLBuilder.EMPTY_ARRAY)
				)
			);
	}

	private Collection<GenericHTMLBuilder<?>> getUpdaterOptions(
			StoredProject project, UpdaterStage stage) {
		Collection<GenericHTMLBuilder<?>> options = new ArrayList<GenericHTMLBuilder<?>>();
		
		for (Updater u : getUpdaters(project, stage)) {
			options.add(
				node("option").withAttribute("value", u.mnem()).with(text(u.descr()))
			);
		}
		
		return options;
	}

	protected Set<Updater> getUpdaters(StoredProject selProject,
			UpdaterStage importStage) {
		return sobjUpdater.getUpdaters(selProject, importStage);
	}

	protected String getClusterNodeName() {
		return sobjClusterNode.getClusterNodeName();
	}
	
	protected Collection<GenericHTMLBuilder<?>> lastAppliedVersion(StoredProject project, Collection<PluginInfo> metrics) {
		List<GenericHTMLBuilder<?>> list = new ArrayList<GenericHTMLBuilder<?>>();
		
		for (PluginInfo metric : metrics) {
			if (metric.installed) {
				list.add(
					tableRow().with(
						tableColumn().withColspan(7).withClass("noattr").with(
							defaultButton()
								.withValue("Synchronise")
								.withAttribute("onclick", doSetFieldAndSubmitString(REQ_PAR_SYNC_PLUGIN, metric.getHashcode())),
							text("&nbsp;"),
							text(metric.getPluginName())
						)
					)
				);
			}
		}
		
		return list;		
	}
    
    protected static HTMLNodeBuilder headerRow() {
    	return
    		node("thead").with(
				tableRow().withClass("head").with(
					tableColumn().withClass("head").withStyle("width: 10%").with(text(getLbl("l0066"))),
					tableColumn().withClass("head").withStyle("width: 35%").with(text(getLbl("l0067"))),
					tableColumn().withClass("head").withStyle("width: 15%").with(text(getLbl("l0068"))),
					tableColumn().withClass("head").withStyle("width: 15%").with(text(getLbl("l0069"))),
					tableColumn().withClass("head").withStyle("width: 15%").with(text(getLbl("l0070"))),
					tableColumn().withClass("head").withStyle("width: 10%").with(text(getLbl("l0071"))),
					tableColumn().withClass("head").withStyle("width: 10%").with(text(getLbl("l0073")))
				)
			);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

