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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;
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
	 * Retrieve all known project ids
	 * 
	 * @return All registered project ids
	 */
	public Set<Long> getProjectIds(){
		return scheduledUpdates.keySet();
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
		return getJobFor(sp.getId(), u);
	}
	
	/**
	 * Retrieve the UpdaterJob associated with a certain 
	 * Updater for a certain project.
	 * 
	 * @param projectid The project id the UpdaterJob belongs to
	 * @param u The Updater the UpdaterJob belongs to
	 * @return The UpdaterJob belonging to the specified project and updater
	 */
	public UpdaterJob getJobFor(long projectid, Updater u){
		return getScheduleFor(projectid).get(u);
	}
	
	/**
	 * Get all the scheduled jobs for a certain project.
	 * 
	 * @param sp The project to retrieve all the jobs for
	 * @return A collection of UpdaterJobs for this project
	 */
	public Collection<UpdaterJob> getJobsFor(StoredProject sp){
		return getJobsFor(sp.getId());
	}
	
	/**
	 * Get all the scheduled jobs for a certain project.
	 * 
	 * @param projectid The project id to retrieve all the jobs for
	 * @return A collection of UpdaterJobs for this project
	 */
	public Collection<UpdaterJob> getJobsFor(long projectid){
		initialize(projectid);
		return getScheduleFor(projectid).values();
	}
	
	/**
	 * Retrieve a project id corresponding to a job
	 * 
	 * @param job The job to find a project id for
	 * @return The project id the job belongs to, otherwise null 
	 */
	public Long getProjectFor(Job job){
		Long projectId = null;

        for (Long pid : getProjectIds()) {
            if (getJobsFor(pid).contains(job)) {
                projectId = pid;
                break;
            }
        }
        
        return projectId;
	}
	
	/**
	 * Retrieve the Updater corresponding to a job
	 * 
	 * @param job The job to find the Updater for
	 * @return The Updater the job belongs to, otherwise null 
	 */
	public Updater getUpdaterFor(Job job){
        for (Map<Updater, UpdaterJob> map : scheduledUpdates.values()) {
        	for (Entry<Updater, UpdaterJob> entry : map.entrySet())
        		if (entry.getValue().equals(job)) {
        			return entry.getKey();
            }
        }
        
        return null;
	}
	
	/**
	 * Create a new schedule for this project if it does not exist.
	 * 
	 * @param sp The project to initialize
	 */
	private void initialize(StoredProject sp){
		initialize(sp.getId());
	}
	
	/**
	 * Create a new schedule for this project if it does not exist.
	 * 
	 * @param projectid The project to initialize
	 */
	private void initialize(long projectid){
		if (!scheduledUpdates.containsKey(projectid))
            scheduledUpdates.put(projectid, new HashMap<Updater, UpdaterJob>());
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
