package eu.sqooss.plugins.javaparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Language;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.JobStateListener;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Parser;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/**
 * Extracts method, function and namespace information and fills in the 
 * corresponding tables in the database, for Java projects.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
@Updater(descr = "Java parser updater", 
        stage = UpdaterStage.PARSE, 
        mnem = "JAVA")
@Parser(languages={Language.JAVA})
public class JavaUpdater implements MetadataUpdater, JobStateListener {
    
    private static final String notProcessed = "select pv " +
    		"from ProjectVersion pv " +
    		"where not exists (" +
    		"  select ns " +
    		"  from NameSpace ns " +
    		"  where ns.changeVersion = pv) " +
    		"and pv.project = :sp";
    
    private StoredProject sp;
    private Logger log;
    private DBService db;
    private float progress;
    private AtomicInteger jobCounter;
    private long numVersions = 1;
    
    public JavaUpdater() {}
   
    public void setUpdateParams(StoredProject sp, Logger l) {
        this.sp = sp;
        this.log = l;
        db = AlitheiaCore.getInstance().getDBService();
        jobCounter = new AtomicInteger();
    }

    public void update() throws Exception {
        db.startDBSession();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sp", sp);
        List<ProjectVersion> toProcess = (List<ProjectVersion>) db.doHQL(notProcessed, params);

        if (toProcess.size() == 0) {
            log.info("No versions to process");
            return;
        }

        Set<Job> jobs = new HashSet<Job>();
        for (ProjectVersion pv : toProcess) {
            JavaUpdaterJob juj = new JavaUpdaterJob(sp, pv, log);
            juj.addJobStateListener(this);
            jobs.add(juj);
        }
        
        numVersions = jobs.size();
        jobCounter.set(jobs.size());
        AlitheiaCore.getInstance().getScheduler().enqueueNoDependencies(jobs);
        
        //Poor man's synchronization
        while (jobCounter.intValue() > 0) {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ignored){}
        }
        
        if (db.isDBSessionActive())db.commitDBSession();
    }

    @Override
    public void jobStateChanged(Job j, State newState) {
        if (newState == State.Error || newState == State.Finished)
            progress = 100 - (float) (((double)jobCounter.decrementAndGet() / (double)numVersions) * 100); 
    }

    @Override
    public int progress() {
        return (int) progress;
    }

    @Override
    public String toString() {
        return "JavaUpdater - Project:{" + sp + "}, " + progress + "%";
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
