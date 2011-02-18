package eu.sqooss.service.admin.actions;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminActionBase;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/**
 * An action that triggers a project update.
 *
 * <h2>Arguments</h2>
 *
 * <table>
 * <th>
 * <td>Argument</td>
 * <td>Required?</td>
 * <td>Explanation</td></th>
 *
 * <tr>
 * <td>project</td>
 * <td>yes</td>
 * <td>The project (name or id) to run the update for</td>
 * </tr>
 *
 * <tr>
 * <td>updater</td>
 * <td>no</td>
 * <td>The mnemonic of the updater to run. This parameter takes precedence over
 * the next one, in case of conflict (i.e. specified updater not in specified
 * stage)</td>
 * </tr>
 * 
 * <tr>
 * <td>stage</td>
 * <td>no</td>
 * <td>One of DEFAULT, IMPORT, PARSING, INFERENCE. If omitted, all updaters
 * available to the project will be run.</td>
 * </tr>
 *
 * </table>
 *
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public class UpdateProject extends AdminActionBase {

    @Override
    public String mnemonic() {
        return "upd";
    }

    @Override
    public String descr() {
        return "Schedules update jobs";
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        String project = args.get("project").toString();
        String stage = args.get("stage").toString();
        String updater = args.get("updater").toString();

        if (project == null) {
            error("missing.param", "Missing required parameter: project");
        }

        Integer projectid = null;
        try {
            projectid = Integer.parseInt(project);
        } catch (NumberFormatException nfe) {}

        StoredProject sp = null;

        if (projectid == null) {
            // The project was provided by name
            sp = StoredProject.getProjectByName(project);
        } else {
            sp = StoredProject.loadDAObyId(projectid, StoredProject.class);
        }

        if (sp == null) {
            error("project.notexists", "Project " + project + " does not exist");
        }

        UpdaterService u = AlitheiaCore.getInstance().getUpdater();
        UpdaterStage us = null;
        try {
            us = UpdaterStage.valueOf(UpdaterStage.class, stage);
        } catch (Exception e) {
            warn("updater.stage.notexists",
                    "No such stage " + stage + ":" + e.getMessage());
        }

        boolean scheduled;

        if (updater == null) {
            if (stage == null) {
                scheduled = u.update(sp);
            } else {
                scheduled = u.update(sp, us);
            }
        } else {
            scheduled = u.update(sp, updater);
            if (!scheduled)
                warn("updater.notexists", "No such updater: " + updater);
        }

        if (scheduled)
            finished("Project addded succesfully");
        else
            error("updater.failed", "Could not schedule update jobs");
    }
}
