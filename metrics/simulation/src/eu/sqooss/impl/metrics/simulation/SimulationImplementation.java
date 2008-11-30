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

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

//import eu.sqooss.core.AlitheiaCore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;

import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginInfo;

import eu.sqooss.metrics.simulation.SimulationMetric;

import eu.sqooss.metrics.simulation.SimulationXMLParser;
import eu.sqooss.metrics.simulation.SimulationParameters;
import eu.sqooss.metrics.simulation.SimulationModel;
import eu.sqooss.metrics.simulation.db.SimulationResults;
import eu.sqooss.metrics.simulation.db.SimulationMin;
import eu.sqooss.metrics.simulation.db.SimulationMax;
import eu.sqooss.metrics.simulation.db.SimulationChi;

public class SimulationImplementation extends AbstractMetric implements
		SimulationMetric {

	public static final String CONFIG_IC_NOF_SIMULATION_STEPS = "NOF_Simulation_Steps";
	public static final String CONFIG_IC_NOF_REPETITIONS = "NOF_Repetitions";
	public static final String CONFIG_IC_TIME_SCALE = "Time_scale";
	public static final String CONFIG_BMFIXED_TASKS_RELEASES_WEIGHT = "Releases weight";
	public static final String CONFIG_BMFIXED_TASKS_LOC_INCREMENT_WEIGHT = "LOC increment weight";
	public static final String CONFIG_BMFIXED_TASKS_COMMITS_WEIGHT = "Commits weight";
	public static final String CONFIG_BMFIXED_CALIBRATION_COMMITS = "Calibration commits";
	public static final String CONFIG_BMFIXED_CALIBRATION_RELEASES = "Calibration releases";
	public static final String CONFIG_BMFIXED_CALIBRATION_CONTRIBUTORS = "Calibration contributors";
	public static final String CONFIG_BMFIXED_CALIBRATION_LOC_INCREMENT = "Calibration LOC increment";
	public static final String CONFIG_BMPROJECT_A_NCORE = "NOF core contributors";
	public static final String CONFIG_BMPROJECT_A_S0 = "S0";
	public static final String CONFIG_BMPROJECT_A_STDEV_S0 = "S0 Standard Deviation";
	public static final String CONFIG_BMPROJECT_B_QZERO = "Q0";
	public static final String CONFIG_BMPROJECT_C_PR_RELEASE_INTERVAL = "Project Release Interval";
	public static final String CONFIG_PD_BUGS_MEAN = "Mean NOF Bugs";
	public static final String CONFIG_PD_BUGS_STDEV = "The standard deviation of the previous variable";
	public static final String CONFIG_PD_BUGS_S_MEAN = "Mean NOF days for a LOC submission";
	public static final String CONFIG_PD_BUGS_S_STDEV = "The standard deviation of the previous variable";
	public static final String CONFIG_PD_BUGS_BUGFIX_TIME_MEAN = "Mean NOF days for a bugfix submission";
	public static final String CONFIG_PD_BUGS_BUGFIX_TIME_STDEV = "The standard deviation of the previous variable";
	public static final String CONFIG_PD_TESTS_TESTING_TIME_MEAN = "Mean NOF days for a test report";
	public static final String CONFIG_PD_TESTS_TESTING_TIME_STDEV = "The standard deviation of the previous variable";

	public SimulationImplementation(BundleContext bc) {
		super(bc);
		super.addActivationType(StoredProject.class);
		super.addMetricActivationType("SIMULATION", StoredProject.class);
	}

	public boolean install() {
		boolean result = true;

		// Installing...
		result &= super.install();
		if (result) {

			result &= super.addSupportedMetrics("Simulation Metric", "SIMU",
					MetricType.Type.PROJECT_WIDE);

			addConfigEntry(CONFIG_IC_NOF_SIMULATION_STEPS, "50",
					"Number of simulation steps",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(CONFIG_IC_NOF_REPETITIONS, "5",
					"Number of repetitions",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(CONFIG_IC_TIME_SCALE, "0.1", "Time scale",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMFIXED_TASKS_RELEASES_WEIGHT, "0.4",
					"Releases' weight", PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMFIXED_TASKS_LOC_INCREMENT_WEIGHT, "0.4",
					"Lines' of code weight",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMFIXED_TASKS_COMMITS_WEIGHT, "0.2",
					"Commits' weight", PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMFIXED_CALIBRATION_COMMITS, "10.0",
					"Average commits per day for the calibration project",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMFIXED_CALIBRATION_RELEASES, "220.0",
					"Average releases per day for the calibration project",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(
					CONFIG_BMFIXED_CALIBRATION_CONTRIBUTORS,
					"25.0",
					"Average number of contributors for the calibration project",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(
					CONFIG_BMFIXED_CALIBRATION_LOC_INCREMENT,
					"220",
					"Average increment of lines of code per day for the calibration project",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(CONFIG_BMPROJECT_A_NCORE, "7",
					"Number of core contributors of the project",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(
					CONFIG_BMPROJECT_A_S0,
					"246.0",
					"Halflife (in mandays) in which the interest of a programmer for the project becomes zero",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMPROJECT_A_STDEV_S0, "213.0",
					"The standard deviation of the previous variable",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_BMPROJECT_B_QZERO, "1",
					"Total calibration value",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(CONFIG_BMPROJECT_C_PR_RELEASE_INTERVAL, "60",
					"Meantime (in days) of a new release",
					PluginInfo.ConfigurationType.INTEGER);

			addConfigEntry(CONFIG_PD_BUGS_MEAN, "0.004",
					"Initial mean bugs per line of code",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_BUGS_STDEV, "0.003",
					"The standard deviation of the previous variable",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_BUGS_S_MEAN, "0.0117",
					"Mean number of days for a line of code to be added",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_BUGS_S_STDEV, "0.0387",
					"The standard deviation of the previous variable",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_BUGS_BUGFIX_TIME_MEAN, "1.0",
					"Mean number of days for a bugfix to be posted",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_BUGS_BUGFIX_TIME_STDEV, "3.0",
					"The standard deviation of the previous variable",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(
					CONFIG_PD_TESTS_TESTING_TIME_MEAN,
					"0.1",
					"Mean number of days testing in order for a defect to be reported",
					PluginInfo.ConfigurationType.DOUBLE);

			addConfigEntry(CONFIG_PD_TESTS_TESTING_TIME_STDEV, "0.2",
					"The standard deviation of the previous variable",
					PluginInfo.ConfigurationType.DOUBLE);

			System.out.println("\ninstalled\n");
		}
		return result;
	}

	public boolean remove() {
		boolean result = super.remove();
		return result;
	}
	
	public List<ResultEntry> getResult(StoredProject sp, Metric m) {

		ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
		
		//on behalf of run
		runSimulation();

		return results;
	}
	
	public void run(StoredProject sp) {

		System.out.println("\nRun works\n");
		
	}
	
	public void runSimulation() {

		// File paths for XSD & XML
		String simulationSchemaPath = "../metrics/simulation/src/eu/sqooss/metrics/simulation/xml/SimulationSchema.xsd";
		String simulationXMLPath = "../metrics/simulation/src/eu/sqooss/metrics/simulation/xml/SimulationConfig.xml";

		// Loads default values from the XML file after its validation.
		SimulationXMLParser myParser = new SimulationXMLParser(
				simulationSchemaPath, simulationXMLPath);
		SimulationParameters parameters = myParser.parseSimulation();

		// Runs the simulation
		SimulationModel simulator = new SimulationModel(parameters);
		simulator.simulate();

		// The results of the simulation are being stored in the DB
		LinkedList<SimulationResults> simulationResults = simulator
				.getResultRecords();
		db.addRecords(simulationResults);

		LinkedList<SimulationMin> simulationMinResults = simulator
				.getMinRecords();
		db.addRecords(simulationMinResults);

		LinkedList<SimulationMax> simulationMaxResults = simulator
				.getMaxRecords();
		db.addRecords(simulationMaxResults);

		LinkedList<SimulationChi> simulationChiResults = simulator
				.getChiRecords();
		db.addRecords(simulationChiResults);

	}
}
