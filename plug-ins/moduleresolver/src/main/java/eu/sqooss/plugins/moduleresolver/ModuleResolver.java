package eu.sqooss.plugins.moduleresolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

@Updater(descr = "Source code module resolver",
        stage = UpdaterStage.INFERENCE, 
        mnem = "SRCMODRES")
public class ModuleResolver implements MetadataUpdater {

    private static final String notProcessed = "select distinct(pv) " +
    		"from ProjectFile pf, ProjectVersion pv " +
    		"where pf.projectVersion = pv and pf.module is null " +
    		"and pv.project = :sp";

    private StoredProject sp;
    private Logger log;
    private DBService db;
    private float progress;
    
    
    @Override
    public int progress() {
        return (int) progress;
    }

    public void setUpdateParams(StoredProject sp, Logger l) {
        this.sp = sp;
        this.log = l;
        db = AlitheiaCore.getInstance().getDBService();
    }

    @Override
    public void update() throws Exception {
        db.startDBSession();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sp", sp);
        List<ProjectVersion> toProcess = (List<ProjectVersion>) db.doHQL(notProcessed, params);

        if (toProcess.size() == 0) {
            log.info("No versions to process");
            return;
        }

        int i = 0;
        for (ProjectVersion pv : toProcess) {
            i ++;
            if (!db.isDBSessionActive()) db.startDBSession();
            pv = db.attachObjectToDBSession(pv);
            log.info("ModuleResolver: Processing version: " + pv);
            for (ProjectFile pf : pv.allDirs()) {

                List<ProjectFile> pfs = pf.getProjectVersion().getFiles(
                        Directory.getDirectory(pf.getFileName(), false),
                        ProjectVersion.MASK_FILES);

                FileTypeMatcher ftm = FileTypeMatcher.getInstance();
                for (ProjectFile f : pfs) {

                    if ((pf.isModule() == null || pf.isModule() == false) &&
                            ftm.getFileType(f.getName()) == FileTypeMatcher.FileType.SRC) {
                        log.debug("ModuleResolver: Source code module: " + pf);
                        pf.setModule(true);
                    }
                    f.setModule(false);
                }
                if (pf.isModule() == null)
                    pf.setModule(false);
            }
            progress = ((float)i / (float)toProcess.size());
            db.commitDBSession();
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ModuleResolver");
        sb.append(":").append(sp).append(":").append(progress * 100).append("%");
        return sb.toString();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
