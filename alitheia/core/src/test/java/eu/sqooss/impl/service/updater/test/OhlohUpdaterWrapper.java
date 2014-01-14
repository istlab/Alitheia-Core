package eu.sqooss.impl.service.updater.test;

import static org.mockito.Mockito.*;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.updater.OhlohUpdater;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

public class OhlohUpdaterWrapper extends OhlohUpdater{

	@Override
	public void run() throws Exception{
		super.run();
	}
	
	@Override
	public void setUpdateParams(StoredProject sp, Logger l) {
        this.project = sp;
        this.logger = l;
        dbs = mock(DBService.class);
    }
	
	public DBService getMockedDBService(){
		return dbs;
	}
}
