package eu.sqooss.impl.service.updater;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.updater.Updater;

/**
 * A thread safe schedule of UpdaterJobs to Updaters for projects.
 * 
 * @author Igor Levaja
 * @author Quinten Stokkink
 */
public class UpdateScheduler {
	
	/**
	 * The container for UpdaterJob to Updater mappings to projects
	 */
	private ConcurrentMap<Long,Map<Updater, UpdaterJob>> scheduledUpdates;
	
	/**
	 * Create a new scheduler
	 */
	public UpdateScheduler(){
		scheduledUpdates = new ConcurrentHashMap<Long, Map<Updater, UpdaterJob>>();
	}
	
	/**
	 * Retrieve the Updater -> UpdateJob mapping for a certain project
	 * 
	 * @param sp The StoredProject to retrieve the schedule of
	 * @return The Updater to UpdateJob mapping for this project
	 */
	public Map<Updater, UpdaterJob> getScheduleFor(StoredProject sp){
		return getScheduleFor(sp.getId());
	}
	
	/**
	 * Retrieve the Updater -> UpdateJob mapping for a certain project
	 * 
	 * @param projectid The project id to retrieve the schedule of
	 * @return The Updater to UpdateJob mapping for this project
	 */
	public Map<Updater, UpdaterJob> getScheduleFor(long projectid){
		return scheduledUpdates.get(projectid);
	}
	
	/**
	 * Check if a schedule has been created for a certain project
	 * 
	 * @param sp The StoredProject to check for a schedule
	 * @return True iff there is a schedule for this project
	 */
	public boolean scheduleExists(StoredProject sp){
		return scheduleExists(sp.getId());
	}
	
	/**
	 * Check if a schedule has been created for a certain project
	 * 
	 * @param projectid The project id to check for a schedule
	 * @return True iff there is a schedule for this project
	 */
	public boolean scheduleExists(long projectid){
		return scheduledUpdates.containsKey(projectid);
	}
	
	/**
	 * Retrieve the UpdaterJob associated with a certain 
	 * Updater for a certain project.
	 * 
	 * @param sp The project the UpdaterJob belongs to
	 * @param u The Updater the UpdaterJob belongs to
	 * @return The UpdaterJob belonging to the specified project and updater
	 */
	public UpdaterJob getJobFor(StoredProject sp, Updater u){
		return getScheduleFor(sp).get(u);
	}
	
	/**
	 * Get all the scheduled jobs for a certain project.
	 * 
	 * @param sp The project to retrieve all the jobs for
	 * @return A collection of UpdaterJobs for this project
	 */
	public Collection<UpdaterJob> getJobsFor(StoredProject sp){
		return getScheduleFor(sp).values();
	}
	
	/**
	 * Create a new schedule for this project if it does not exist.
	 * 
	 * @param sp The project to initialize
	 */
	private void initialize(StoredProject sp){
		if (!scheduledUpdates.containsKey(sp.getId()))
            scheduledUpdates.put(sp.getId(), new HashMap<Updater, UpdaterJob>());
	}
	
	/**
	 * Add an Updater + UpdaterJob combination for a
	 * certain project.
	 * 
	 * @param sp The project the couple is associated with
	 * @param u The Updater to add
	 * @param job The UpdaterJob that is coupled to the Updater
	 */
	public void addUpdater(StoredProject sp, Updater u, UpdaterJob job){
		initialize(sp);
		getScheduleFor(sp).put(u, job);
	}
}
