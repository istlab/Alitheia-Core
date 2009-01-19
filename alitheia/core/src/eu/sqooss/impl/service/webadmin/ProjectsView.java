/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.ClusterNodeProject;
import eu.sqooss.service.db.EvaluationMark;
import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.UpdaterService.UpdateTarget;

public class ProjectsView extends AbstractView {
    // Script for submitting this page
    private static String SUBMIT = "document.projects.submit();";

    // Action parameter's values
    private static String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    private static String ACT_CON_ADD_PROJECT   = "conAddProject";
    private static String ACT_REQ_REM_PROJECT   = "reqRemProject";
    private static String ACT_CON_REM_PROJECT   = "conRemProject";
    private static String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    private static String ACT_CON_UPD_ALL       = "conUpdateAll";
    private static String ACT_CON_UPD_CODE      = "conUpdateCode";
    private static String ACT_CON_UPD_MAIL      = "conUpdateMail";
    private static String ACT_CON_UPD_BUGS      = "conUpdateBugs";
    private static String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    private static String REQ_PAR_ACTION        = "reqAction";
    private static String REQ_PAR_PROJECT_ID    = "projectId";
    private static String REQ_PAR_PRJ_NAME      = "projectName";
    private static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    private static String REQ_PAR_PRJ_CONT      = "projectContact";
    private static String REQ_PAR_PRJ_BUG       = "projectBL";
    private static String REQ_PAR_PRJ_MAIL      = "projectML";
    private static String REQ_PAR_PRJ_CODE      = "projectSCM";
    private static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    
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
    public static String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        int in = 6;

        // Initialize the resource bundles with the request's locale
        initResources(req.getLocale());

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
            reqValAction = req.getParameter(REQ_PAR_ACTION);
            
            // Retrieve the selected project's DAO (if any)
            reqValProjectId = fromString(req.getParameter(REQ_PAR_PROJECT_ID));
            if (reqValProjectId != null) {
                selProject = sobjDB.findObjectById(
                        StoredProject.class, reqValProjectId);
            }
            
            if (reqValAction == null) {
                reqValAction = "";
            } else if (reqValAction.equals(ACT_CON_ADD_PROJECT)) {
            	selProject = addProject(e, req, in);
            } else if (reqValAction.equals(ACT_CON_REM_PROJECT)) {
            	selProject = removeProject(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_CODE)) {
            	triggerCodeUpdate(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_MAIL)) {
            	triggerMailUpdate(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_BUGS)) {
            	triggerBugUpdate(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_ALL)) {
            	triggerAllUpdate(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_ALL_NODE)) {
            	triggerAllUpdateNode(e, selProject, in);
            } else {
            	// Retrieve the selected plug-in's hash-code
        		String reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);
        		syncPlugin(e, selProject, reqValSyncPlugin);
            }
        }
        createFrom(b, e, selProject, reqValAction , in);
        return b.toString();
    }
  
	// ---------------------------------------------------------------
    // Add project
    // ---------------------------------------------------------------
    private static StoredProject addProject(StringBuilder e, HttpServletRequest req, int indent) {

    	// Retrieve the specified project properties
		String reqValPrjName = req.getParameter(REQ_PAR_PRJ_NAME);
		String reqValPrjWeb = req.getParameter(REQ_PAR_PRJ_WEB);
		String reqValPrjContact = req.getParameter(REQ_PAR_PRJ_CONT);
		String reqValPrjBug = req.getParameter(REQ_PAR_PRJ_BUG);
		String reqValPrjMail = req.getParameter(REQ_PAR_PRJ_MAIL);
		String reqValPrjCode = req.getParameter(REQ_PAR_PRJ_CODE);
		// Checks the validity of the project properties
		boolean valid = true;
		if (checkProjectName(reqValPrjName) == false) {
			valid = false;
			e.append(sp(indent)).append("Invalid project name!<br/>\n");
		}
		if (checkEmail(reqValPrjContact) == false) {
			valid = false;
			e.append(sp(indent)).append("Invalid contact e-mail!<br/>\n");
		}
		if (checkUrl(reqValPrjWeb, "http,https") == false) {
			valid = false;
			e.append(sp(indent)).append("Invalid homepage URL!<br/>\n");
		}
		if ((reqValPrjBug != null) && (reqValPrjBug.length() > 0)) {
			if (checkTDSUrl(reqValPrjBug) == false) {
				valid = false;
				e.append(sp(indent)).append("Invalid bug database URL!<br/>\n");
			}
		} else if ((reqValPrjMail != null) && (reqValPrjMail.length() > 0)) {
			if (checkTDSUrl(reqValPrjMail) == false) {
				valid = false;
				e.append(sp(indent)).append("Invalid mailing list URL!<br/>\n");
			}
		} else if ((reqValPrjCode != null) && (reqValPrjCode.length() > 0)) {
			if (checkTDSUrl(reqValPrjCode) == false) {
				valid = false;
				e.append(sp(indent)).append("Invalid source code URL!<br/>\n");
			}
		} else {
			valid = false;
			e.append(sp(indent))
				.append("At least one resource source must be specified!")
				.append("<br/>\n");
		}
		StoredProject p = null;
		// Proceed upon valid project properties
		if (valid) {
			// Check if a project with the same name already exists
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("name", reqValPrjName);
			if (sobjDB.findObjectsByProperties(StoredProject.class, params).size() > 1) {
				e.append(sp(indent) + getErr("prj_exists") + "<br/>\n");
			}
			// Create the new project's DAO
			else {
				p = new StoredProject();
				p.setName(reqValPrjName);
				p.setWebsiteUrl(reqValPrjWeb);
				p.setContactUrl(reqValPrjContact);
				p.setBtsUrl(reqValPrjBug);
				p.setMailUrl(reqValPrjMail);
				p.setScmUrl(reqValPrjCode);
				// Setup the evaluation marks for all installed metrics
				Set<EvaluationMark> marks = new HashSet<EvaluationMark>();
				Map<String, Object> noProps = Collections.emptyMap();
				for (Metric m : sobjDB.findObjectsByProperties(Metric.class,
						noProps)) {
					EvaluationMark em = new EvaluationMark();
					em.setMetric(m);
					em.setStoredProject(p);
					marks.add(em);
				}
				p.setEvaluationMarks(marks);

				// Try to add the DAO to the DB
				if (sobjDB.addRecord(p) == true) {
					// register the new project in the TDS
					sobjTDS.addAccessor(p.getId(), p.getName(), p.getBtsUrl(),
							p.getMailUrl(), p.getScmUrl());
				} else {
					e.append(sp(indent)).append(getErr("prj_add_failed"))
					 .append(getMsg("m0001")).append("<br/>\n");
				}
			}
		}
		return p;
		// Return to the "Add project" view upon failure
		//if (e.length() > 0) {
			//reqValAction = ACT_REQ_ADD_PROJECT;
		//}
    }
    
    // ---------------------------------------------------------------
    // Remove project
    // ---------------------------------------------------------------
    private static StoredProject removeProject(StringBuilder e, 
    		StoredProject selProject, int indent) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				e.append(sp(indent)).append(getErr("e0034")).append("<br/>\n");
			}
			selProject = null;
		} else {
			e.append(sp(indent) + getErr("e0034") + "<br/>\n");
		}
    	return selProject;
    }
    // ---------------------------------------------------------------
	// Trigger code update
	// ---------------------------------------------------------------
	private static void triggerCodeUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
		if (sobjUpdater.update(selProject, UpdateTarget.CODE, null) == false) {
			e.append(sp(indent)).append(getErr("e0035")).append(
					getMsg("try_again")).append("<br/>\n");
		}
	}

	// ---------------------------------------------------------------
	// Trigger mailing list(s) update
	// ---------------------------------------------------------------
	private static void triggerMailUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
		if (sobjUpdater.update(selProject, UpdateTarget.MAIL, null) == false) {
			e.append(sp(indent)).append(getErr("e0036")).append(
					getMsg("try_again")).append("<br/>\n");
		}
	}

	// ---------------------------------------------------------------
	// Trigger bugs list(s) update
	// ---------------------------------------------------------------
	private static void triggerBugUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
		if (sobjUpdater.update(selProject, UpdateTarget.BUGS, null) == false) {
			e.append(sp(indent)).append(getErr("e0037")).append(
					getMsg("try_again")).append("<br/>\n");
		}
	}

	// ---------------------------------------------------------------
	// Trigger update on all resources for that project
	// ---------------------------------------------------------------
	private static void triggerAllUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
		if (sobjUpdater.update(selProject, UpdateTarget.ALL, null) == false) {
			e.append(sp(indent)).append(getErr("e0036 ")).append(
					getMsg("try_again")).append("<br/>\n");
		}
	}
	
	// ---------------------------------------------------------------
	// Trigger update on all resources on all projects of a node
	// ---------------------------------------------------------------
    private static void triggerAllUpdateNode(StringBuilder e,
			StoredProject selProject, int in) {
		List<ClusterNodeProject> projectList = 
			ClusterNodeProject.getNodeAssignments(
					ClusterNode.getClusteNodeByName(
							sobjClusterNode.getClusterNodeName()));
		
		for (ClusterNodeProject project : projectList) {
			triggerAllUpdate(e, project.getProject(), in);
		}
	}

	
	// ---------------------------------------------------------------
	// Trigger synchronize on the selected plug-in for that project
	// ---------------------------------------------------------------
    private static void syncPlugin(StringBuilder e, StoredProject selProject, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (selProject != null)) {
			PluginInfo pInfo = sobjPA.getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
				if (pObj != null) {
					compMA.syncMetric(pObj, selProject);
					sobjLogger.debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + selProject.getName() + ").");
				}
			}
		}
    }
    
    private static void createFrom(StringBuilder b, StringBuilder e, 
    		StoredProject selProject, String reqValAction, int in) {

        // ===============================================================
        // Create the form
        // ===============================================================
        b.append(sp(in) + "<form id=\"projects\""
                + " name=\"projects\""
                + " method=\"post\""
                + " action=\"/projects\">\n");

        // ===============================================================
        // Display the accumulated error messages (if any)
        // ===============================================================
        b.append(errorFieldset(e, ++in));

        // Get the complete list of projects stored in the SQO-OSS framework
        List<StoredProject> projects = sobjDB.findObjectsByProperties(
                    StoredProject.class, new HashMap<String, Object>());
        Collection<PluginInfo> metrics = sobjPA.listPlugins();

        // ===================================================================
        // "Show project info" view
        // ===================================================================
        if ((reqValAction.equals(ACT_REQ_SHOW_PROJECT))
                && (selProject != null)) {
            // Create the field-set
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>"
                    + "Project information"
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">\n");
            // Create the input fields
            b.append(normalInfoRow(
                    "Project name", selProject.getName(), in));
            b.append(normalInfoRow(
                    "Homepage", selProject.getWebsiteUrl(), in));
            b.append(normalInfoRow(
                    "Contact e-mail", selProject.getContactUrl(), in));
            b.append(normalInfoRow(
                    "Bug database", selProject.getBtsUrl(), in));
            b.append(normalInfoRow(
                    "Mailing list", selProject.getMailUrl(), in));
            b.append(normalInfoRow(
                    "Source code", selProject.getScmUrl(), in));

            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td colspan=\"2\" class=\"borderless\">\n");
            // Back button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("btn_back") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");
        }
        // ===================================================================
        // "Add project" editor
        // ===================================================================
        else if (reqValAction.equals(ACT_REQ_ADD_PROJECT)) {
            // Create the field-set
            b.append(sp(in++) + "<table class=\"borderless\" width='100%'>\n");
            // Create the input fields
            b.append(normalInputRow(
                    "Project name", REQ_PAR_PRJ_NAME, "", in));
            b.append(normalInputRow(
                    "Homepage", REQ_PAR_PRJ_WEB, "", in));
            b.append(normalInputRow(
                    "Contact e-mail", REQ_PAR_PRJ_CONT, "", in));
            b.append(normalInputRow(
                    "Bug database", REQ_PAR_PRJ_BUG, "", in));
            b.append(normalInputRow(
                    "Mailing list", REQ_PAR_PRJ_MAIL, "", in));
            b.append(normalInputRow(
                    "Source code", REQ_PAR_PRJ_CODE, "", in));

            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td colspan=\"2\" class=\"borderless\">\n");
            // Apply button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("project_add") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_ADD_PROJECT + "';"
                    + SUBMIT + "\">\n");
            // Cancel button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("cancel") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>\n");
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((reqValAction.equals(ACT_REQ_REM_PROJECT))
                && (selProject != null)) {
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>" + getLbl("l0059")
                    + ": " + selProject.getName()
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">");
            // Confirmation message
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"borderless\">"
                    + "<b>" + getMsg("delete_project") + "</b>"
                    + "</td>\n");
            // Affected invocation rules
            HashMap<String,Object> properties = new HashMap<String, Object>();
            properties.put("project", selProject);
            List<?> affectedRules = sobjDB.findObjectsByProperties(
                    InvocationRule.class, properties);
            if ((affectedRules != null) && (affectedRules.size() > 0)) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td class=\"borderless\">"
                        + getMsg("delete_assoc_rules") + ": "
                        + affectedRules.size()
                        + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td class=\"borderless\">\n");
            // Confirm button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0006") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_REM_PROJECT + "';"
                    + SUBMIT + "\">\n");
            // Cancel button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0004") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>");
            b.append(sp(in) + "</fieldset>\n");
        }
        // ===================================================================
        // Projects list view
        // ===================================================================
        else {
            addHeaderRow(b,in);

            if (projects.isEmpty()) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td colspan=\"6\" class=\"noattr\">\n"
                        + getMsg("no_projects")
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            else {
                //------------------------------------------------------------
                // Create the content rows
                //------------------------------------------------------------
                b.append(sp(in++) + "<tbody>\n");
                for (StoredProject nextPrj : projects) {
                    boolean selected = false;
                    if ((selProject != null)
                            && (selProject.getId() == nextPrj.getId())) {
                        selected = true;
                    }
                    b.append(sp(in++) + "<tr class=\""
                            + ((selected) ? "selected" : "edit") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + REQ_PAR_PROJECT_ID + "').value='"
                            + ((selected) ? "" : nextPrj.getId())
                            + "';"
                            + SUBMIT + "\">\n");
                    // Project Id
                    b.append(sp(in) + "<td class=\"trans\">"
                            + nextPrj.getId()
                            + "</td>\n");
                    // Project name
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((selected)
                                    ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\""
                                        + getLbl("btn_info")
                                        + "\""
                                        + " onclick=\"javascript:"
                                        + "document.getElementById('"
                                        + REQ_PAR_ACTION + "').value='" 
                                        + ACT_REQ_SHOW_PROJECT + "';"
                                        + SUBMIT + "\">"
                                    : "<img src=\"/edit.png\""
                                        + " alt=\"[Edit]\"/>")
                            + "&nbsp;"
                            + nextPrj.getName()
                            + "</td>\n");
                    // Last project version
                    String lastVersion = getLbl("l0051");
                    ProjectVersion v = ProjectVersion.getLastProjectVersion(nextPrj);
                    if (v != null) {
                        lastVersion = v.getRevisionId();
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + lastVersion
                            + "</td>\n");
                    // Date of the last known email
                    MailMessage mm = MailMessage.getLatestMailMessage(nextPrj);
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((mm == null)?getLbl("l0051"):mm.getSendDate())
                            + "</td>\n");
                    // ID of the last known bug entry
                    Bug bug = Bug.getLastUpdate(nextPrj);
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((bug == null)?getLbl("l0051"):bug.getBugID())
                            + "</td>\n");
                    // Evaluation state
                    String evalState = getLbl("project_not_evaluated");
                    if (nextPrj.getEvaluationMarks() != null) {
                        for( EvaluationMark mark : nextPrj.getEvaluationMarks()) {
                            if ( mark.getWhenRun() != null ) {
                                evalState = getLbl("project_is_evaluated");
                                break;
                            }
                        }
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + evalState
                            + "</td>\n");
                    
                    // Cluster node
                    String nodename = null;
                    if (null != ClusterNodeProject.getProjectAssignment(nextPrj)) {
                        if (null != ClusterNodeProject.getProjectAssignment(nextPrj).getNode()) {
                            nodename = ClusterNodeProject.getProjectAssignment(nextPrj).getNode().getName();
                        }
                    }
                    if (null == nodename) {
                        nodename = "(local)";
                    }
                    b.append(sp(in) + "<td class=\"trans\">" + nodename + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    if ((selected) && (metrics.isEmpty() == false)) {
                        showLastAppliedVersion(nextPrj, metrics, b);
                    }
                }
            }
            //----------------------------------------------------------------
            // Tool-bar
            //----------------------------------------------------------------
            addToolBar(selProject,b,in);

            //----------------------------------------------------------------
            // Close the table
            //----------------------------------------------------------------
            b.append(sp(--in) + "</tbody>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");
        }

        // ===============================================================
        // INPUT FIELDS
        // ===============================================================
        addHiddenFields(selProject,b,in);

        // ===============================================================
        // Close the form
        // ===============================================================
        b.append(sp(--in) + "</form>\n");
    }


    private static void addHiddenFields(StoredProject selProject,
            StringBuilder b,
            long in) {
        // "Action type" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_ACTION + 
                "' name='" + REQ_PAR_ACTION + "' value=''>\n");
        // "Project Id" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_PROJECT_ID +
                "' name='" + REQ_PAR_PROJECT_ID +
                "' value='" + ((selProject != null) ? selProject.getId() : "") +
                "'>\n");
        // "Plug-in hashcode" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_SYNC_PLUGIN +
                "' name='" + REQ_PAR_SYNC_PLUGIN + 
                "' value=''>\n");
    }
    
    private static void addToolBar(StoredProject selProject,
            StringBuilder b,
            long in) {
        b.append(sp(in++) + "<tr class=\"subhead\">\n");
        b.append(sp(in++) + "<td>View</td><td colspan=\"6\">\n");
        // Refresh button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0008") + "\"" + " onclick=\"javascript:" + "window.location='/projects" + ((selProject != null)
                ? "?" + REQ_PAR_PROJECT_ID + "=" + selProject.getId()
                : "") + "';\"" + ">");
        b.append("</td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'>\n");
        // Add project button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("add_project") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_REQ_ADD_PROJECT + "';" + SUBMIT + "\">\n");
        // Remove project button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0059") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_REQ_REM_PROJECT + "';" + SUBMIT + "\"" + ((selProject != null) ? "" : " disabled") + ">");
        b.append("</td></tr><tr class='subhead'><td>Update</td><td colspan='4'>\n");
        // Trigger source update
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0061") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_CODE + "';" + SUBMIT + "\"" + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                selProject, UpdateTarget.CODE) == false))
                ? "" : " disabled") + ">\n");
        // Trigger mailing list update
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0062") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_MAIL + "';" + SUBMIT + "\"" + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                selProject, UpdateTarget.MAIL) == false))
                ? "" : " disabled") + ">\n");
        // Trigger bugs list update
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0063") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_BUGS + "';" + SUBMIT + "\"" + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                selProject, UpdateTarget.BUGS) == false))
                ? "" : " disabled") + ">\n");
        // Trigger all updates
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0064") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_ALL + "';" + SUBMIT + "\"" + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                selProject, UpdateTarget.ALL) == false))
                ? "" : " disabled") + ">\n");
        b.append(sp(--in) + "</td>\n");
        b.append(sp(--in) + "<td colspan=\"2\" align=\"right\">\n");
     // Trigger updates on host
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\" value=\"Update all on "+ sobjClusterNode.getClusterNodeName() +"\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_ALL_NODE + "';" + SUBMIT + "\">\n");
        b.append(sp(--in) + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
    }
    
    private static void showLastAppliedVersion(
            StoredProject project,
            Collection<PluginInfo> metrics,
            StringBuilder b) {
        for(PluginInfo m : metrics) {
            if (m.installed) {
                b.append("<tr>\n");
                b.append(sp(1) + "<td colspan=\"7\""
                        + " class=\"noattr\">\n"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Synchronise\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + REQ_PAR_SYNC_PLUGIN + "').value='"
                        + m.getHashcode() + "';"
                        + SUBMIT + "\""
                        + ">"
                        + "&nbsp;"
                        + m.getPluginName()
                        + "</td>\n");
                b.append("</tr>\n");
            }
        }
    }

    private static void addHeaderRow(StringBuilder b, long in) {
        //----------------------------------------------------------------
        // Create the header row
        //----------------------------------------------------------------
        b.append(sp(in++) + "<table>\n");
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr class=\"head\">\n");
        b.append(sp(in) + "<td class='head'  style='width: 10%;'>"
                + getLbl("l0066")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 35%;'>"
                + getLbl("l0067")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0068")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0069")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0070")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 10%;'>"
                + getLbl("l0071")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 10%;'>"
                + getLbl("l0073")
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

