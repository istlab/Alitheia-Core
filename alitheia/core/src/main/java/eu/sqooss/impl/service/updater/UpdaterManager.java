/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,
 *                 Athens, Greece.
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
package eu.sqooss.impl.service.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.sqooss.impl.service.updater.exceptions.MissingAnnotationException;
import eu.sqooss.impl.service.updater.exceptions.MnemonicUsedException;
import eu.sqooss.impl.service.updater.exceptions.UpdaterException;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;
import eu.sqooss.service.util.BidiMap;
/**
 * Updater Manager
 * @author Igor Levaja
 * @author Quinten Stokkink
 */
public class UpdaterManager {
    
	/**
     *  List of registered updaters 
     */
    private BidiMap<Updater, Class<? extends MetadataUpdater>> updaters;
    
    /**
     * Public Constructor
     * Creates Bi-directional Map
     */
    public UpdaterManager() {
    	updaters = new BidiMap<Updater, Class<? extends MetadataUpdater>>(); //from method StartUp()
    }
    
    /**
     * Registering Updater to Manager 
     * @param mu MetadataUpdater class
     * @return Updater, that is Annotation of MetadataUpdater
     * @throws UpdaterException
     */
	public Updater addUpdater(Class<? extends MetadataUpdater> mu) throws UpdaterException {
		
		Updater u = mu.getAnnotation(Updater.class);
		
		if(u == null)
			throw new MissingAnnotationException("Class " + mu + " is missing required annotation" +
            		" @Updater");
		
		 if (getUpdaterByMnemonic(u.mnem()) != null)
			 throw new MnemonicUsedException("Mnemonic already used by updater " 
	                    + updaters.get(getUpdaterByMnemonic(u.mnem())));
		
		updaters.put(u, mu);
		
		return u;
	}
	
	/**
	 * Unregistering updater from manager
	 * @param mu Updater to be unregistered
	 */
	public void removeUpdater(Class<? extends MetadataUpdater> mu) {
		updaters.remove(updaters.getKey(mu));
	}
	
	/**
	 * Getting Collection of Updaters
	 * @return Set of Updaters
	 */
	public Set<Updater> getUpdaters() {
		return updaters.keySet();
	}
	
	/**
	 * Getting MetadataUpdater specified by Updater
	 * @param u Updater
	 * @return MetadataUpdater
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public MetadataUpdater getMetadataUpdater(Updater u) throws InstantiationException, IllegalAccessException {
		return updaters.get(u).newInstance();
	}

    /**
     * Getting MetadataUpdater specified by Mnemonic
     * @param s String (Mnemonic)
     * @return MetadataUpdater
     */
    public Class<? extends MetadataUpdater> getMetadataUpdaterByMnemonic(String s) { 
    	return updaters.get(getUpdaterByMnemonic(s)); }
    
    /**
	 * Getting Updater specified by Mnemonic
	 * @param updater String (Mnemonic)
	 * @return Updater
	 */
    public Updater getUpdaterByMnemonic(String updater) {
        for (Updater upd : updaters.keySet()) {
            if (upd.mnem().equals(updater))
                return upd;
        }
        return null;
    } 
    
    /**
     * Given a protocol, retrieve a list of the corresponding
     * updaters.
     * 
     * @param protocol The protocol to find support for
     * @return The known Updaters that support the protocol
     */
    public List<Updater> getUpdatersByProtocol(String protocol) {
        List<Updater> upds = new ArrayList<Updater>();
        
        for (Updater u : getUpdaters()) {
            for (String p : u.protocols()) {
                if (protocol.equals(p)) {
                    upds.add(u);
                    break;
                }
            }
        }
        
        return upds;
    }
 
    /**
     * Retrieve a list of all known Updaters with a certain stage.
     * 
     * @param u The UpdaterStage to check for
     * @return The known Updaters that have the same UpdaterStage
     */
    public List<Updater> getUpdatersByStage(UpdaterStage u) {
        List<Updater> upds = new ArrayList<Updater>();
       
        for (Updater upd : getUpdaters()) {
            if (upd.stage().equals(u))
                upds.add(upd);
        }
        
        return upds;
    }
}
