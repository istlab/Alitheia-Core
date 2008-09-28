/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by A.U.TH (author: Kritikos Apostolos <akritiko@csd.auth.gr>)
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

import org.osgi.framework.BundleContext;

import java.util.List;
import java.util.ArrayList;

import eu.sqooss.metrics.simulation.Simulation;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.StoredProject;

import eu.sqooss.service.pa.PluginInfo;

public class SimulationImplementation extends AbstractMetric implements
		Simulation {
	
	public static final String CONFIG_SIMULATION_STEPS = "Simulation steps";
    public static final String CONFIG_NUMBER_OF_REPETITIONS = "Number of repetitions";
    public static final String CONFIG_TIME_SCALE = "Time scale";
	
	public SimulationImplementation(BundleContext bc) {
		super(bc);
		super.addActivationType(StoredProject.class);
		super.addMetricActivationType("SIMUL", StoredProject.class);
	}

	public boolean install() {
		boolean result = super.install();
		if (result) {
			result &= super.addSupportedMetrics("Simulation Metric", "SIMU",
					MetricType.Type.PROJECT_WIDE);
		
			addConfigEntry(CONFIG_SIMULATION_STEPS, 
	                 "500" , 
	                 "Number of simulation steps", 
	                 PluginInfo.ConfigurationType.INTEGER);	
		}
		return result;
	}

	public boolean remove() {	
		boolean result = true;
		result &= super.remove();
		return result;
	}

	public boolean update() {
		return remove() && install();
	}

	public List<ResultEntry> getResult(StoredProject sp, Metric m) {

		ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
		return results;
	}

	public void run(StoredProject sp) {

	}
}
