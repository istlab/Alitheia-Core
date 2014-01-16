package eu.sqooss.impl.service.updater;

import java.util.Set;

import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.util.BidiMap;

public class UpdaterManager {
    /* List of registered updaters */
    private BidiMap<Updater, Class<? extends MetadataUpdater>> updaters;
    
    public UpdaterManager() {
    	updaters = new BidiMap<Updater, Class<? extends MetadataUpdater>>(); //from method StartUp()
    }
    
	public Updater addUpdater(Class<? extends MetadataUpdater> mu) throws UpdaterException {
		
		Updater u = mu.getAnnotation(Updater.class);
		
		if(u == null)
			throw new MissingAnnotation("Class " + mu + " is missing required annotation" +
            		" @Updater");
		
		 if (getUpdaterByMnemonic(u.mnem()) != null)
			 throw new MnemonicUsed("Mnemonic already used by updater " 
	                    + updaters.get(getUpdaterByMnemonic(u.mnem())));
		
		updaters.put(u, mu);
		
		return u;
	}
	
	public void removeUpdater(Class<? extends MetadataUpdater> mu) {
		updaters.remove(updaters.getKey(mu));
	}
	
	public Set<Updater> getUpdaters() {
		return updaters.keySet();
	}
	
	public MetadataUpdater getMetadataUpdater(Updater u) throws InstantiationException, IllegalAccessException {
		return updaters.get(u).newInstance();
	}
	
    public Updater getUpdaterByMnemonic(String updater) {
        for (Updater upd : updaters.keySet()) {
            if (upd.mnem().equals(updater))
                return upd;
        }
        return null;
    }
}
