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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;

import eu.sqooss.metrics.simulation.db.SimulationResults;
import eu.sqooss.metrics.simulation.db.SimulationMin;
import eu.sqooss.metrics.simulation.db.SimulationMax;
import eu.sqooss.metrics.simulation.db.SimulationChi;

/**
 * SimulationAlgorithm runs the simulation for a project and generates the
 * results at a suitable form in order to be stored in SQO-OSS database <br>
 * 
 * @author Apostolos Kritikos <a
 *         href="mailto:akritiko@csd.auth.gr">(akritiko@csd.auth.gr)</a>
 */

public class SimulationModel {

	static final int MAXSTEPS = 500;
	static final int MAXPIECES = 10;
	static final int MAXEVENTS = 1000;

	static final int MAXTIMEWORKED = 1250;
	static final double M_PI = 6.283058;

	//TODO: to be deleted after changing the write to file...
	private String fileName;
	
	// <XML ATTRIBUTES>
	
	private int numberOfModules;
	private Vector<Integer> softwarePieces;
	private int simulationSteps;
	private int maximumRepetitions;
	private double timeScale;
	private Vector<Double> programmersInterestForSubmissionTasks;
	private Vector<Double> programmersInterestForDebuggingTasks;
	private Vector<Double> programmersInterestForTestingTasks;
	private Vector<Double> programmersInterestForFunctionalImprovementTasks;
	private double releasesFrequencyWeight;
	private double averageLOCIncrementWeight;
	private double averageCommitsIncrementWeight;
	private double calibrationAverageCommits;
	private double calibrationAverageReleases;
	private double calibrationAverageContributors;
	private int calibrationAverageLOCIncrement;
	private int numberOfCoreContributors;
	private double programmersInterestHalfLife;
	private double programmersInterestHalfLifeStandardDeviation;
	private double calibrationParameter;
	private int averageReleaseTime;
	private int[] averageLOCAddedToModuleSubmission;
	private int[] averageLOCAddedToModuleSubmissionStandardDeviation;
	private double[] functionallyCompleteModuleMaxLOC;
	private double[] functionallyCompleteModuleMaxLOCStandardDeviation;
	private double[] funcImprvTaskAverageLOCAdded;
	private double[] funcImprvTaskAverageLOCAddedStandardDeviation;
	private double initialAverageBugsPerLOC;
	private double initialAverageBugsPerLOCStandardDeviation;
	private double averageTimePerLOCProduction;
	private double averageTimePerLOCProductionStandardDeviation;
	private double averageTimePerBugFix;
	private double averageTimePerBugFixStandardDeviation;
	private double averageTimePerTestReport;
	private double averageTimePerTestReportStandardDeviation;

	// </XML ATTRIBUTES>

	private Vector<Integer> locMeanVector;
	private Vector<Integer> locStdevVector;
	private Vector<Double> locMeanCompPVector;
	private Vector<Double> locStdevCompVector;
	private Vector<Double> locFMeanVector;
	private Vector<Double> locFStdevVector;

	private double g0;
	private double zeroInterest;
	private double upRoot;

	private int[] s;
	private int meanF;
	private int stdevF;

	private double[] aS;
	private double[] aB;
	private double[] aT;
	private double[] aF;
	private double[] aaS;
	private double[] aaB;
	private double[] aaT;
	private double[] aaF;

	private double[] aaaS;
	private double[] aaaaS;

	private double[][] aaaB;
	private double[][] aaaT;
	private double[][] aaaF;
	private double[][] aaaaB;
	private double[][] aaaaT;
	private double[][] aaaaF;

	private double sumS;
	private double sumB;
	private double sumT;
	private double sumF;

	private int timeWorkedMax;

	private int[][] f;
	private int[][] fInit;
	private int[][] b;
	private int[][] bReported;
	private int[][] tt;

	private int[] lTot = new int[MAXSTEPS + 1];
	private int[] bTot = new int[MAXSTEPS + 1];
	private int lTotal;
	private int bTotal;
	private int bReportedTotal;
	private int dLovTotal;
	private int lTotalPrev;

	private double bugReportedPer;
	private double bugReportedPerStdev;
	private double redFact;
	private double cQ;
	private double cB;
	private double cT;
	private double r;

	private int nMax;
	private int nPr;

	private int nCoreAvailable;
	private int numBugsI;
	private int dLocTotalAccum;
	private int lTotPrev;
	private int lTotCurr;
	private double reduce;
	private double interest;
	private double numTestsI;
	private double avdLocTot;
	private double q;
	private double q1;
	private double qInit;
	private double e;
	private double lamda;
	private double qBarSquared;
	private double qPrime;
	private double qik;

	private int[] nProgAvailable = new int[MAXTIMEWORKED + 1];
	private int[] indexS = new int[MAXSTEPS + 1];
	private int[] indexB = new int[MAXSTEPS + 1];
	private int[] indexT = new int[MAXSTEPS + 1];
	private int[] indexF = new int[MAXSTEPS + 1];

	private int[][] eventSModule = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventSLoc = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventSBug = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventSWho = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventSTime = new int[MAXSTEPS + 1][MAXEVENTS];

	private int[][] eventBModule = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventBPiece = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventBWho = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventBTime = new int[MAXSTEPS + 1][MAXEVENTS];

	private int[][] eventTModule = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventTPiece = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventTBugReport = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventTWho = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventTTime = new int[MAXSTEPS + 1][MAXEVENTS];

	private int[][] eventFModule = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFPiece = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFLoc = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFBug = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFWho = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFTime = new int[MAXSTEPS + 1][MAXEVENTS];

	private int[][] eventSCore = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventBCore = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventTCore = new int[MAXSTEPS + 1][MAXEVENTS];
	private int[][] eventFCore = new int[MAXSTEPS + 1][MAXEVENTS];

	private int[] pS;

	private int[][] pB;
	private int[][] pT;
	private int[][] pF;

	private int locSubm;
	private int bugSubm;
	private int timeSubm;
	private int dtSubm;
	private int bugReport;

	private int numReleases;

	//TODO: Delete after output goes to database
	private File outfile;
	private File outfile2;
	private File outfile3;
	private File outfile4;
	private File outfile5;

	private int lTotalMax;
	private int lTotalMin;

	private double prevDiff;
	private double prevDiff2;

	private Random myRand = new Random();

	private LinkedList<SimulationMax> maxRecords = new LinkedList<SimulationMax>();
	private LinkedList<SimulationMin> minRecords = new LinkedList<SimulationMin>();
	private LinkedList<SimulationChi> chiRecords = new LinkedList<SimulationChi>();
	private LinkedList<SimulationResults> resultRecords = new LinkedList<SimulationResults>();

	public SimulationModel(SimulationParameters parameters) {

		numberOfModules = parameters.getNumberOfModules();
		simulationSteps = parameters.getSimulationSteps();
		maximumRepetitions = parameters.getMaximumRepetitions();
		timeScale = parameters.getTimeScale();

		s = new int[numberOfModules + 1];
		aS = new double[numberOfModules + 1];
		aB = new double[numberOfModules + 1];
		aT = new double[numberOfModules + 1];
		aF = new double[numberOfModules + 1];
		aaS = new double[numberOfModules + 1];
		aaB = new double[numberOfModules + 1];
		aaT = new double[numberOfModules + 1];
		aaF = new double[numberOfModules + 1];

		aaaS = new double[numberOfModules + 1];
		aaaaS = new double[numberOfModules + 1];
		aaaB = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		aaaaB = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		aaaT = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		aaaaT = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		aaaF = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		aaaaF = new double[MAXPIECES][numberOfModules + 1 * MAXPIECES];

		averageLOCAddedToModuleSubmission = new int[numberOfModules + 1];
		averageLOCAddedToModuleSubmissionStandardDeviation = new int[numberOfModules + 1];

		functionallyCompleteModuleMaxLOC = new double[numberOfModules + 1];
		functionallyCompleteModuleMaxLOCStandardDeviation = new double[numberOfModules + 1];
		funcImprvTaskAverageLOCAdded = new double[numberOfModules + 1];
		funcImprvTaskAverageLOCAddedStandardDeviation = new double[numberOfModules + 1];

		f = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		fInit = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		b = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		bReported = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		tt = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];

		pS = new int[numberOfModules + 1];
		pB = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		pT = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];
		pF = new int[MAXPIECES][numberOfModules + 1 * MAXPIECES];

		softwarePieces = parameters.getSoftwarePieces();
		programmersInterestForSubmissionTasks = parameters.getProgrammersInterestForSubmissionTasks();
		programmersInterestForDebuggingTasks = parameters.getProgrammersInterestForDebuggingTasks();
		programmersInterestForTestingTasks = parameters.getProgrammersInterestForTestingTasks();
		programmersInterestForFunctionalImprovementTasks = parameters
				.getProgrammersInterestForFunctionalImprovementTasks();
		locMeanVector = parameters.getAverageLOCAddedToModuleSubmission();
		locStdevVector = parameters
				.getAverageLOCAddedToModuleSubmissionStandardDeviation();
		locMeanCompPVector = parameters.getFunctionallyCompleteModuleMaxLOC();
		locStdevCompVector = parameters
				.getFunctionallyCompleteModuleMaxLOCStandardDeviation();
		locFMeanVector = parameters.getFuncImprvTaskAverageLOCAdded();
		locFStdevVector = parameters
				.getFuncImprvTaskAverageLOCAddedStandardDeviation();

		for (int i = 1; i <= numberOfModules; i++) {
			s[i] = softwarePieces.get(i - 1);
			aS[i] = programmersInterestForSubmissionTasks.get(i - 1);
			aB[i] = programmersInterestForDebuggingTasks.get(i - 1);
			aT[i] = programmersInterestForTestingTasks.get(i - 1);
			aF[i] = programmersInterestForFunctionalImprovementTasks.get(i - 1);
			averageLOCAddedToModuleSubmission[i] = locMeanVector.get(i - 1);
			averageLOCAddedToModuleSubmissionStandardDeviation[i] = locStdevVector.get(i - 1);
			functionallyCompleteModuleMaxLOC[i] = locMeanCompPVector.get(i - 1);
			functionallyCompleteModuleMaxLOCStandardDeviation[i] = locStdevCompVector.get(i - 1);
			funcImprvTaskAverageLOCAdded[i] = locFMeanVector.get(i - 1);
			funcImprvTaskAverageLOCAddedStandardDeviation[i] = locFStdevVector.get(i - 1);
		}

		initialAverageBugsPerLOC = parameters.getInitialAverageBugsPerLOC();
		initialAverageBugsPerLOCStandardDeviation = parameters
				.getInitialAverageBugsPerLOCStandardDeviation();
		averageTimePerLOCProduction = (parameters.getAverageTimePerLOCProduction() / timeScale);
		averageTimePerLOCProductionStandardDeviation = (parameters
				.getAverageTimePerLOCProductionStandardDeviation() / timeScale);
		averageTimePerBugFix = (parameters.getAverageTimePerBugFix() / timeScale);
		averageTimePerBugFixStandardDeviation = (parameters
				.getAverageTimePerBugFixStandardDeviation() / timeScale);
		averageTimePerTestReport = parameters.getAverageTimePerTestReport() / timeScale;
		averageTimePerTestReportStandardDeviation = parameters
				.getAverageTimePerTestReportStandardDeviation()
				/ timeScale;
		averageReleaseTime = (int) (parameters.getAverageReleaseTime() / timeScale);
		numberOfCoreContributors = parameters.getNumberOfCoreContributors();
		releasesFrequencyWeight = parameters.getReleasesFrequencyWeight();
		averageLOCIncrementWeight = parameters.getAverageLOCIncrementWeight();
		averageCommitsIncrementWeight = parameters.getAverageCommitsIncrementWeight();
		calibrationAverageCommits = (parameters.getCalibrationAverageCommits() * timeScale);
		calibrationAverageReleases = (parameters.getCalibrationAverageReleases() * timeScale);
		calibrationAverageLOCIncrement = (int) (parameters.getCalibrationAverageLOCIncrement() * timeScale);
		calibrationAverageContributors = parameters.getCalibrationAverageContributors();
		calibrationParameter = parameters.getCalibrationParameter();
		programmersInterestHalfLife = parameters.getProgrammersInterestHalfLife();
		programmersInterestHalfLifeStandardDeviation = parameters.getProgrammersInterestHalfLifeStandardDeviation();

		fileName = "results_";
	}

	public boolean simulate() {
		int i;
		int j;
		int k;
		int k2;
		int t1;
		int t;
		int nEvent;
		int oo = 0;
		int repetitions;
		int ss;
		int flag;
		int totS;
		int totB;
		int totT;
		int totF;
		int iTotS;
		int iTotB;
		int iTotT;
		int iTotF;
		int newBlood;

		int timeWorked;
		double norm;
		double lamdaFudge;
		double exponent;

		double[] lTotalR = new double[simulationSteps + 1];
		double[] activitySR = new double[simulationSteps + 1];

		double[] newBloodR = new double[simulationSteps + 1];
		double[] activityBR = new double[simulationSteps + 1];
		double[] activityTR = new double[simulationSteps + 1];
		double[] activityFR = new double[simulationSteps + 1];
		double[] progsAvailR = new double[simulationSteps + 1];
		double[] progsOccupR = new double[simulationSteps + 1];

		double[] bTotalR = new double[simulationSteps + 1];
		double[] bReportedTotalR = new double[simulationSteps + 1];
		double[] qR = new double[simulationSteps + 1];

		double[] lTotalR2 = new double[simulationSteps + 1];
		double[] activitySR2 = new double[simulationSteps + 1];

		double[] newBloodR2 = new double[simulationSteps + 1];
		double[] activityBR2 = new double[simulationSteps + 1];
		double[] activityTR2 = new double[simulationSteps + 1];
		double[] activityFR2 = new double[simulationSteps + 1];
		double[] progsAvailR2 = new double[simulationSteps + 1];
		double[] progsOccupR2 = new double[simulationSteps + 1];

		double[] bTotalR2 = new double[simulationSteps + 1];
		double[] bReportedTotalR2 = new double[simulationSteps + 1];
		double[] qR2 = new double[simulationSteps + 1];

		double lTotalRs;
		double activitySRs;
		double newBloodRs;
		double activityBRs;
		double activityTRs;
		double activityFRs;
		double progsOccupRs;
		double bTotalRs;
		double bReportedTotalRs;
		double qRs;

		int[] ll = new int[simulationSteps + 1];

		double[] bb = new double[simulationSteps + 1];
		double[] bbr = new double[simulationSteps + 1];

		int[] as = new int[simulationSteps + 1];
		int[] ab = new int[simulationSteps + 1];
		int[] af = new int[simulationSteps + 1];
		int[] at = new int[simulationSteps + 1];
		int[] po = new int[simulationSteps + 1];
		int[] nb = new int[simulationSteps + 1];

		double[] qqq = new double[simulationSteps + 1];

		double chiSq;

		double hh = 0;
		double hh2;
		int nn = 0;
		int kk;
		int aas;
		int aab;
		int aat;
		int aaf;
		int nnb;
		double bbb;
		double bbbr;
		double bbb2;
		double bbbr2;
		double aas2;
		double aab2;
		double aat2;
		double aaf2;
		double nnb2;

		boolean myFlag = true;

		flag = 0;

		while (myFlag) {
			myRand.setSeed(233);

			if (flag == 0) {
				for (t1 = 0; t1 <= simulationSteps; t1++) {
					lTotalR[t1] = 0;
					bTotalR[t1] = 0;
					bReportedTotalR[t1] = 0;
					activitySR[t1] = 0;
					activityBR[t1] = 0;
					activityFR[t1] = 0;
					activityTR[t1] = 0;
					qR[t1] = 0.;
					progsAvailR[t1] = 0;
					progsOccupR[t1] = 0;
					newBloodR[t1] = 0;
					lTotalR2[t1] = 0;
					bTotalR2[t1] = 0;
					bReportedTotalR2[t1] = 0;
					activitySR2[t1] = 0;
					activityBR2[t1] = 0;
					activityFR2[t1] = 0;
					activityTR2[t1] = 0;
					qR2[t1] = 0.;
					progsAvailR2[t1] = 0;
					progsOccupR2[t1] = 0;
					newBloodR2[t1] = 0;
				}
			}

			lTotalMax = 0;
			lTotalMin = 100000000;
			prevDiff = 1000000.;
			prevDiff2 = 1000000.;

			for (repetitions = 1; repetitions <= maximumRepetitions; repetitions++) {
				System.out.println("Repetition" + repetitions);
				myRand.setSeed(31 + repetitions * 2);
				for (i = 1; i <= numberOfModules; i++) {
					
					aaS[i] = 1.;
					aaB[i] = 1.;
					aaT[i] = 1.;
					aaF[i] = 1.;

					// *******************SOS SOS SOS
					// ***************************
					for (j = 1; j <= s[i]; j++)
						fInit[i][j] = (int) logNormal((double) averageLOCAddedToModuleSubmission[i],
								(double) averageLOCAddedToModuleSubmissionStandardDeviation[i]);
					// Bugmax[i]=1; //maximum number of bugs per 1000 lines
					for (j = 1; j <= s[i]; j++) {
						f[i][j] = fInit[i][j]; // lines of code for soft. piece
						// j of module i
						b[i][j] = 1; // number of defects of soft. piece j of
						// mod. i
						bReported[i][j] = 1; // number of reported defects of
						// soft. piece j of mod. i
						tt[i][j] = 0; // number of times soft. piece j of
						// module i was tested
					}
					for (j = s[i] + 1; j < MAXPIECES; j++) {
						f[i][j] = 0; // lines of code for soft. piece j of
						// module i
						b[i][j] = 0; // number of defects of soft. piece j of
						// mod. i
						bReported[i][j] = 0; // number of reported defects of
						// soft. piece j of mod. i
						tt[i][j] = 0; // number of times soft. piece j of
						// module i was tested
					}

				}
				
				bugReportedPer = 0.5; // percentage of bugs that can be
				// reported in one day
				bugReportedPerStdev = 0.25; // stdev of bug_report_per
				cQ = 0.000004;
				cB = 0.025;
				cT = 1.0;

				// Pr_release_interval=(int) (60./SCALE);
				numReleases = MAXSTEPS / averageReleaseTime;
				if (numReleases * averageReleaseTime + 1 > MAXSTEPS)
					numReleases--;

				timeWorkedMax = 0;
				lTot[1] = 0;
				bTot[1] = 0;
				bReportedTotal = 0;

				for (i = 1; i <= numberOfModules; i++) {
					lTot[1] += f[i][1];
					bTot[1] += b[i][1];
					bReportedTotal += bReported[i][1];
				}
				lTot[0] = 0;
				bTot[0] = 0;
				lTotalPrev = 0;

				lTotal = lTot[1];
				bTotal = bTot[1];
				lTotPrev = lTotal;
				lTotCurr = lTotal;

				r = 0;
				
				nMax = 200000;
				
				qInit = 1.0; // initial Q-interest relative to Linux (for the
				// beginning of project (t=1))
				lamdaFudge = 5.;
				zeroInterest = (calibrationAverageContributors - (double) numberOfCoreContributors) / ((double) nMax);
				
				g0 = 4. * (1. - zeroInterest) / (programmersInterestHalfLife * programmersInterestHalfLife);
				upRoot = 0.5 * programmersInterestHalfLife * (1. + 1. / Math.sqrt(1. - zeroInterest));

				nPr = 0;

				// ***********************************************************************
				for (i = 0; i <= MAXTIMEWORKED; i++)
					nProgAvailable[i] = 0;

				for (i = 0; i <= MAXSTEPS; i++) {
					indexS[i] = 0;
					indexB[i] = 0;
					indexT[i] = 0;
					indexF[i] = 0;

					for (j = 0; j < MAXEVENTS; j++) {
						eventSModule[i][j] = 0;
						eventSLoc[i][j] = 0;
						eventSBug[i][j] = 0;
						eventSCore[i][j] = 0;
						eventSWho[i][j] = 0;
						eventSTime[i][j] = 0;

						eventBModule[i][j] = 0;
						eventBPiece[i][j] = 0;
						eventBCore[i][j] = 0;
						eventBWho[i][j] = 0;
						eventBTime[i][j] = 0;

						eventTModule[i][j] = 0;
						eventTPiece[i][j] = 0;
						eventTBugReport[i][j] = 0;
						eventTCore[i][j] = 0;
						eventTWho[i][j] = 0;
						eventTTime[i][j] = 0;

						eventFModule[i][j] = 0;
						eventFPiece[i][j] = 0;
						eventFLoc[i][j] = 0;
						eventFBug[i][j] = 0;
						eventFCore[i][j] = 0;
						eventFWho[i][j] = 0;
						eventFTime[i][j] = 0;
					}
				}
				nProgAvailable[0] = nMax;

				nCoreAvailable = numberOfCoreContributors; // *** check how Ncore_avail behaves in
				// task assignment
				dLocTotalAccum = 0;
				norm = 1. / (1. - Math.pow(10., -lamdaFudge));
				totS = 0;
				totB = 0;
				totT = 0;
				totF = 0;

				// ******************* START SIMULATION (time loop)
				// *********************
				for (t1 = 1; t1 <= simulationSteps; t1++) {
					//System.out.println("Simulation step: " + t1);
					t = t1 % MAXSTEPS;
					
					lTot[t] = lTotal;
					bTot[t] = bTotal;

					// E Check for submission of events and update variables
					// ________________________
					// E1) Software writing
					iTotS = 0;
					iTotB = 0;
					iTotT = 0;
					iTotF = 0;
					for (nEvent = 1; nEvent <= indexS[t]; nEvent++) {
						iTotS++;
						i = eventSModule[t][nEvent];
						s[i]++;
						fInit[i][s[i]] = eventSLoc[t][nEvent];
						f[i][s[i]] = fInit[i][s[i]];
						b[i][s[i]] = eventSBug[t][nEvent];
						tt[i][s[i]] = 0;
						lTotal += f[i][s[i]];
						bTotal += b[i][s[i]];
						if (eventSCore[t][nEvent] == 0) {
							timeWorked = (int) Math.max(Math.min(
									(double) (eventSWho[t][nEvent]
											+ eventSTime[t][nEvent] + 1),
									(double) MAXTIMEWORKED)
									* timeScale, 1.);

							nProgAvailable[timeWorked]++;
							
							if (timeWorked > timeWorkedMax)
								timeWorkedMax = timeWorked;
						} else {
							nCoreAvailable++;
						}
					}
					indexS[t] = 0;
					// E2) Debugging
					for (nEvent = 1; nEvent <= indexB[t]; nEvent++) {
						iTotB++;
						i = eventBModule[t][nEvent];
						j = eventBPiece[t][nEvent];
						if (b[i][j] > 0) {
							if (bReported[i][j] > 0)
								bReported[i][j]--;
							b[i][j]--;
							bTotal--;
							bReportedTotal--;
						}
						if (eventBCore[t][nEvent] == 0) {
							timeWorked = (int) Math.max(Math.min(
									(double) (eventBWho[t][nEvent]
											+ eventBTime[t][nEvent] + 1),
									(double) MAXTIMEWORKED)
									* timeScale, 1.);
							nProgAvailable[timeWorked]++;
							
							if (timeWorked > timeWorkedMax)
								timeWorkedMax = timeWorked;
						} else {
							nCoreAvailable++;
						}
					}
					indexB[t] = 0;

					// E3) Testing
					for (nEvent = 1; nEvent <= indexT[t]; nEvent++) {
						iTotT++;
						i = eventTModule[t][nEvent];
						j = eventTPiece[t][nEvent];
						tt[i][j]++;
						bReported[i][j] += eventTBugReport[t][nEvent];
						bReportedTotal += eventTBugReport[t][nEvent];
						if (eventTCore[t][nEvent] == 0) {
							timeWorked = (int) Math.max(Math.min(
									(double) (eventTWho[t][nEvent]
											+ eventTTime[t][nEvent] + 1),
									(double) MAXTIMEWORKED)
									* timeScale, 1.);
							nProgAvailable[timeWorked]++;
							
							if (timeWorked > timeWorkedMax)
								timeWorkedMax = timeWorked;
						} else {
							nCoreAvailable++;
						}
					}
					indexT[t] = 0;
					// E4) Functional improving
					for (nEvent = 1; nEvent <= indexF[t]; nEvent++) {
						iTotF++;
						i = eventFModule[t][nEvent];
						j = eventFPiece[t][nEvent];
						f[i][j] += eventFLoc[t][nEvent];
						b[i][j] += eventFBug[t][nEvent];
						lTotal += eventFLoc[t][nEvent];
						bTotal += eventFBug[t][nEvent];
						tt[i][j] -= (nint(tt[i][j] * 0.5));
						if (eventFCore[t][nEvent] == 0) {
							timeWorked = (int) Math.max(Math.min(
									(double) (eventFWho[t][nEvent]
											+ eventFTime[t][nEvent] + 1),
									(double) MAXTIMEWORKED)
									* timeScale, 1.);
							nProgAvailable[timeWorked]++;
						
							if (timeWorked > timeWorkedMax)
								timeWorkedMax = timeWorked;
						} else {
							nCoreAvailable++;
						}
					}
					indexF[t] = 0;
					// _____________________________- END E -
					// _________________________

					// Check for production release
					if (t1 % averageReleaseTime == 1) {
						nPr++;
						lTotPrev = lTotCurr;
						lTotCurr = lTotal;
					}

					// A computation of E[t)
					// A1) computation of Q[t)
					// A1a) computation of R[t)
					// curr_release_time=((N_pr-1)*Pr_release_interval+1)%MAXSTEPS;
					// prev_release_time=((N_pr-2)*Pr_release_interval+1)%MAXSTEPS;
					if (nPr > 1) {
						r = (lTotCurr - lTotPrev)
								/ ((double) averageReleaseTime);
					}

					// A1b) computation of <dLoc_tot/dt>

					dLovTotal = lTotal - lTotalPrev;
					dLocTotalAccum += dLovTotal;
					avdLocTot = dLocTotalAccum / ((double) t1);
					q1 = releasesFrequencyWeight * r / calibrationAverageReleases + averageLOCIncrementWeight * avdLocTot / calibrationAverageLOCIncrement + averageCommitsIncrementWeight
							* (totS + totB + totT + totF) / (t1 * calibrationAverageCommits);
					if (q1 > 1.) {
						q = 1. + Math.log10(lamdaFudge * q1);
					} else {
						q = norm * (1. - Math.pow(10., -lamdaFudge * q1));
					}
					if (t1 == 1)
						q = qInit;
					// A2) computation of Lamda(t)
					lamda = timeScale * integral(nProgAvailable, 0, timeWorkedMax); // ***
					// sum
					// syntax

					e = calibrationParameter * q * lamda + nCoreAvailable;

					// ____________________ - END A -
					// ______________________________

					// calculation of number of initiating tasks per task type
					// at time t
					// B1) calculation of A's, and a's for each module, task and
					// program
					// B1a) Software writing:
					for (i = 1; i <= numberOfModules; i++) {
						aaaS[i] = aS[i] * Math.exp(-8. * s[i]);
						// calculation of q_i_bar_squared
						qBarSquared = 0.;
						for (k2 = 1; k2 <= s[i]; k2++) {
							qBarSquared += (((double) f[i][k2])
									* ((double) f[i][k2]) / (1. + ((double) b[i][k2])
									* ((double) b[i][k2])));
						}

						if (s[i] != 0)
							qBarSquared /= s[i];
						qPrime = Math.exp(-cQ * qBarSquared);
						aaaaS[i] = aaS[i] * aaaS[i] * (1 - qPrime);

					}
					// B1b) debugging
					for (i = 1; i <= numberOfModules; i++) {
						numBugsI = 0;
						for (k2 = 1; k2 <= s[i]; k2++)
							numBugsI += ((int) Math.min((double) bReported[i][k2],
									(double) b[i][k2])); // *** sum syntax
						for (j = 1; j <= s[i]; j++) {
							if (numBugsI != 0) {
								aaaB[i][j] = aB[i]
										* Math.min((double) bReported[i][j],
												(double) b[i][j]) / numBugsI;
							} else {
								aaaB[i][j] = aB[i] / s[i];
							}
							// calculation of Q(i,j)
							qik = Math.exp(-cQ * ((double) f[i][j])
									* ((double) f[i][j])
									/ (1. + (double) (b[i][j] * b[i][j])));

							if (bReported[i][j] != 0) {
								aaaaB[i][j] = aaB[i]
										* aaaB[i][j]
										* 0.5
										* (qik + Math
												.exp(-cB
														* Math.min(
																(double) b[i][j],
																(double) bReported[i][j])));
							} else {
								aaaaB[i][j] = aaaB[i][j];
							}
						}
					}
					// B1c) testing
					for (i = 1; i <= numberOfModules; i++) {
						numTestsI = 0.;
						for (k2 = 1; k2 <= s[i]; k2++) {
							numTestsI += (1. / ((double) (tt[i][k2] + 1)));
						}
						for (j = 1; j <= s[i]; j++) {
							aaaT[i][j] = aT[i]
									* (1. / ((double) (tt[i][j] + 1)))
									/ numTestsI;

							aaaaT[i][j] = aaT[i]
									* aaaT[i][j]
									* (1. - Math.exp(-cT * tt[i][j]) + 0.5 * (1 - Math
											.exp(-0.1 / (numTestsI * s[i]))));
						}
					}
					// B1d) functional improving
					for (i = 1; i <= numberOfModules; i++) {
						for (j = 1; j <= s[i]; j++) {
							aaaF[i][j] = aF[i] / s[i]; // *** check scaling
							// calculation of Q(i,j);

							qik = Math
									.exp((f[i][j] - functionallyCompleteModuleMaxLOC[i]
											* averageLOCAddedToModuleSubmission[i])
											/ (functionallyCompleteModuleMaxLOCStandardDeviation[i] * averageLOCAddedToModuleSubmission[i]));
							qik = Math.min(qik, 1.);

							aaaaF[i][j] = aaF[i] * aaaF[i][j] * qik;
						}
					}

					for (i = 1; i <= numberOfModules; i++) {
						for (j = 1; j <= s[i]; j++) {
							aaaB[i][j] /= numberOfModules;
							aaaT[i][j] /= numberOfModules;
							aaaF[i][j] /= numberOfModules;
							aaaaB[i][j] /= numberOfModules;
							aaaaT[i][j] /= numberOfModules;
							aaaaF[i][j] /= numberOfModules;
						}
						aaaS[i] /= numberOfModules;
						aaaaS[i] /= numberOfModules;
					}

					// ________________________ - END B -
					// _____________________________

					// C calculation of initiated tasks
					interest = 0.;
					reduce = 0.;
					sumS = 0.;
					sumB = 0.;
					sumT = 0.;
					sumF = 0.;

					for (i = 1; i <= numberOfModules; i++) {
						for (j = 1; j <= s[i]; j++) {
							interest += (aaaB[i][j] + aaaT[i][j] + aaaF[i][j]);
							reduce += (aaaaB[i][j] + aaaaT[i][j] + aaaaF[i][j]);
							sumS += (aaaaB[i][j] + aaaaT[i][j] + aaaaF[i][j]);
							sumB += (aaaaT[i][j] + aaaaF[i][j]);
							sumT += (aaaaB[i][j] + aaaaF[i][j]);
							sumF += (aaaaB[i][j] + aaaaT[i][j]);
						}
						sumB += aaaaS[i];
						sumT += aaaaS[i];
						sumF += aaaaS[i];
						interest += aaaS[i];
						reduce += aaaaS[i];
					}
					totS = 0;
					totB = 0;
					totT = 0;
					totF = 0;
					for (i = 1; i <= numberOfModules; i++) {
						pS[i] = rNint(e * (aaaS[i] * (1. + sumS) - aaaaS[i]));
						totS += pS[i];
						for (j = 1; j <= s[i]; j++) { // *** check scaling
							pB[i][j] = rNint(e
									* (aaaB[i][j] * (1. + sumB) - aaaaB[i][j]));
							pT[i][j] = rNint(e
									* (aaaT[i][j] * (1. + sumT) - aaaaT[i][j]));
							pF[i][j] = rNint(e
									* (aaaF[i][j] * (1. + sumF) - aaaaF[i][j]));
							totB += pB[i][j];
							totT += pT[i][j];
							totF += pF[i][j];
						}
					}

					// ________________________ - END C -
					// _____________________________

					totS = Math.abs(totS);
					totB = Math.abs(totB);
					totT = Math.abs(totT);
					totF = Math.abs(totF);
					newBlood = 0;

					boolean mustBreak = false;

					// D calculation of submission times, LOC/bugs submitted at
					// submission event and who will do the tasks (new
					// programmer or old?)
					for (i = 1; i <= numberOfModules; i++) {
						// D1) Software writing
						for (j = 1; j <= pS[i]; j++) {
							locSubm = nint(distributeLOC(averageLOCAddedToModuleSubmission[i], averageLOCAddedToModuleSubmissionStandardDeviation[i]));
							bugSubm = rNint(locSubm
									* distributeBug(initialAverageBugsPerLOC, initialAverageBugsPerLOCStandardDeviation));
							dtSubm = rNint(locSubm
									* distributeTimeLOC(averageTimePerLOCProduction * 0.5,
											averageTimePerLOCProductionStandardDeviation * 0.707));
							dtSubm = nint(Math.max((double) dtSubm, 1.));
							if (dtSubm < MAXSTEPS) {
								
								timeSubm = (dtSubm + t + oo) % MAXSTEPS;
								oo++;
								timeSubm = (dtSubm + t) % MAXSTEPS;
								if (indexS[timeSubm] > MAXEVENTS - 2) {
									j--;
									continue;
								} else {
									eventSTime[timeSubm][indexS[timeSubm] + 1] = dtSubm;
									eventSModule[timeSubm][indexS[timeSubm] + 1] = i;
									eventSLoc[timeSubm][indexS[timeSubm] + 1] = locSubm;
									eventSBug[timeSubm][indexS[timeSubm] + 1] = bugSubm;
									if (nCoreAvailable > 0) {
										nCoreAvailable--;
										eventSCore[timeSubm][indexS[timeSubm] + 1] = 1;
									} else {
										nn = 0;
										for (ss = 0; ss <= MAXTIMEWORKED; ss++)
											nn += nProgAvailable[ss];

										if (nn == 0) {
											mustBreak = true;
											break;
										}

										timeWorked = distributeWho(nn); // distr_WHO
										// selects
										// the
										// time
										// a
										// progammer
										// has
										// already
										// worked
										// totally in the project. It depends on
										// the function
										// Nprog_avail(i)*(1+g0*i*(s0-i)).
										if (timeWorked == 0)
											newBlood++;
										eventSWho[timeSubm][indexS[timeSubm] + 1] = timeWorked;
										nProgAvailable[timeWorked]--;
										eventSCore[timeSubm][indexS[timeSubm] + 1] = 0;
									}
									indexS[timeSubm]++;
								}
							}
							if (mustBreak) {
								break;
							}
						}

						// D2) Debugging
						if (!mustBreak) {
							for (j = 1; j <= s[i]; j++) {
								for (k = 1; k <= pB[i][j]; k++) {
									dtSubm = rNint(distributeBugFix(
											averageTimePerBugFix,
											averageTimePerBugFixStandardDeviation, f[i][j], lTotal)); // ***
									// time
									// to
									// fix
									// a
									// bug
									// is
									// proportional
									// to the number of LOC in the software
									// piece examines (F(i,j)) and proportional
									// to the square of total LOC in the project
									// so far (L_tot(t)).
									dtSubm = nint(Math.max((double) dtSubm, 1.));
									if (dtSubm < MAXSTEPS) {
										
										timeSubm = (dtSubm + t) % MAXSTEPS;
										if (indexB[timeSubm] > MAXEVENTS - 2) {
											k--;
											continue;
										} else {
											eventBTime[timeSubm][indexB[timeSubm] + 1] = dtSubm;
											eventBModule[timeSubm][indexB[timeSubm] + 1] = i;
											eventBPiece[timeSubm][indexB[timeSubm] + 1] = j;
											if (nCoreAvailable > 0) {
												nCoreAvailable--;
												eventBCore[timeSubm][indexS[timeSubm] + 1] = 1;
											} else {
												nn = 0;
												for (ss = 0; ss <= MAXTIMEWORKED; ss++)
													nn += nProgAvailable[ss];

												if (nn == 0) {
													mustBreak = true;
													break;
												}

												timeWorked = distributeWho(nn); // ***
												// distr_WHO
												// selects
												// the
												// time
												// a
												// progammer
												// has
												// already
												// worked
												// totally in the project. It
												// depends on the function
												// Nprog_avail(i)*(1+g0*i*(s0-i)).
												if (timeWorked == 0)
													newBlood++;
												eventBWho[timeSubm][indexB[timeSubm] + 1] = timeWorked;
												nProgAvailable[timeWorked]--;
												eventBCore[timeSubm][indexB[timeSubm] + 1] = 0;
											}
											indexB[timeSubm]++;
										}
									}
									if (mustBreak) {
										break;
									}
								}
								if (mustBreak) {
									break;
								}
							}
						}
						// D3) testing
						if (!mustBreak) {
							for (j = 1; j <= s[i]; j++) {
								for (k = 1; k <= pT[i][j]; k++) {
									dtSubm = rNint(distributeTesting(
											averageTimePerTestReport,
											averageTimePerTestReportStandardDeviation, f[i][j],
											lTotal)); // *** time to test is
									// proportional
									// to the number of LOC in the software
									// piece examines (F(i,j)) and proportional
									// to the total LOC in the project
									// so far (L_tot(t)).
									dtSubm = nint(Math.max((double) dtSubm, 1.));
									bugReport = rNint(distributeTest(b[i][j],
											bugReportedPer,
											bugReportedPerStdev)); // ***
									// distr_test
									// should
									// give the
									// number of
									// bugs that
									// can be
									// reported
									// in one day and nust be proportional to
									// the square root of the number of real
									// bugs existing in the piece
									if (dtSubm < MAXSTEPS) {
										oo = 0;

										timeSubm = (dtSubm + t) % MAXSTEPS;
										if (indexT[timeSubm] > MAXEVENTS - 2) {
											k--;
											continue;
										} else {
											eventTTime[timeSubm][indexT[timeSubm] + 1] = dtSubm;
											eventTModule[timeSubm][indexT[timeSubm] + 1] = i;
											eventTPiece[timeSubm][indexT[timeSubm] + 1] = j;
											eventTBugReport[timeSubm][indexT[timeSubm] + 1] = bugReport;
											if (nCoreAvailable > 0) {
												nCoreAvailable--;
												eventTCore[timeSubm][indexS[timeSubm] + 1] = 1;
											} else {
												nn = 0;
												for (ss = 0; ss <= MAXTIMEWORKED; ss++)
													nn += nProgAvailable[ss];

												if (nn == 0) {
													mustBreak = true;
													break;
												}

												timeWorked = distributeWho(nn); // ***
												// distr_WHO
												// selects
												// the
												// time
												// a
												// progammer
												// has
												// already
												// worked
												// totally in the project. It
												// depends on the function
												// Nprog_avail(i)*(1+g0*i*(s0-i)).
												if (timeWorked == 0)
													newBlood++;
												eventTWho[timeSubm][indexT[timeSubm] + 1] = timeWorked;
												nProgAvailable[timeWorked]--;
												eventTCore[timeSubm][indexT[timeSubm] + 1] = 0;
											}
											indexT[timeSubm]++;
										}
									}
									if (mustBreak) {
										break;
									}
								}
								if (mustBreak) {
									break;
								}
							}
						}
						// D4) Functional improving
						if (!mustBreak) {
							for (j = 1; j <= s[i]; j++) {
								for (k = 1; k <= pF[i][j]; k++) {
									exponent = (f[i][j] - functionallyCompleteModuleMaxLOC[i]
											* averageLOCAddedToModuleSubmission[i])
											/ (functionallyCompleteModuleMaxLOCStandardDeviation[i] * averageLOCAddedToModuleSubmission[i]);
									if (exponent > 50.) {
										redFact = 10000000000.;
									} else if (exponent < (-50.)) {
										redFact = 0.0000000001;
									} else {
										redFact = Math.exp(exponent);
									}
									redFact = Math.max(redFact, 1.);
									meanF = (int) (funcImprvTaskAverageLOCAdded[i] / redFact);
									stdevF = (int) (funcImprvTaskAverageLOCAddedStandardDeviation[i] / Math
											.sqrt(redFact));
									locSubm = 1 + rNint(distributeLOC(meanF,
											stdevF)); // ***the number of LOC
									// written should be
									// INVERSELY
									// proportional
									// to the number of LOC already written for
									// the piece
									bugSubm = rNint(locSubm
											* distributeBug(initialAverageBugsPerLOC,
													initialAverageBugsPerLOCStandardDeviation)); // ***
									dtSubm = rNint(locSubm
											* distributeTimeLOC(averageTimePerLOCProduction,
													averageTimePerLOCProductionStandardDeviation));
									dtSubm = nint(Math.max((double) dtSubm, 1.));
									if (dtSubm < MAXSTEPS) {
										
										timeSubm = (dtSubm + t) % MAXSTEPS;
										if (indexF[timeSubm] > MAXEVENTS - 2) {
											k--;
											continue;
										} else {
											eventFTime[timeSubm][indexF[timeSubm] + 1] = dtSubm;
											eventFModule[timeSubm][indexF[timeSubm] + 1] = i;
											eventFPiece[timeSubm][indexF[timeSubm] + 1] = j;
											eventFLoc[timeSubm][indexF[timeSubm] + 1] = locSubm;
											eventFBug[timeSubm][indexF[timeSubm] + 1] = bugSubm;

											if (nCoreAvailable > 0) {
												nCoreAvailable--;
												eventFCore[timeSubm][indexS[timeSubm] + 1] = 1;
											} else {
												nn = 0;
												for (ss = 0; ss <= MAXTIMEWORKED; ss++)
													nn += nProgAvailable[ss];

												if (nn == 0) {
													mustBreak = true;
													break;
												}

												timeWorked = distributeWho(nn); // ***
												// distr_WHO
												// selects
												// the
												// time
												// a
												// programmer
												// has
												// already
												// worked
												// totally in the project. It
												// depends on the function
												// Nprog_avail(i)*(1+g0*i*(s0-i)).
												if (timeWorked == 0)
													newBlood++;
												eventFWho[timeSubm][indexF[timeSubm] + 1] = timeWorked;
												nProgAvailable[timeWorked]--;
												eventFCore[timeSubm][indexF[timeSubm] + 1] = 0;
											}
											indexF[timeSubm]++;
										}
									}
								}
								if (mustBreak) {
									break;
								}
							}
						}
						if (mustBreak) {
							break;
						}
					}

					// ________________________ - END D -
					// _____________________________

					if (flag == 0) {
						lTotalR[t1] += (double) lTotal;
						bTotalR[t1] += (1000. * bTotal / ((double) lTotal));
						bReportedTotalR[t1] += (1000. * bReportedTotal / ((double) lTotal));
						activitySR[t1] += (double) iTotS;
						activityBR[t1] += (double) iTotB;
						activityFR[t1] += (double) iTotF;
						activityTR[t1] += (double) iTotT;
						qR[t1] += q;

						nn = 0;
						for (kk = 0; kk <= MAXTIMEWORKED; kk++)
							nn += nProgAvailable[kk];
						progsOccupR[t1] += ((double) (nMax - nn));
						newBloodR[t1] += (double) newBlood;

						ll[t1] = lTotal;
						bb[t1] = 1000. * bTotal / ((double) lTotal);
						bbr[t1] = 1000. * bReportedTotal / ((double) lTotal);
						as[t1] = iTotS;
						ab[t1] = iTotB;
						af[t1] = iTotF;
						at[t1] = iTotT;
						qqq[t1] = q;
						po[t1] = nMax - nn;
						nb[t1] = newBlood;
					} else {
						lTotalR2[t1] += (((double) lTotal - lTotalR[t1]
								* hh) * ((double) lTotal - lTotalR[t1] * hh));
						bTotalR2[t1] += Math.pow(1000. * bTotal
								/ ((double) lTotal) - bTotalR[t1] * hh, 2.);
						bReportedTotalR2[t1] += Math.pow(1000.
								* bReportedTotal / ((double) lTotal)
								- bReportedTotalR[t1] * hh, 2.);
						activitySR2[t1] += (((double) iTotS - activitySR[t1]
								* hh) * ((double) iTotS - activitySR[t1] * hh));
						activityBR2[t1] += (((double) iTotB - activityBR[t1]
								* hh) * ((double) iTotB - activityBR[t1] * hh));
						activityFR2[t1] += (((double) iTotF - activityFR[t1]
								* hh) * ((double) iTotF - activityFR[t1] * hh));
						activityTR2[t1] += (((double) iTotT - activityTR[t1]
								* hh) * ((double) iTotT - activityTR[t1] * hh));
						qR2[t1] += ((q - qR[t1] * hh) * (q - qR[t1] * hh));

						progsOccupR2[t1] += (((double) nMax - (double) nn - progsOccupR[t1]
								* hh) * ((double) nMax - (double) nn - progsOccupR[t1]
								* hh));
						newBloodR2[t1] += (((double) newBlood - newBloodR[t1]
								* hh) * ((double) newBlood - newBloodR[t1]
								* hh));
					}

					lTotalPrev = lTotal;

					// OUTPUT OF RESULTS:

				}
				// ********************* END SIMULATION (time loop)
				// *****************************
				

				if (flag == 0) {
					if (lTotal > lTotalMax) {
						String tempFileName = fileName + "_results_max.txt";
						outfile2 = new File(tempFileName);
						FileWriter tempWriter = null;

						try {
							tempWriter = new FileWriter(outfile2);
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							
							tempWriter
									.write("time\tLOC\tB_dens\tB_rep_dens\tQ\ttotS\ttotB\ttotT\ttotF\tOcc_Programers\tNew_blood\r\n");
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						
			

						bbb = 0.;
						bbbr = 0.;
						aas = 0;
						aab = 0;
						aat = 0;
						aaf = 0;
						nnb = 0;
						for (t1 = 1; t1 <= simulationSteps; t1++) {
							bbb += bb[t1];
							bbbr += bbr[t1];
							aas += as[t1];
							aab += ab[t1];
							aat += at[t1];
							aaf += af[t1];
							nnb += nb[t1];
							if (t1 % 10 == 1) {
								try {
									
									SimulationMax tempMax = new SimulationMax(t1,1l,ll[t1], bbb, bbbr, qqq[t1], aas, aab, aat, aaf, po[t1], nnb);
									maxRecords.add(tempMax);
									
									tempWriter.write(t1 + "\t" + "\t" + "\t"
											+ ll[t1] + "\t" + bbb + "\t" + bbbr
											+ "\t" + qqq[t1] + "\t" + aas
											+ "\t" + aab + "\t" + aat + "\t"
											+ aaf + "\t" + po[t1] + "\t" + nnb
											+ "\r\n");
									
									
									
								} catch (IOException e) {
									
									e.printStackTrace();
								}

								bbb = 0.;
								bbbr = 0.;
								aas = 0;
								aab = 0;
								aat = 0;
								aaf = 0;
								nnb = 0;
							}
						}
						try {
							tempWriter.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						lTotalMax = lTotal;
					}

					chiSq = Math.sqrt(0.75 * (lTotal - 950000)
							* (lTotal - 950000) + 0.25
							* (ll[simulationSteps] - 350000)
							* (ll[simulationSteps] - 350000));
					if (chiSq < prevDiff) {
						String tempFileName = fileName + "_results_chi.txt";
						outfile4 = new File(tempFileName);
						FileWriter tempWriter = null;

						try {
							
							tempWriter = new FileWriter(outfile4);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						try {
							
							tempWriter
									.write("time\tLOC\tB_dens\tB_rep_dens\tQ\ttotS\ttotB\ttotT\ttotF\tOcc_Programers\tNew_blood\r\n");
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						bbb = 0.;
						bbbr = 0.;
						aas = 0;
						aab = 0;
						aat = 0;
						aaf = 0;
						nnb = 0;

						for (t1 = 1; t1 <= simulationSteps; t1++) {
							bbb += bb[t1];
							bbbr += bbr[t1];
							aas += as[t1];
							aab += ab[t1];
							aat += at[t1];
							aaf += af[t1];
							nnb += nb[t1];
							
							if (t1 % 10 == 1) {
								try {
									
									SimulationChi tempChi = new SimulationChi(t1,1l,ll[t1], bbb, bbbr, qqq[t1], aas, aab, aat, aaf, po[t1], nnb);
									chiRecords.add(tempChi);
									
									tempWriter.write(t1 + "\t" + ll[t1] + "\t"
											+ bbb + "\t" + bbbr + "\t"
											+ qqq[t1] + "\t" + aas + "\t" + aab
											+ "\t" + aat + "\t" + aaf + "\t"
											+ po[t1] + "\t" + nnb + "\r\n");
									
									
									
								} catch (IOException e) {
									
									e.printStackTrace();
								}
								bbb = 0.;
								bbbr = 0.;
								aas = 0;
								aab = 0;
								aat = 0;
								aaf = 0;
								nnb = 0;
							}
						}
						try {
							tempWriter.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						prevDiff = chiSq;
					}

					if ((Math.abs(lTotal - 950000) < 200000)
							&& (Math.abs(ll[maximumRepetitions] - 350000) < 70000)
							&& (chiSq < prevDiff2)) {
						String tempFileName = fileName + "_results_chi2.txt";
						outfile5 = new File(tempFileName);
						FileWriter tempWriter = null;

						try {
							tempWriter = new FileWriter(outfile5);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						try {
							tempWriter
									.write("time\tLOC\tB_dens\tB_rep_dens\tQ\ttotS\ttotB\ttotT\ttotF\tOcc_Programers\tNew_blood\r\n");
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						bbb = 0.;
						bbbr = 0.;
						aas = 0;
						aab = 0;
						aat = 0;
						aaf = 0;
						nnb = 0;

						for (t1 = 1; t1 <= simulationSteps; t1++) {
							bbb += bb[t1];
							bbbr += bbr[t1];
							aas += as[t1];
							aab += ab[t1];
							aat += at[t1];
							aaf += af[t1];
							nnb += nb[t1];
							if (t1 % 10 == 1) {
								try {
									
									tempWriter.write(t1 + "\t" + ll[t1] + "\t"
											+ bbb + "\t" + bbbr + "\t"
											+ qqq[t1] + "\t" + aas + "\t" + aab
											+ "\t" + aat + "\t" + aaf + "\t"
											+ po[t1] + "\t" + nnb + "\r\n");
									

									
									
								} catch (IOException e) {
									
									e.printStackTrace();
								}

								bbb = 0.;
								bbbr = 0.;
								aas = 0;
								aab = 0;
								aat = 0;
								aaf = 0;
								nnb = 0;
							}
						}
						try {
							tempWriter.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						prevDiff2 = chiSq;
					}

					if (lTotal < lTotalMin) {
						String tempFileName = fileName + "_results_min.txt";
						outfile3 = new File(tempFileName);
						FileWriter tempWriter = null;

						try {
							tempWriter = new FileWriter(outfile3);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						try {
							tempWriter
									.write("time\tLOC\tB_dens\tB_rep_dens\tQ\ttotS\ttotB\ttotT\ttotF\tOcc_Programers\tNew_blood\r\n");
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						bbb = 0.;
						bbbr = 0.;
						aas = 0;
						aab = 0;
						aat = 0;
						aaf = 0;
						nnb = 0;
						
						minRecords.clear();

						for (t1 = 1; t1 <= simulationSteps; t1 += 10) {
							bbb += bb[t1];
							bbbr += bbr[t1];
							aas += as[t1];
							aab += ab[t1];
							aat += at[t1];
							aaf += af[t1];
							nnb += nb[t1];
							if (t1 % 10 == 1) {
								try {
									
									SimulationMin tempMin = new SimulationMin(t1,1l,ll[t1], bbb, bbbr, qqq[t1], aas, aab, aat, aaf, po[t1], nnb);
									minRecords.add(tempMin);
									
									tempWriter.write(t1 + "\t" + ll[t1] + "\t"
											+ bbb + "\t" + bbbr + "\t"
											+ qqq[t1] + "\t" + aas + "\t" + aab
											+ "\t" + aat + "\t" + aaf + "\t"
											+ po[t1] + "\t" + nnb + "\r\n");
									
									
									
								} catch (IOException e) {
									
									e.printStackTrace();
								}

								bbb = 0.;
								bbbr = 0.;
								aas = 0;
								aab = 0;
								aat = 0;
								aaf = 0;
								nnb = 0;
							}
						}
						try {
							tempWriter.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}

						lTotalMin = lTotal;
					}
					// ***********************************************************
				}
			}

			// **************** END REPETITIONS LOOP
			// ***************************************************
			
			

			hh = 1. / ((double) maximumRepetitions);
			if (flag == 0) {
				flag = 1;
			} else {
				myFlag = false;
			}
		}

		String tempFileName = fileName + "_results.txt";
		outfile = new File(tempFileName);
		FileWriter tempWriter = null;

		try {
			tempWriter = new FileWriter(outfile);
			tempWriter
					.write("time\tLOC\tB_dens\tB_rep_dens\tQ\ttotS\ttotB\ttotT\ttotF\tOcc_Programers\tNew_blood\t");
			tempWriter
					.write("LOC2\tB_dens2\tB_rep_dens2\tQ2\ttotS2\ttotB2\ttotT2\ttotF2\tOcc_Programers2\tNew_blood2\n");
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		hh2 = 1. / ((double) maximumRepetitions - 1);
		bbb = 0.;
		bbbr = 0.;
		aas = 0;
		aab = 0;
		aat = 0;
		aaf = 0;
		nnb = 0;
		bbb2 = 0.;
		bbbr2 = 0.;
		aas2 = 0.;
		aab2 = 0.;
		aat2 = 0.;
		aaf2 = 0.;
		nnb2 = 0.;
		
		resultRecords.clear();

		for (t1 = 1; t1 <= simulationSteps; t1++) {
			lTotalRs = Math.sqrt(lTotalR2[t1] * hh2);
			bTotalRs = Math.sqrt(bTotalR2[t1] * hh2);
			bReportedTotalRs = Math.sqrt(bReportedTotalR2[t1] * hh2);
			activitySRs = Math.sqrt(activitySR2[t1] * hh2);
			activityBRs = Math.sqrt(activityBR2[t1] * hh2);
			activityFRs = Math.sqrt(activityFR2[t1] * hh2);
			activityTRs = Math.sqrt(activityTR2[t1] * hh2);
			qRs = Math.sqrt(qR2[t1] * hh2);
			progsOccupRs = Math.sqrt(progsOccupR2[t1] * hh2);
			newBloodRs = Math.sqrt(newBloodR2[t1] * hh2);

			bbb += bTotalR[t1];
			bbbr += bReportedTotalR[t1];
			aas += ((int) activitySR[t1]);
			aab += ((int) activityBR[t1]);
			aat += ((int) activityTR[t1]);
			aaf += ((int) activityFR[t1]);
			nnb += newBloodR[t1];

			bbb2 += (bTotalRs * bTotalRs);
			bbbr2 += (bReportedTotalRs * bReportedTotalRs);
			aas2 += (activitySRs * activitySRs);
			aab2 += (activityBRs * activityBRs);
			aat2 += (activityTRs * activityTRs);
			aaf2 += (activityFRs * activityFRs);
			nnb2 += (newBloodRs * newBloodRs);

			if (t1 % 10 == 1) {

				try {
					
					tempWriter.write(t1 + "\t" + lTotalR[t1] * hh + "\t"
							+ bbb * hh + "\t" + bbbr * hh + "\t" + qR[t1] * hh
							+ "\t" + aas * hh + "\t" + aab * hh + "\t" + aat
							* hh + "\t" + aaf * hh + "\t" + progsOccupR[t1]
							* hh + "\t" + nnb * hh + "\t");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				try {
					tempWriter.write(lTotalRs + "\t" + Math.sqrt(bbb2) + "\t"
							+ Math.sqrt(bbbr2) + "\t" + qRs + "\t"
							+ Math.sqrt(aas2) + "\t" + Math.sqrt(aab2) + "\t"
							+ Math.sqrt(aat2) + "\t" + Math.sqrt(aaf2) + "\t"
							+ progsOccupRs + "\t" + Math.sqrt(nnb2) + "\r\n");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				SimulationResults tempResults = new SimulationResults(t1,1l, lTotalR[t1] * hh, bbb * hh, bbbr * hh, qR[t1] * hh, aas * hh, aab * hh, aat * hh, aaf * hh, progsOccupR[t1] * hh, nnb * hh, lTotalRs, Math.sqrt(bbb2), Math.sqrt(bbbr2), qRs, Math.sqrt(aas2), Math.sqrt(aab2), Math.sqrt(aat2), Math.sqrt(aaf2), progsOccupRs, Math.sqrt(nnb2));
				resultRecords.add(tempResults);
				
				

				bbb = 0.;
				bbbr = 0.;
				aas = 0;
				aab = 0;
				aat = 0;
				aaf = 0;
				nnb = 0;
				bbb2 = 0.;
				bbbr2 = 0.;
				aas2 = 0.;
				aab2 = 0.;
				aat2 = 0.;
				aaf2 = 0.;
				nnb2 = 0.;

			}
		}
		try {
			tempWriter.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return true;
	}

	double distributeBugFix(double bugFixTimeMean, double bugFixTimeStdev,
			int F1, int L_tot1)
	// *** time to fix a bug is proportional
	// to the number of LOC in the software piece examines (F(i,j)) and
	// proportional
	// to the square of total LOC in the project
	// so far (L_tot(t)) *** the latter is not implemented so far.
	{
		double tim, ba;
		ba = (F1 - 1000) * 0.002;
		tim = logNormal(bugFixTimeMean, bugFixTimeStdev) + Math.max(ba, 0.);
		return tim;
	}

	int nint(double numx) {
		int ha;

		ha = (int) numx;
		if (numx - (double) ha <= 0.5) {
			return ha;
		} else {
			return ha + 1;
		}
	}

	int rNint(double numx) {
		int ha;
		double dec, r;

		ha = (int) numx;
		dec = numx - ha;
		r = myRand.nextDouble() / 1.0d;
		if (r <= dec) {
			return ha + 1;
		} else {
			return ha;
		}
	}

	double integral(int avail[], int start, int end) {
		int i;
		double integ;
		
		integ = 0.;
		for (i = start; i <= end; i++) {
			if ((double) i < upRoot) {
				integ += (avail[i] * (zeroInterest + g0 * i
						* (programmersInterestHalfLife - (double) i)));
			} else {
				integ += (avail[i] * zeroInterest);
			}
		}

		return integ;
	}

	int distributeWho(int nn) // *** distr_WHO selects the time a progammer has
	// already worked
	{
		int tim, i;
		double range, r;
		boolean fff;

		range = Math.min((double) timeWorkedMax, (double) MAXTIMEWORKED);
		if (nn == nProgAvailable[MAXTIMEWORKED]) {
			return MAXTIMEWORKED;
		} else {
			fff = false;
			for (i = 1; i <= range; i++) {
				if (nProgAvailable[i] != 0) {
					fff = true;
					break;
				}
			}
			if (fff) {
				do {
					r = 1 + (range - 1) * myRand.nextDouble() / 1.0d;
					tim = nint(r);
				} 
				while (nProgAvailable[tim] == 0);
			} else {
				tim = 0;
			}
			return tim;
		}

	}

	double distributeLOC(int locMean, int locStdev) {
		double loc;
		loc = logNormal((double) locMean, (double) locStdev);
		return loc;

	}

	double distributeBug(double bugPerLOCMean, double bugPerLOCStdev) {
		double bug;
		bug = logNormal(bugPerLOCMean, bugPerLOCStdev);
		return bug;
	}

	double distributeTimeLOC(double timePerLOCMeanS, double timePerLOCStdevS) {
		double tim;
		tim = logNormal(timePerLOCMeanS, timePerLOCStdevS);
		return tim;

	}

	double distributeTest(int b, double bugReportPer,
			double bugReportPerStdev)
	
	// *** distr_test should give the number of bugs that can be reported
	// in one day and must be proportional to the number of real bugs existing
	// in the piece
	{
		double bug;
		bug = b * logNormal(bugReportPer, bugReportPerStdev);
		bug = Math.min(bug, (double) b);
		return bug;
	}

	double distributeTesting(double testingTimeMean, double testingTimeStdev,
			int f1, int lTot)
	// *** time to test is proportional
	// to the number of LOC in the software piece examines (F(i,j)) and
	// proportional to the total LOC in the project
	// so far (L_tot(t)).
	{
		double tim, ba, boo;
		ba = (f1 - 1000) * 0.001;
		boo = (lTot - numberOfModules * 1000) * 0.00001;
		tim = logNormal(testingTimeMean, testingTimeStdev) + Math.max(ba, 0.)
				+ Math.max(boo, 0.);
		return tim;
	}

	double logNormal(double mean, double dev) {
		double x;
		double y;
		double r1;
		double r2;
		double mean2;
		double var2;
		double la;

		r1 = myRand.nextDouble() / 1.0d;
		r2 = myRand.nextDouble() / 1.0d;

		la = 1. + dev * dev / (mean * mean);
		var2 = Math.log(la);
		mean2 = Math.log(mean / Math.sqrt(la));

		x = mean2 + Math.sqrt(-2.0 * var2 * Math.log(1.0 - r1))
				* Math.cos(M_PI * r2);
		y = Math.exp(x);
		return y;
	}

	double parabolic(double max, double range, double offset, double ran) {

		double a;
		double y;

		if (range < 0.1) {
			y = 0.;

		} else {

			a = (1. - range * offset)
					/ (max * range * 0.5 - range * range / 3.);
			y = newton(ran, a, max, range, offset);
		}
		return y;
	}

	private double newton(double r, double A, double max, double range,
			double offset) {

		double y;
		double yOld;
		double acc;
		double limitUp;
		double limitDown;
		int i;

		limitUp = range + 0.00001;
		limitDown = -0.00001;

		do {
			y = range * myRand.nextDouble() / 1.0d;
			i = 0;
			do {
				yOld = y;
				y -= ((A * (max * y * y * 0.5 - y * y * y / 3.) + offset * y
						* range - r) / (A * (max * y - y * y) + offset * range));
				acc = Math.abs(y - yOld);
				i++;
			} while ((acc > 0.00001) && (i < 15));
		} while ((y < limitDown) || (y > limitUp));

		return y;
	}
	
	public LinkedList<SimulationMax> getMaxRecords()
	{
		return maxRecords;
	}
	
	public LinkedList<SimulationMin> getMinRecords()
	{
		return minRecords;
	}
	
	public LinkedList<SimulationChi> getChiRecords()
	{
		return chiRecords;
	}
	
	public LinkedList<SimulationResults> getResultRecords()
	{
		return resultRecords;
	}
	
}