package eu.sqooss.impl.service.updater.test;

import eu.sqooss.impl.service.updater.UpdaterJob;
import eu.sqooss.service.updater.MetadataUpdater;

public class UpdaterJobWrapper extends UpdaterJob{

	public UpdaterJobWrapper(MetadataUpdater updater) {
		super(updater);
	}

	public void run() throws Exception{
		super.run();
	}
	
}
