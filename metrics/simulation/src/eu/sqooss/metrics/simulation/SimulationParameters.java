/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Kritikos Apostolos <akritiko@csd.auth.gr>
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

package eu.sqooss.metrics.simulation;

import java.util.Vector;

/**
 * SimulationParameters class includes all the information needed for the
 * simulation to be initialized and proceed properly <br>
 * 
 * @author Apostolos Kritikos <a href="mailto:akritiko@csd.auth.gr">(akritiko@csd.auth.gr)</a>
 */

public class SimulationParameters {

	// The Initial Conditions parameter group

	private Integer numberOfModules; // The number of modules for the current
	// project under simulation
	private Vector<Integer> softwarePieces; // Number of software pieces for
	// each module

	// Simulation parameter group

	private Integer simulationSteps;
	private Integer maximumRepetitions;
	private Double timeScale;

	// Behavioral Model Fixed parameters group

	// Programmers parameter group

	private Vector<Double> programmersInterestForSubmissionTasks;
	private Vector<Double> programmersInterestForDebuggingTasks;
	private Vector<Double> programmersInterestForTestingTasks;
	private Vector<Double> programmersInterestForFunctionalImprovementTasks;

	// Tasks parameter group

	private Double releasesFrequencyWeight;
	private Double averageLOCIncrementWeight;
	private Double averageCommitsIncrementWeight;

	// Calibration parameter group (as calibration project we usually define a
	// well known open source project)

	private Double calibrationAverageCommits;
	private Double calibrationAverageReleases;
	private Double calibrationAverageContributors;
	private Integer calibrationAverageLOCIncrement;

	// Behavioral Model Project parameter group

	// A Group
	private Integer numberOfCoreContributors;
	private Double programmersInterestHalfLife; // The half of the time in which
												// a programmer loses his
												// interest for the project
												// under simulation
	private Double programmersInterestHalfLifeStandardDeviation;

	// B Group
	private Double calibrationParameter;

	// C Group
	private Integer averageReleaseTime;

	// Probability Distribution parameter group

	// Lines of code parameter group
	private Vector<Integer> averageLOCAddedToModuleSubmission;
	private Vector<Integer> averageLOCAddedToModuleSubmissionStandardDeviation;
	private Vector<Double> functionallyCompleteModuleMaxLOC;
	private Vector<Double> functionallyCompleteModuleMaxLOCStandardDeviation;
	private Vector<Double> funcImprvTaskAverageLOCAdded; // funcImprv: Functional Improvement
	private Vector<Double> funcImprvTaskAverageLOCAddedStandardDeviation;

	// Bugs parameter group
	private Double initialAverageBugsPerLOC;
	private Double initialAverageBugsPerLOCStandardDeviation;
	private Double averageTimePerLOCProduction;
	private Double averageTimePerLOCProductionStandardDeviation;
	private Double averageTimePerBugFix;
	private Double averageTimePerBugFixStandardDeviation;

	// Tests parameter group
	private Double averageTimePerTestReport;
	private Double averageTimePerTestReportStandardDeviation;

	/**
	 *  
	 * @param numberOfModules
	 * @param softwarePieces
	 * @param simulationSteps
	 * @param maximumRepetitions
	 * @param timeScale
	 * @param programmersInterestForSubmissionTasks
	 * @param programmersInterestForDebuggingTasks
	 * @param programmersInterestForTestingTasks
	 * @param programmersInterestForFunctionalImprovementTasks
	 * @param releasesFrequencyWeight
	 * @param averageLOCIncrementWeight
	 * @param averageCommitsIncrementWeight
	 * @param calibrationAverageCommits
	 * @param calibrationAverageReleases
	 * @param calibrationAverageContributors
	 * @param calibrationAverageLOCIncrement
	 * @param numberOfCoreContributors
	 * @param programmersInterestHalfLife
	 * @param programmersInterestHalfLifeStandardDeviation
	 * @param calibrationParameter
	 * @param averageReleaseTime
	 * @param averageLOCAddedToModuleSubmission
	 * @param averageLOCAddedToModuleSubmissionStandardDeviation
	 * @param functionallyCompleteModuleMaxLOC
	 * @param functionallyCompleteModuleMaxLOCStandardDeviation
	 * @param funcImprvTaskAverageLOCAdded
	 * @param funcImprvTaskAverageLOCAddedStandardDeviation
	 * @param initialAverageBugsPerLOC
	 * @param initialAverageBugsPerLOCStandardDeviation
	 * @param averageTimePerLOCProduction
	 * @param averageTimePerLOCProductionStandardDeviation
	 * @param averageTimePerBugFix
	 * @param averageTimePerBugFixStandardDeviation
	 * @param averageTimePerTestReport
	 * @param averageTimePerTestReportStandardDeviation
	 */
	public SimulationParameters(Integer numberOfModules,
			Vector<Integer> softwarePieces, Integer simulationSteps,
			Integer maximumRepetitions, Double timeScale,
			Vector<Double> programmersInterestForSubmissionTasks,
			Vector<Double> programmersInterestForDebuggingTasks,
			Vector<Double> programmersInterestForTestingTasks,
			Vector<Double> programmersInterestForFunctionalImprovementTasks,
			Double releasesFrequencyWeight, Double averageLOCIncrementWeight,
			Double averageCommitsIncrementWeight,
			Double calibrationAverageCommits,
			Double calibrationAverageReleases,
			Double calibrationAverageContributors,
			Integer calibrationAverageLOCIncrement,
			Integer numberOfCoreContributors,
			Double programmersInterestHalfLife,
			Double programmersInterestHalfLifeStandardDeviation,
			Double calibrationParameter, Integer averageReleaseTime,
			Vector<Integer> averageLOCAddedToModuleSubmission,
			Vector<Integer> averageLOCAddedToModuleSubmissionStandardDeviation,
			Vector<Double> functionallyCompleteModuleMaxLOC,
			Vector<Double> functionallyCompleteModuleMaxLOCStandardDeviation,
			Vector<Double> funcImprvTaskAverageLOCAdded,
			Vector<Double> funcImprvTaskAverageLOCAddedStandardDeviation,
			Double initialAverageBugsPerLOC,
			Double initialAverageBugsPerLOCStandardDeviation,
			Double averageTimePerLOCProduction,
			Double averageTimePerLOCProductionStandardDeviation,
			Double averageTimePerBugFix,
			Double averageTimePerBugFixStandardDeviation,
			Double averageTimePerTestReport,
			Double averageTimePerTestReportStandardDeviation) {
		super();
		this.numberOfModules = numberOfModules;
		this.softwarePieces = softwarePieces;
		this.simulationSteps = simulationSteps;
		this.maximumRepetitions = maximumRepetitions;
		this.timeScale = timeScale;
		this.programmersInterestForSubmissionTasks = programmersInterestForSubmissionTasks;
		this.programmersInterestForDebuggingTasks = programmersInterestForDebuggingTasks;
		this.programmersInterestForTestingTasks = programmersInterestForTestingTasks;
		this.programmersInterestForFunctionalImprovementTasks = programmersInterestForFunctionalImprovementTasks;
		this.releasesFrequencyWeight = releasesFrequencyWeight;
		this.averageLOCIncrementWeight = averageLOCIncrementWeight;
		this.averageCommitsIncrementWeight = averageCommitsIncrementWeight;
		this.calibrationAverageCommits = calibrationAverageCommits;
		this.calibrationAverageReleases = calibrationAverageReleases;
		this.calibrationAverageContributors = calibrationAverageContributors;
		this.calibrationAverageLOCIncrement = calibrationAverageLOCIncrement;
		this.numberOfCoreContributors = numberOfCoreContributors;
		this.programmersInterestHalfLife = programmersInterestHalfLife;
		this.programmersInterestHalfLifeStandardDeviation = programmersInterestHalfLifeStandardDeviation;
		this.calibrationParameter = calibrationParameter;
		this.averageReleaseTime = averageReleaseTime;
		this.averageLOCAddedToModuleSubmission = averageLOCAddedToModuleSubmission;
		this.averageLOCAddedToModuleSubmissionStandardDeviation = averageLOCAddedToModuleSubmissionStandardDeviation;
		this.functionallyCompleteModuleMaxLOC = functionallyCompleteModuleMaxLOC;
		this.functionallyCompleteModuleMaxLOCStandardDeviation = functionallyCompleteModuleMaxLOCStandardDeviation;
		this.funcImprvTaskAverageLOCAdded = funcImprvTaskAverageLOCAdded;
		this.funcImprvTaskAverageLOCAddedStandardDeviation = funcImprvTaskAverageLOCAddedStandardDeviation;
		this.initialAverageBugsPerLOC = initialAverageBugsPerLOC;
		this.initialAverageBugsPerLOCStandardDeviation = initialAverageBugsPerLOCStandardDeviation;
		this.averageTimePerLOCProduction = averageTimePerLOCProduction;
		this.averageTimePerLOCProductionStandardDeviation = averageTimePerLOCProductionStandardDeviation;
		this.averageTimePerBugFix = averageTimePerBugFix;
		this.averageTimePerBugFixStandardDeviation = averageTimePerBugFixStandardDeviation;
		this.averageTimePerTestReport = averageTimePerTestReport;
		this.averageTimePerTestReportStandardDeviation = averageTimePerTestReportStandardDeviation;
	}

	/**
	 * @return the number of modules for the project under simulation
	 */
	public Integer getNumberOfModules() {
		return numberOfModules;
	}

	/**
	 * @param numberOfModules
	 *            the number of modules to set
	 */
	public void setNumberOfModules(Integer numberOfModules) {
		this.numberOfModules = numberOfModules;
	}

	/**
	 * @return a vector of integers containing the software pieces for each module
	 */
	public Vector<Integer> getSoftwarePieces() {
		return softwarePieces;
	}

	/**
	 * @param softwarePieces
	 *            the vector of integers containing the software pieces for each module to set
	 */
	public void setSoftwarePieces(Vector<Integer> softwarePieces) {
		this.softwarePieces = softwarePieces;
	}

	/**
	 * @return the number of simulation steps
	 */
	public Integer getSimulationSteps() {
		return simulationSteps;
	}

	/**
	 * @param simulationSteps
	 *            the number of simulation steps to set
	 */
	public void setSimulationSteps(Integer simulationSteps) {
		this.simulationSteps = simulationSteps;
	}

	/**
	 * @return the number of maximum repetitions
	 */
	public Integer getMaximumRepetitions() {
		return maximumRepetitions;
	}

	/**
	 * @param maximumRepetitions
	 *            the number of maximum repetitions to set
	 */
	public void setMaximumRepetitions(Integer maximumRepetitions) {
		this.maximumRepetitions = maximumRepetitions;
	}

	/**
	 * @return the time scale (in days)
	 */
	public Double getTimeScale() {
		return timeScale;
	}

	/**
	 * @param timeScale
	 *            the time scale (in days) to set
	 */
	public void setTimeScale(Double timeScale) {
		this.timeScale = timeScale;
	}

	/**
	 * @return a vector of doubles containing each each programmers interest for submission tasks
	 */
	public Vector<Double> getProgrammersInterestForSubmissionTasks() {
		return programmersInterestForSubmissionTasks;
	}

	/**
	 * @param programmersInterestForSubmissionTasks
	 *            the vector of doubles containing each each programmers interest for submission tasks to set
	 */
	public void setProgrammersInterestForSubmissionTasks(
			Vector<Double> programmersInterestForSubmissionTasks) {
		this.programmersInterestForSubmissionTasks = programmersInterestForSubmissionTasks;
	}

	/**
	 * @return a vector of doubles containing each each programmers interest for debugging tasks
	 */
	public Vector<Double> getProgrammersInterestForDebuggingTasks() {
		return programmersInterestForDebuggingTasks;
	}

	/**
	 * @param programmersInterestForDebuggingTasks
	 *            the vector of doubles containing each each programmers interest for debugging tasks to set
	 */
	public void setProgrammersInterestForDebuggingTasks(
			Vector<Double> programmersInterestForDebuggingTasks) {
		this.programmersInterestForDebuggingTasks = programmersInterestForDebuggingTasks;
	}

	/**
	 * @return a vector of doubles containing each each programmers interest for testing tasks
	 */
	public Vector<Double> getProgrammersInterestForTestingTasks() {
		return programmersInterestForTestingTasks;
	}

	/**
	 * @param programmersInterestForTestingTasks
	 *            the vector of doubles containing each each programmers interest for testing tasks to set
	 */
	public void setProgrammersInterestForTestingTasks(
			Vector<Double> programmersInterestForTestingTasks) {
		this.programmersInterestForTestingTasks = programmersInterestForTestingTasks;
	}

	/**
	 * @return a vector of doubles containing each each programmers interest for functional improvement tasks
	 */
	public Vector<Double> getProgrammersInterestForFunctionalImprovementTasks() {
		return programmersInterestForFunctionalImprovementTasks;
	}

	/**
	 * @param programmersInterestForFunctionalImprovementTasks
	 *            the vector of doubles containing each each programmers interest for functional improvement tasks to set
	 */
	public void setProgrammersInterestForFunctionalImprovementTasks(
			Vector<Double> programmersInterestForFunctionalImprovementTasks) {
		this.programmersInterestForFunctionalImprovementTasks = programmersInterestForFunctionalImprovementTasks;
	}

	/**
	 * @return the weight for releases frequency of the project under simulation
	 */
	public Double getReleasesFrequencyWeight() {
		return releasesFrequencyWeight;
	}

	/**
	 * @param releasesFrequencyWeight
	 *            the weight for releases frequency of the project under simulation to set
	 */
	public void setReleasesFrequencyWeight(Double releasesFrequencyWeight) {
		this.releasesFrequencyWeight = releasesFrequencyWeight;
	}

	/**
	 * @return the weight for lines of code increment in average
	 */
	public Double getAverageLOCIncrementWeight() {
		return averageLOCIncrementWeight;
	}

	/**
	 * @param averageLOCIncrementWeight
	 *            the weight for lines of code increment in average to set
	 */
	public void setAverageLOCIncrementWeight(Double averageLOCIncrementWeight) {
		this.averageLOCIncrementWeight = averageLOCIncrementWeight;
	}

	/**
	 * @return the weight for commit increment in average
	 */
	public Double getAverageCommitsIncrementWeight() {
		return averageCommitsIncrementWeight;
	}

	/**
	 * @param averageCommitsIncrementWeight
	 *            the weight for commit increment in average to set
	 */
	public void setAverageCommitsIncrementWeight(
			Double averageCommitsIncrementWeight) {
		this.averageCommitsIncrementWeight = averageCommitsIncrementWeight;
	}

	/**
	 * @return the number of commits (in average) for the calibration project
	 */
	public Double getCalibrationAverageCommits() {
		return calibrationAverageCommits;
	}

	/**
	 * @param calibrationAverageCommits
	 *            the number of commits (in average) for the calibration project to set
	 */
	public void setCalibrationAverageCommits(Double calibrationAverageCommits) {
		this.calibrationAverageCommits = calibrationAverageCommits;
	}

	/**
	 * @return the number of releases (in average) for the calibration project
	 */
	public Double getCalibrationAverageReleases() {
		return calibrationAverageReleases;
	}

	/**
	 * @param calibrationAverageReleases
	 *            the number of releases (in average) for the calibration project to set
	 */
	public void setCalibrationAverageReleases(Double calibrationAverageReleases) {
		this.calibrationAverageReleases = calibrationAverageReleases;
	}

	/**
	 * @return the number of contributors (in average) for the calibration project
	 */
	public Double getCalibrationAverageContributors() {
		return calibrationAverageContributors;
	}

	/**
	 * @param calibrationAverageContributors
	 *            the number of contributors (in average) for the calibration project to set
	 */
	public void setCalibrationAverageContributors(
			Double calibrationAverageContributors) {
		this.calibrationAverageContributors = calibrationAverageContributors;
	}

	/**
	 * @return the lines of code increment (in average) for the calibration project
	 */
	public Integer getCalibrationAverageLOCIncrement() {
		return calibrationAverageLOCIncrement;
	}

	/**
	 * @param calibrationAverageLOCIncrement
	 *            the lines of code increment (in average) for the calibration project to set
	 */
	public void setCalibrationAverageLOCIncrement(
			Integer calibrationAverageLOCIncrement) {
		this.calibrationAverageLOCIncrement = calibrationAverageLOCIncrement;
	}

	/**
	 * @return the number of core contributors 
	 */
	public Integer getNumberOfCoreContributors() {
		return numberOfCoreContributors;
	}

	/**
	 * @param numberOfCoreContributors
	 *            the number of core contributors to set
	 */
	public void setNumberOfCoreContributors(Integer numberOfCoreContributors) {
		this.numberOfCoreContributors = numberOfCoreContributors;
	}

	/**
	 * @return the half life of time in which a programmer's interest for the project becomes zero
	 */
	public Double getProgrammersInterestHalfLife() {
		return programmersInterestHalfLife;
	}

	/**
	 * @param programmersInterestHalfLife
	 *            the half life of time in which a programmer's interest for the project becomes zero to set
	 */
	public void setProgrammersInterestHalfLife(
			Double programmersInterestHalfLife) {
		this.programmersInterestHalfLife = programmersInterestHalfLife;
	}

	/**
	 * @return the standard deviation of the half life of time in which a programmer's interest for the project becomes zero
	 */
	public Double getProgrammersInterestHalfLifeStandardDeviation() {
		return programmersInterestHalfLifeStandardDeviation;
	}

	/**
	 * @param programmersInterestHalfLifeStandardDeviation
	 *            the standard deviation of the half life of time in which a programmer's interest for the project becomes zero to set
	 */
	public void setProgrammersInterestHalfLifeStandardDeviation(
			Double programmersInterestHalfLifeStandardDeviation) {
		this.programmersInterestHalfLifeStandardDeviation = programmersInterestHalfLifeStandardDeviation;
	}

	/**
	 * @return the calibration parameter for the project under simulation
	 */
	public Double getCalibrationParameter() {
		return calibrationParameter;
	}

	/**
	 * @param calibrationParameter
	 *            the calibration parameter for the project under simulation to set
	 */
	public void setCalibrationParameter(Double calibrationParameter) {
		this.calibrationParameter = calibrationParameter;
	}

	/**
	 * @return the average release time
	 */
	public Integer getAverageReleaseTime() {
		return averageReleaseTime;
	}

	/**
	 * @param averageReleaseTime
	 *            the average release time to set
	 */
	public void setAverageReleaseTime(Integer averageReleaseTime) {
		this.averageReleaseTime = averageReleaseTime;
	}

	/**
	 * @return the lines of code added to a module as a result of a submission task
	 */
	public Vector<Integer> getAverageLOCAddedToModuleSubmission() {
		return averageLOCAddedToModuleSubmission;
	}

	/**
	 * @param averageLOCAddedToModuleSubmission
	 *            the lines of code added to a module as a result of a submission task to set
	 */
	public void setAverageLOCAddedToModuleSubmission(
			Vector<Integer> averageLOCAddedToModuleSubmission) {
		this.averageLOCAddedToModuleSubmission = averageLOCAddedToModuleSubmission;
	}

	/**
	 * @return the standard deviation of the lines of code added to a module as a result of a submission task
	 */
	public Vector<Integer> getAverageLOCAddedToModuleSubmissionStandardDeviation() {
		return averageLOCAddedToModuleSubmissionStandardDeviation;
	}

	/**
	 * @param averageLOCAddedToModuleSubmissionStandardDeviation
	 *            the standard deviation of the lines of code added to a module as a result of a submission task to set
	 */
	public void setAverageLOCAddedToModuleSubmissionStandardDeviation(
			Vector<Integer> averageLOCAddedToModuleSubmissionStandardDeviation) {
		this.averageLOCAddedToModuleSubmissionStandardDeviation = averageLOCAddedToModuleSubmissionStandardDeviation;
	}

	/**
	 * @return the maximum number of lines of code for a functionally completed module
	 */
	public Vector<Double> getFunctionallyCompleteModuleMaxLOC() {
		return functionallyCompleteModuleMaxLOC;
	}

	/**
	 * @param functionallyCompleteModuleMaxLOC
	 *            the maximum number of lines of code for a functionally completed module to set
	 */
	public void setFunctionallyCompleteModuleMaxLOC(
			Vector<Double> functionallyCompleteModuleMaxLOC) {
		this.functionallyCompleteModuleMaxLOC = functionallyCompleteModuleMaxLOC;
	}

	/**
	 * @return the standard deviation of maximum number of lines of code for a functionally completed module
	 */
	public Vector<Double> getFunctionallyCompleteModuleMaxLOCStandardDeviation() {
		return functionallyCompleteModuleMaxLOCStandardDeviation;
	}

	/**
	 * @param functionallyCompleteModuleMaxLOCStandardDeviation
	 *            the standard deviation of maximum number of lines of code for a functionally completed module to set
	 */
	public void setFunctionallyCompleteModuleMaxLOCStandardDeviation(
			Vector<Double> functionallyCompleteModuleMaxLOCStandardDeviation) {
		this.functionallyCompleteModuleMaxLOCStandardDeviation = functionallyCompleteModuleMaxLOCStandardDeviation;
	}

	/**
	 * @return the lines of code added to a module as a result of a functional improvement task
	 */
	public Vector<Double> getFuncImprvTaskAverageLOCAdded() {
		return funcImprvTaskAverageLOCAdded;
	}

	/**
	 * @param funcImprvTaskAverageLOCAdded
	 *            the lines of code added to a module as a result of a functional improvement task to set
	 */
	public void setFuncImprvTaskAverageLOCAdded(
			Vector<Double> funcImprvTaskAverageLOCAdded) {
		this.funcImprvTaskAverageLOCAdded = funcImprvTaskAverageLOCAdded;
	}

	/**
	 * @return the standard deviation of the lines of code added to a module as a result of a functional improvement task
	 */
	public Vector<Double> getFuncImprvTaskAverageLOCAddedStandardDeviation() {
		return funcImprvTaskAverageLOCAddedStandardDeviation;
	}

	/**
	 * @param funcImprvTaskAverageLOCAddedStandardDeviation
	 *            the standard deviation of the lines of code added to a module as a result of a functional improvement task to set
	 */
	public void setFuncImprvTaskAverageLOCAddedStandardDeviation(
			Vector<Double> funcImprvTaskAverageLOCAddedStandardDeviation) {
		this.funcImprvTaskAverageLOCAddedStandardDeviation = funcImprvTaskAverageLOCAddedStandardDeviation;
	}

	/**
	 * @return the number of initial bugs per line of code 
	 */
	public Double getInitialAverageBugsPerLOC() {
		return initialAverageBugsPerLOC;
	}

	/**
	 * @param initialAverageBugsPerLOC
	 *            the number of initial bugs per line of code to set
	 */
	public void setInitialAverageBugsPerLOC(Double initialAverageBugsPerLOC) {
		this.initialAverageBugsPerLOC = initialAverageBugsPerLOC;
	}

	/**
	 * @return the standard deviation of the number of initial bugs per line of code 
	 */
	public Double getInitialAverageBugsPerLOCStandardDeviation() {
		return initialAverageBugsPerLOCStandardDeviation;
	}

	/**
	 * @param initialAverageBugsPerLOCStandardDeviation
	 *            the standard deviation of the number of initial bugs per line of code  to set
	 */
	public void setInitialAverageBugsPerLOCStandardDeviation(
			Double initialAverageBugsPerLOCStandardDeviation) {
		this.initialAverageBugsPerLOCStandardDeviation = initialAverageBugsPerLOCStandardDeviation;
	}

	/**
	 * @return the average time needed for a line of code to be produced
	 */
	public Double getAverageTimePerLOCProduction() {
		return averageTimePerLOCProduction;
	}

	/**
	 * @param averageTimePerLOCProduction
	 *            the average time needed for a line of code to be produced to set
	 */
	public void setAverageTimePerLOCProduction(
			Double averageTimePerLOCProduction) {
		this.averageTimePerLOCProduction = averageTimePerLOCProduction;
	}

	/**
	 * @return the standard deviation of the time needed for a line of code to be produced
	 */
	public Double getAverageTimePerLOCProductionStandardDeviation() {
		return averageTimePerLOCProductionStandardDeviation;
	}

	/**
	 * @param averageTimePerLOCProductionStandardDeviation
	 *            the standard deviation of the time needed for a line of code to be produced, to set
	 */
	public void setAverageTimePerLOCProductionStandardDeviation(
			Double averageTimePerLOCProductionStandardDeviation) {
		this.averageTimePerLOCProductionStandardDeviation = averageTimePerLOCProductionStandardDeviation;
	}

	/**
	 * @return the average time needed in order for a bug to be fixed
	 */
	public Double getAverageTimePerBugFix() {
		return averageTimePerBugFix;
	}

	/**
	 * @param averageTimePerBugFix
	 *            the average time needed in order for a bug to be fixed, to set
	 */
	public void setAverageTimePerBugFix(Double averageTimePerBugFix) {
		this.averageTimePerBugFix = averageTimePerBugFix;
	}

	/**
	 * @return the standard deviation of the time needed in order for a bug to be fixed
	 */
	public Double getAverageTimePerBugFixStandardDeviation() {
		return averageTimePerBugFixStandardDeviation;
	}

	/**
	 * @param averageTimePerBugFixStandardDeviation
	 *            the standard deviation of the time needed in order for a bug to be fixed, to set
	 */
	public void setAverageTimePerBugFixStandardDeviation(
			Double averageTimePerBugFixStandardDeviation) {
		this.averageTimePerBugFixStandardDeviation = averageTimePerBugFixStandardDeviation;
	} 

	/**
	 * @return the average time needed in order for a test report to be submitted
	 */
	public Double getAverageTimePerTestReport() {
		return averageTimePerTestReport;
	}

	/**
	 * @param averageTimePerTestReport
	 *            the average time needed in order for a test report to be submitted, to set
	 */
	public void setAverageTimePerTestReport(Double averageTimePerTestReport) {
		this.averageTimePerTestReport = averageTimePerTestReport;
	}

	/**
	 * @return the standard deviation of the time needed in order for a test report to be submitted
	 */
	public Double getAverageTimePerTestReportStandardDeviation() {
		return averageTimePerTestReportStandardDeviation;
	}

	/**
	 * @param averageTimePerTestReportStandardDeviation
	 *            the standard deviation of the time needed in order for a test report to be submitted, to set
	 */
	public void setAverageTimePerTestReportStandardDeviation(
			Double averageTimePerTestReportStandardDeviation) {
		this.averageTimePerTestReportStandardDeviation = averageTimePerTestReportStandardDeviation;
	}
}
