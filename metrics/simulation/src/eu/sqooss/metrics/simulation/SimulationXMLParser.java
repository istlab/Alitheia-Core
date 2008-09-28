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

import java.io.IOException;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.sqooss.metrics.simulation.SimulationParameters;

/**
 * SimulationXMLParser class parses the XML file that contains the default
 * values for the simulation and extracts them after it validates that the XML
 * follows the appropriate XSD schema<br>
 * 
 * @author Apostolos Kritikos <a
 *         href="mailto:akritiko@csd.auth.gr">(akritiko@csd.auth.gr)</a>
 */

public class SimulationXMLParser {

	/**
	 * The path to the XSD schema according to which the XML file in going to be
	 * validated
	 */
	private String simulationSchemaPath;

	/**
	 * The path to the XML file that contains the default values for the
	 * simulation
	 */
	private String simulationXMLPath;

	/**
	 * 
	 * @param simulationSchemaPath
	 * @param simulationXMLPath
	 */
	public SimulationXMLParser(String simulationSchemaPath, String simulationXMLPath) {

		this.simulationSchemaPath = simulationSchemaPath;
		this.simulationXMLPath = simulationXMLPath;
	}

	/**
	 * Parse simulation parses the XML file after it validates it and extracts
	 * the values. Eventually it creates a {@link SimulationParameters} object
	 * to hold the extracted values <br>
	 * 
	 * @return a {@link SimulationParameters} object
	 */
	public SimulationParameters parseSimulation() {

		Schema simulationSchema;
		Validator vSimulation;
		DocumentBuilder dBuilder;
		SimulationParameters parsedSimulationParameters = null;

		SchemaFactory sFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			simulationSchema = sFactory.newSchema(new StreamSource(
					simulationSchemaPath));
		} catch (SAXException e) {
			System.err.println(e.getMessage());
			System.err.println("The Schema: " + simulationSchemaPath
					+ " is INVALID");
			return parsedSimulationParameters;
		}

		vSimulation = simulationSchema.newValidator();

		// Getting a new instance of DocumentBuilderFactory and setting
		// parameters
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		dFactory.setIgnoringComments(true);
		dFactory.setIgnoringElementContentWhitespace(true);
		dFactory.setNamespaceAware(true);

		dBuilder = null;

		try {
			dBuilder = dFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Could not instansiate a Document Builder");
			return parsedSimulationParameters;
		}

		Document docSimulation = null;

		try {
			docSimulation = dBuilder.parse(simulationXMLPath);
			vSimulation.validate(new DOMSource(docSimulation));
		} catch (SAXException e) {
			System.err.println("The xml file:" + simulationXMLPath
					+ " is INVALID.");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error Reading file:" + simulationXMLPath);
			System.exit(-1);
		}

		// XML parse procedure (constructing the root element)
		Node root = docSimulation.getChildNodes().item(0).getFirstChild();

		// Parse initial conditions
		Node initialConditionParameters = root.getNextSibling().getFirstChild();

		// Modules
		Node modules = initialConditionParameters.getNextSibling()
				.getFirstChild();
		Node nextModule = modules.getNextSibling();

		Integer numberOfModules = 0; 
		Integer currentModuleNumberOfPieces = 0;
		Vector<Integer> softwarePieces = new Vector<Integer>(); 

		while (nextModule != null) {
			
			currentModuleNumberOfPieces = Integer.parseInt(nextModule
					.getFirstChild().getNextSibling().getNextSibling()
					.getNextSibling().getFirstChild().getNodeValue());
			softwarePieces.add(currentModuleNumberOfPieces);

			// Counts the number of total modules found in the configuration
			// file
			numberOfModules++;

			// Retrieves the next module
			nextModule = nextModule.getNextSibling().getNextSibling();
		}

		// Parse simulation parameters
		Node specificSimulationParameters = initialConditionParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();

		// Simulation steps
		Integer simulationSteps = Integer.parseInt(specificSimulationParameters
				.getNextSibling().getFirstChild().getNodeValue()); 

		// Number of repetitions
		Integer maximumRepetitions = Integer
				.parseInt(specificSimulationParameters.getNextSibling()
						.getNextSibling().getNextSibling().getFirstChild()
						.getNodeValue());

		// Time scale
		Double timeScale = Double.parseDouble(specificSimulationParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild()
				.getNodeValue());

		// Parse behavioral model fixed parameters
		Node bmFixedparameters = root.getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();

		// Parse programmers interest
		
		// ...for submission tasks
		Node submissionInterest = bmFixedparameters.getNextSibling()
				.getFirstChild().getNextSibling().getFirstChild();
		Node nextSubmissionInterest = submissionInterest.getNextSibling();

		// ...for debugging tasks
		Node debuggingInterest = bmFixedparameters.getNextSibling()
				.getFirstChild().getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();
		Node nextDebuggingInterest = debuggingInterest.getNextSibling();

		// ...for testing tasks
		Node testingInterest = bmFixedparameters.getNextSibling()
				.getFirstChild().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();
		Node nextTestingInterest = testingInterest.getNextSibling();

		// ...for functional improvement tasks
		Node functionalInterest = bmFixedparameters.getNextSibling()
				.getFirstChild().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild();
		Node nextFunctionalInterest = functionalInterest.getNextSibling();

		Vector<Double> programmersInterestForSubmissionTasks = new Vector<Double>(); 
		Double tempSubmissionInterest = 0.0;

		Vector<Double> programmersInterestForDebuggingTasks = new Vector<Double>(); 
		Double tempDebuggingInterest = 0.0;

		Vector<Double> programmersInterestForTestingTasks = new Vector<Double>(); 
		Double tempTestingInterest = 0.0;

		Vector<Double> programmersInterestForFunctionalImprovementTasks = new Vector<Double>(); 
		Double tempFunctionalInterest = 0.0;

		while (nextSubmissionInterest != null && nextDebuggingInterest != null
				&& nextTestingInterest != null
				&& nextFunctionalInterest != null) {

			tempSubmissionInterest = Double.parseDouble(nextSubmissionInterest
					.getFirstChild().getNodeValue());
			tempDebuggingInterest = Double.parseDouble(nextDebuggingInterest
					.getFirstChild().getNodeValue());
			tempTestingInterest = Double.parseDouble(nextTestingInterest
					.getFirstChild().getNodeValue());
			tempFunctionalInterest = Double.parseDouble(nextFunctionalInterest
					.getFirstChild().getNodeValue());

			programmersInterestForSubmissionTasks.add(tempSubmissionInterest);
			programmersInterestForDebuggingTasks.add(tempDebuggingInterest);
			programmersInterestForTestingTasks.add(tempTestingInterest);
			programmersInterestForFunctionalImprovementTasks
					.add(tempFunctionalInterest);

			// Retrieves the next module
			nextSubmissionInterest = nextSubmissionInterest.getNextSibling()
					.getNextSibling();
			nextDebuggingInterest = nextDebuggingInterest.getNextSibling()
					.getNextSibling();
			nextTestingInterest = nextTestingInterest.getNextSibling()
					.getNextSibling();
			nextFunctionalInterest = nextFunctionalInterest.getNextSibling()
					.getNextSibling();
		}

		// Parse task parameters
		Node taskParameters = bmFixedparameters.getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild();

		// Release frequency
		Double releasesFrequencyWeight = Double.parseDouble(taskParameters
				.getNextSibling().getFirstChild().getNodeValue());

		// LOC increment mean
		Double averageLOCIncrementWeight = Double.parseDouble(taskParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild().getNodeValue()); 

		// Commits mean
		Double averageCommitsIncrementWeight = Double
				.parseDouble(taskParameters.getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getFirstChild().getNodeValue()); 

		// Parse calibration parameters
		Node calibrationParameters = bmFixedparameters.getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();

		// Commits mean calibration
		Double calibrationAverageCommits = Double
				.parseDouble(calibrationParameters.getNextSibling()
						.getFirstChild().getNodeValue()); 

		// Release mean release mean calibration
		Double calibrationAverageReleases = Double
				.parseDouble(calibrationParameters.getNextSibling()
						.getNextSibling().getNextSibling().getFirstChild()
						.getNodeValue()); 

		// Contributors mean calibration
		Double calibrationAverageContributors = Double
				.parseDouble(calibrationParameters.getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getNextSibling().getFirstChild().getNodeValue()); 

		// LOC increment mean calibration
		Integer calibrationAverageLOCIncrement = Integer
				.parseInt(calibrationParameters.getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getFirstChild().getNodeValue()); 

		// Parse behavioral project parameters
		Node bmProjectParameters = root.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();

		// Parse aParameters
		Node aParameters = bmProjectParameters.getNextSibling().getFirstChild();

		// Core contributors
		Integer numberOfCoreContributors = Integer.parseInt(aParameters
				.getNextSibling().getFirstChild().getNodeValue()); 

		// Half life of programmers interest
		Double programmersInterestHalfLife = Double.parseDouble(aParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild().getNodeValue()); 

		// Standard deviation of the above variable
		Double programmersInterestHalfLifeStandardDeviation = Double
				.parseDouble(aParameters.getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getFirstChild().getNodeValue()); 

		// Parse bParameters
		Node bParameters = bmProjectParameters.getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild();

		// qZero
		Double calibrationParameter = Double.parseDouble(bParameters
				.getNextSibling().getFirstChild().getNodeValue()); 

		// Parse cParameters
		Node cParameters = bmProjectParameters.getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();

		// Project Release Interval
		Integer averageReleaseTime = Integer.parseInt(cParameters
				.getNextSibling().getFirstChild().getNodeValue());

		// Parse probability distribution parameters
		Node pdParameters = root.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild();

		// Parse lines of code parameters
		Node locParameters = pdParameters.getNextSibling().getFirstChild();

		Node modulesLOCMean = locParameters.getNextSibling().getFirstChild();
		Node nextModuleLOCMean = modulesLOCMean.getNextSibling();

		Node modulesLOCStDev = locParameters.getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();
		Node nextModuleLOCStDev = modulesLOCStDev.getNextSibling();

		Node completedLOCMean = locParameters.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();
		Node nextCompletedLOCMean = completedLOCMean.getNextSibling();

		Node completedLOCStDev = locParameters.getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();
		Node nextCompletedLOCStDev = completedLOCStDev.getNextSibling();

		Node funcImpLOCMean = locParameters.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();
		Node nextFuncImpLOCMean = funcImpLOCMean.getNextSibling();

		Node funcImpLOCStDev = locParameters.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();
		Node nextFuncImpLOCStDev = funcImpLOCStDev.getNextSibling();

		Vector<Integer> averageLOCAddedToModuleSubmission = new Vector<Integer>(); 
		Integer tempModuleLOCMean = 0;

		Vector<Integer> averageLOCAddedToModuleSubmissionStandardDeviation = new Vector<Integer>();
		Integer tempModuleStDev = 0;

		Vector<Double> functionallyCompleteModuleMaxLOC = new Vector<Double>(); 
		Double tempCompletedLOCMean = 0.0;

		Vector<Double> functionallyCompleteModuleMaxLOCStandardDeviation = new Vector<Double>(); 
		Double tempCompletedStDev = 0.0;

		Vector<Double> funcImprvTaskAverageLOCAdded = new Vector<Double>(); 
		Double tempFuncImpLOCMean = 0.0;

		Vector<Double> funcImprvTaskAverageLOCAddedStandardDeviation = new Vector<Double>(); 
		Double tempFuncImpStDev = 0.0;

		while (nextModuleLOCMean != null && nextModuleLOCStDev != null
				&& nextCompletedLOCMean != null
				&& nextCompletedLOCStDev != null && nextFuncImpLOCMean != null
				&& nextFuncImpLOCStDev != null) {

			tempModuleLOCMean = Integer.parseInt(nextModuleLOCMean
					.getFirstChild().getNodeValue());
			tempModuleStDev = Integer.parseInt(nextModuleLOCStDev
					.getFirstChild().getNodeValue());
			tempCompletedLOCMean = Double.parseDouble(nextCompletedLOCMean
					.getFirstChild().getNodeValue());
			tempCompletedStDev = Double.parseDouble(nextCompletedLOCStDev
					.getFirstChild().getNodeValue());
			tempFuncImpLOCMean = Double.parseDouble(nextFuncImpLOCMean
					.getFirstChild().getNodeValue());
			tempFuncImpStDev = Double.parseDouble(nextFuncImpLOCStDev
					.getFirstChild().getNodeValue());

			averageLOCAddedToModuleSubmission.add(tempModuleLOCMean);
			averageLOCAddedToModuleSubmissionStandardDeviation
					.add(tempModuleStDev);
			functionallyCompleteModuleMaxLOC.add(tempCompletedLOCMean);
			functionallyCompleteModuleMaxLOCStandardDeviation
					.add(tempCompletedStDev);
			funcImprvTaskAverageLOCAdded.add(tempFuncImpLOCMean);
			funcImprvTaskAverageLOCAddedStandardDeviation.add(tempFuncImpStDev);

			// Retrieves the next module
			nextModuleLOCMean = nextModuleLOCMean.getNextSibling()
					.getNextSibling();
			nextModuleLOCStDev = nextModuleLOCStDev.getNextSibling()
					.getNextSibling();
			nextCompletedLOCMean = nextCompletedLOCMean.getNextSibling()
					.getNextSibling();
			nextCompletedLOCStDev = nextCompletedLOCStDev.getNextSibling()
					.getNextSibling();
			nextFuncImpLOCMean = nextFuncImpLOCMean.getNextSibling()
					.getNextSibling();
			nextFuncImpLOCStDev = nextFuncImpLOCStDev.getNextSibling()
					.getNextSibling();
		}

		// Parse bugs parameters
		Node bugsParameters = pdParameters.getNextSibling().getNextSibling()
				.getNextSibling().getFirstChild();

		Double initialAverageBugsPerLOC = Double.parseDouble(bugsParameters
				.getNextSibling().getFirstChild().getNodeValue()); 

		Double initialAverageBugsPerLOCStandardDeviation = Double
				.parseDouble(bugsParameters.getNextSibling().getNextSibling()
						.getNextSibling().getFirstChild().getNodeValue()); 

		Double averageTimePerLOCProduction = Double.parseDouble(bugsParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getFirstChild()
				.getNodeValue()); 

		Double averageTimePerLOCProductionStandardDeviation = Double
				.parseDouble(bugsParameters.getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getFirstChild()
						.getNodeValue()); 

		Double averageTimePerBugFix = Double.parseDouble(bugsParameters
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild().getNodeValue()); 

		Double averageTimePerBugFixStandardDeviation = Double
				.parseDouble(bugsParameters.getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getNextSibling().getNextSibling().getNextSibling()
						.getFirstChild().getNodeValue()); 

		// Parse tests parameters
		Node testsParameters = pdParameters.getNextSibling().getNextSibling()
				.getNextSibling().getNextSibling().getNextSibling()
				.getFirstChild();

		// Release frequency
		Double averageTimePerTestReport = Double.parseDouble(testsParameters
				.getNextSibling().getFirstChild().getNodeValue()); 

		// Release frequency
		Double averageTimePerTestReportStandardDeviation = Double
				.parseDouble(testsParameters.getNextSibling().getNextSibling()
						.getNextSibling().getFirstChild().getNodeValue()); 

		parsedSimulationParameters = new SimulationParameters(numberOfModules,
				softwarePieces, simulationSteps, maximumRepetitions, timeScale,
				programmersInterestForSubmissionTasks,
				programmersInterestForDebuggingTasks,
				programmersInterestForTestingTasks,
				programmersInterestForFunctionalImprovementTasks,
				releasesFrequencyWeight, averageLOCIncrementWeight,
				averageCommitsIncrementWeight, calibrationAverageCommits,
				calibrationAverageReleases, calibrationAverageContributors,
				calibrationAverageLOCIncrement, numberOfCoreContributors,
				programmersInterestHalfLife,
				programmersInterestHalfLifeStandardDeviation,
				calibrationParameter, averageReleaseTime,
				averageLOCAddedToModuleSubmission,
				averageLOCAddedToModuleSubmissionStandardDeviation,
				functionallyCompleteModuleMaxLOC,
				functionallyCompleteModuleMaxLOCStandardDeviation,
				funcImprvTaskAverageLOCAdded,
				funcImprvTaskAverageLOCAddedStandardDeviation,
				initialAverageBugsPerLOC,
				initialAverageBugsPerLOCStandardDeviation,
				averageTimePerLOCProduction,
				averageTimePerLOCProductionStandardDeviation,
				averageTimePerBugFix, averageTimePerBugFixStandardDeviation,
				averageTimePerTestReport,
				averageTimePerTestReportStandardDeviation);
		return parsedSimulationParameters;
	}
}
