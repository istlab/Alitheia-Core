/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Kritikos Apostolos <akritiko@csd.auth.gr>
 *
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

package eu.sqooss.impl.metrics.simulation;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.simulation.Simulation;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.util.Pair;
import java.sql.SQLException;

import eu.sqooss.service.db.Metric;
import eu.sqooss.service.abstractmetric.ResultEntry;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.service.db.SimulationResultsMax;
import eu.sqooss.service.db.SimulationResultsMin;
import eu.sqooss.service.db.SimulationResultsChi;
import eu.sqooss.service.db.SimulationResultsSquareChi;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

public class SimulationImplementation extends AbstractMetric implements
		Simulation {
	public SimulationImplementation(BundleContext bc) {
		super(bc);
	}

	private List<Class<? extends DAObject>> activationTypes;

	public boolean install() {
		boolean result = super.install();

		return result;
	}

	public boolean remove() {

		return super.remove();
	}

	public boolean update() {

		return remove() && install();
	}

	public List<ResultEntry> getResult(ProjectFile a, Metric m) {
		List<ResultEntry> results = null;

		return results;
	}

	public void run(ProjectFile a) {
		SimulationJob w = null;
		try {
			w = new SimulationJob(this);

			ServiceReference serviceRef = null;
			serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
			Scheduler s = ((AlitheiaCore) bc.getService(serviceRef))
					.getScheduler();
			s.enqueue(w);
		} catch (Exception e) {
			log.error("Could not schedule " + w.getClass().getName()
					+ " for project file: " + ((ProjectFile) a).getFileName());
		}
	}
}

// vi: ai nosi sw=4 ts=4 expandtab

