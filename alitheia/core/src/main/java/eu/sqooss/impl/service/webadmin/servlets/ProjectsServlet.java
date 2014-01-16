package eu.sqooss.impl.service.webadmin.servlets;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.*;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.*;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/**
 * This servlet is responsible for projects.
 * It provides pages to see the list of all projects or a single project.
 * It provides forms and actions to add, edit or delete projects.
 * It also provides actions to synchronize project metrics and run updaters
 */
@SuppressWarnings("serial")
public class ProjectsServlet extends AbstractWebadminServlet {

	private static final String ROOT_PATH = "/projects";

	private static final String PAGE_PROJECTSLIST = ROOT_PATH;

	private static final String PAGE_ADDPROJECT = ROOT_PATH + "/add";
	private static final String PAGE_DELETEPROJECT = ROOT_PATH + "/delete";
	private static final String PAGE_VIEWPROJECT = ROOT_PATH + "/view";
	private static final String ACTION_PROJECT = ROOT_PATH + "/action";

	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_PROJECTSLIST, "/projects/projectlist.vm")
			.put(PAGE_ADDPROJECT, "/projects/add.vm")
			.put(PAGE_DELETEPROJECT, "/projects/delete.vm")
			.put(PAGE_VIEWPROJECT, "/projects/project.vm")
			.build();

	/**
	 * Valid actions for a project. These are the valid values for the "action"
	 * parameter (when lowercase)
	 */
	private enum PROJECT_ACTIONS {
		INSTALLBYFORM,
		INSTALLBYPROPERTIES,
		DELETE,
		SYNCHRONIZE,
		TRIGGERUPDATE,
		TRIGGERALLUPDATE,
		TRIGGERALLUPDATENODE,
		INVALID;
	}

	private final Scheduler sobjSched;
	private final UpdaterService sobjUpdater;
	private final ClusterNodeService sobjClusterNode;
	private final PluginAdmin sobjPA;
	private final MetricActivator sobjMA;
	private AdminService sobjAdminService;

	public ProjectsServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjSched = core.getScheduler();
		sobjMA = core.getMetricActivator();
		sobjUpdater = core.getUpdater();
		sobjClusterNode = core.getClusterNodeService();
		sobjPA = core.getPluginAdmin();
		sobjAdminService = core.getAdminService();
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}

	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {

		// Switch over the URI
		switch(req.getRequestURI()) {
		case PAGE_PROJECTSLIST:
			return PageProjectsList(req, vc);
		case PAGE_ADDPROJECT:
			return PageAddProject(req, vc);
		case PAGE_DELETEPROJECT:
			return PageDeleteProject(req, vc);
		case PAGE_VIEWPROJECT:
			return PageViewProject(req, vc);
		case ACTION_PROJECT:
			return renderAction(req, vc);
		default:
			throw new PageNotFoundException();
		}
	}

	private Template renderAction(HttpServletRequest req, VelocityContext vc) {
		// Convert the action argument to the enum and switch on it
		PROJECT_ACTIONS action;
		try {
			action = Enum.valueOf(PROJECT_ACTIONS.class, req.getParameter("action").toUpperCase());
		}
		catch(IllegalArgumentException | NullPointerException e) {
			action = PROJECT_ACTIONS.INVALID;
		}
		switch (action) {
		case INSTALLBYFORM:
			return installProjectByForm(req, vc);
		case INSTALLBYPROPERTIES:
			return installProjectByProperties(req, vc);
		case DELETE:
			return deleteProject(req, vc);
		case SYNCHRONIZE:
			return synchronizePlugin(req, vc);
		case TRIGGERUPDATE:
			return triggerUpdate(req, vc);
		case TRIGGERALLUPDATE:
			return triggerAllUpdate(req, vc);
		case TRIGGERALLUPDATENODE:
			return triggerAllUpdateNode(req, vc);
		default:
			return makeErrorMsg(vc, "No or invalid action ");
		}
	}

	private Template installProjectByForm(HttpServletRequest req,
			VelocityContext vc) {
		// Install a project by a filled form
		AdminAction aa = sobjAdminService.create(AddProject.MNEMONIC);
		String scm, name, bug, bts, mail, web;
			
		if (req.getParameter("REQ_PAR_PRJ_CODE") == null)
		    scm = "";
        else
            scm = req.getParameter("REQ_PAR_PRJ_CODE");
        aa.addArg("scm", scm);
		
		if (req.getParameter("REQ_PAR_PRJ_NAME") == null)
            name = "";
        else
            name = req.getParameter("REQ_PAR_PRJ_NAME");
        aa.addArg("name", name);
        
        if (req.getParameter("REQ_PAR_PRJ_BUG") == null)
            bug = "";
        else
            bug = req.getParameter("REQ_PAR_PRJ_BUG");
        aa.addArg("bug", bug);
		
		if (req.getParameter("REQ_PAR_PRJ_BTS") == null)
            bts = "";
        else
            bts = req.getParameter("REQ_PAR_PRJ_BTS");
        aa.addArg("bts", bts);
		
		if (req.getParameter("REQ_PAR_PRJ_MAIL") == null)
		    mail = "";
		else
		    mail = req.getParameter("REQ_PAR_PRJ_MAIL");
		aa.addArg("mail", mail);
		
		if (req.getParameter("REQ_PAR_PRJ_WEB") == null)
		    web = "";
		else
		    web = req.getParameter("REQ_PAR_PRJ_WEB");
		aa.addArg("web", web);
		sobjAdminService.execute(aa);

		// Print result
		if (aa.hasErrors())
			return makeErrorMsg(vc, "A problem occurred when installing the project");
		else
			return makeSuccessMsg(vc, "The project is installed succesfully");
	}

	private Template installProjectByProperties(HttpServletRequest req,
			VelocityContext vc) {
		// Install a project by a project.properties file
		AdminAction aa = sobjAdminService.create(AddProject.MNEMONIC);
		
		String properties;
		if (req.getParameter("properties") == null) 
		    properties = "";
		else
		    properties = req.getParameter("properties");
		aa.addArg("dir", properties);
		sobjAdminService.execute(aa);

		// Print result
		if (aa.hasErrors())
			return makeErrorMsg(vc, errorMapToString(aa.errors()));
		else
			return makeSuccessMsg(vc, resultMapToString(aa.results()));
	}

	private Template deleteProject(HttpServletRequest req, VelocityContext vc) {
		if (getSelectedProject(req) != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjDB, sobjPA, getSelectedProject(req));
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				return makeErrorMsg(vc, getTranslation().error("e0034"));
			}
			return makeSuccessMsg(vc, "A delete project job is enqueued in the scheduler");
		} else
			return makeErrorMsg(vc, getTranslation().error("e0034"));
	}

	private Template synchronizePlugin(HttpServletRequest req,
			VelocityContext vc) {
	    String hashcode;
	    if (req.getParameter("REQ_PAR_SYNC_PLUGIN") == null)
	        return makeErrorMsg(vc, "The hashcode that identify a plugin is empty");
	    else
	        hashcode = req.getParameter("REQ_PAR_SYNC_PLUGIN");
		PluginInfo pInfo = sobjPA.getPluginInfo(hashcode);
		if (pInfo != null) {
			AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
			if (pObj != null) {
				sobjMA.syncMetric(pObj, getSelectedProject(req));
				sobjLogger.debug("Syncronise plugin (" + pObj.getName()
						+ ") on project (" + getSelectedProject(req).getName() + ").");
				return makeSuccessMsg(vc, "Jobs are scheduled to run the plugin over all projects");
			}
		}
		return makeErrorMsg(vc, "Could not find the plugin");
	}

	private Template triggerUpdate(HttpServletRequest req,
			VelocityContext vc) {
		// Trigger an updater on a project
		AdminAction aa = sobjAdminService.create(UpdateProject.MNEMONIC);
		aa.addArg("project", getSelectedProject(req).getId());
		String reqUpd = req.getParameter("reqUpd");
		if (reqUpd == null)
		    reqUpd = "";
		aa.addArg("updater", reqUpd);
		sobjAdminService.execute(aa);

		// Print result
		if (aa.hasErrors())
			return makeErrorMsg(vc, errorMapToString(aa.errors()));
		else
			return makeSuccessMsg(vc, resultMapToString(aa.results()));
	}

	private Template triggerAllUpdate(HttpServletRequest req,
			VelocityContext vc) {

		// Trigger all updaters on a project
		AdminAction aa = sobjAdminService.create(UpdateProject.MNEMONIC);
		aa.addArg("project", getSelectedProject(req).getId());
		sobjAdminService.execute(aa);

		// Print result
		if (aa.hasErrors())
			return makeErrorMsg(vc, errorMapToString(aa.errors()));
		else
			return makeSuccessMsg(vc, resultMapToString(aa.results()));
	}

	private Template triggerAllUpdateNode(HttpServletRequest req, VelocityContext vc) {

		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		boolean hasErrors = false;

		if (projectList == null || projectList.isEmpty())
			return makeErrorMsg(vc, "Triggering updaters failed because there are no projects");
		else {
			// Trigger all updaters on the node
			for (StoredProject project : projectList) {

				// Execute updaters
				AdminAction aa = sobjAdminService.create(UpdateProject.MNEMONIC);
				aa.addArg("project", project.getId());
				sobjAdminService.execute(aa);

				// Merge the new results with the old ones
				if (aa.hasErrors()) {
					hasErrors = true;
				}
			}

			// Print result
			if (hasErrors)
				return makeErrorMsg(vc, "A problem occured when triggering the updates");
			else
				return makeSuccessMsg(vc, "Succesfully triggered the updates");
		}
	}

	private Template PageProjectsList(HttpServletRequest req, VelocityContext vc) {

		// Load the template
		Template t = loadTemplate(templates.get(PAGE_PROJECTSLIST));

		// Add selected project
		vc.put("selProject", getSelectedProject(req));

		// Add installed metrics
		Collection<PluginInfo> installedMetrics = sobjPA.listPlugins();
		vc.put("installedMetrics", installedMetrics);

		// The list of projects
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		vc.put("projectList", projectList);

		// Add updaters
		vc.put("updaterStages", UpdaterStage.values());
		vc.put("sobjUpdater", sobjUpdater);

		// Add ClusterNode service
		vc.put("sobjClusterNode", sobjClusterNode);

		// Static classes for some information of projects
		vc.put("ProjectVersion", ProjectVersion.class);
		vc.put("MailMessage", MailMessage.class);
		vc.put("Bug", Bug.class);

		return t;
	}


	private Template PageAddProject(HttpServletRequest req, VelocityContext vc) {
		// Load the template
		Template t = loadTemplate(templates.get(PAGE_ADDPROJECT));
		return t;
	}

	private Template PageDeleteProject(HttpServletRequest req, VelocityContext vc) {

		// Load the template
		Template t = loadTemplate(templates.get(PAGE_DELETEPROJECT));

		// Add selected project
		vc.put("selProject", getSelectedProject(req));

		return t;
	}

	private Template PageViewProject(HttpServletRequest req, VelocityContext vc) {

		// Load the template
		Template t = loadTemplate(templates.get(PAGE_VIEWPROJECT));

		// Add selected project
		vc.put("selProject", getSelectedProject(req));

		return t;
	}

	private StoredProject getSelectedProject(HttpServletRequest req) {
		// Set selected project
		String projectId = req.getParameter("REQ_PAR_PROJECT_ID");
		if (projectId == null || projectId.equals(""))
			return null;
		else
			return sobjDB.findObjectById(StoredProject.class, fromString(projectId));
	}

	private String errorMapToString(Map<String, Object> map) {
	    if (map == null)
	        return "";
	      
	    String string = "";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			string += entry.getKey() + ": " + entry.getValue().toString() + "<br />\n";
		}
		return string;
	}

	private String resultMapToString(Map<String, Object> map) {
	    if (map == null)
            return "";
	    
	    String string = "";
		for (Object o : map.values()) {
			string += o.toString() + "<br />\n";
		}
		return string;
	}

	/**
	 * A job to delete a project
	 */
	// There are some problems with this class, but iIt was not changed when the webadmin was refactored
	private class ProjectDeleteJob extends Job {

		private StoredProject sp;
		private DBService dbs;
		private PluginAdmin pa;

		public ProjectDeleteJob(DBService dbs, PluginAdmin pa, StoredProject sp) {
			this.dbs = dbs;
			this.pa = pa;
			this.sp = sp;
		}

		@Override
		public long priority() {
			return 0xff;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void run() throws Exception {
			if (!dbs.isDBSessionActive()) {
				dbs.startDBSession();
			}

			sp = dbs.attachObjectToDBSession(sp);
			// Delete any associated invocation rules first
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put("project", sp);

			//Cleanup plugin results
			List<Plugin> ps = (List<Plugin>) dbs.doHQL("from Plugin");

			for (Plugin p : ps ) {
				AlitheiaPlugin ap = pa.getPlugin(pa.getPluginInfo(p.getHashcode()));
				if (ap == null) {
					//logger.warn("Plugin with hashcode: "+ p.getHashcode() +
					//		" not installed");
					continue;
				}

				ap.cleanup(sp);
			}

			boolean success = true;

			// Delete project version's parents.
			List<ProjectVersion> versions = sp.getProjectVersions();

			for (ProjectVersion pv : versions) {
				/* Set<ProjectVersionParent> parents = pv.getParents();
	            for (ProjectVersionParent pvp : parents) {

	            }*/
				pv.getParents().clear();
			}

			//Delete the project's config options
			List<StoredProjectConfig> confParams = StoredProjectConfig.fromProject(sp);
			if (!confParams.isEmpty()) {
				success &= dbs.deleteRecords(confParams);
			}

			// Delete the selected project
			success &= dbs.deleteRecord(sp);

			if (success) {
				dbs.commitDBSession();
			} else {
				dbs.rollbackDBSession();
			}

		}

		@Override
		public String toString() {
			return "ProjectDeleteJob - Project:{" + sp +"}";
		}
	}

}
