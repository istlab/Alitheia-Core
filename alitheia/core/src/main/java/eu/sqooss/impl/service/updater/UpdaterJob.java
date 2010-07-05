package eu.sqooss.impl.service.updater;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.updater.MetadataUpdater;

public class UpdaterJob extends Job {

    MetadataUpdater mu;
    
    public UpdaterJob(MetadataUpdater updater) {
        mu = updater;
    }
    
    @Override
    public int priority() {
        return 0;
    }

    @Override
    protected void run() throws Exception {
        mu.update();
    }
    
    @Override
    public String toString() {
        return mu.toString();
    }
}
