/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Session;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.utils.DatabaseQueries;
import eu.sqooss.impl.service.web.services.utils.WSPair;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.FileMetadata;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

public class WebServicesImpl {
    
    private SecurityManager securityManager;
    private Logger logger;
    private DBService db;
    
    public WebServicesImpl(BundleContext bc, SecurityManager securityManager,
            DBService db, Logger logger) {
        this.securityManager = securityManager;
        this.db = db;
        this.logger = logger;
    }
    
    /* project's methods */
    
    //5.1.1
    public WSStoredProject[] evaluatedProjectsList(String userName, String password) {
        logger.info("Gets the evaluated project list! user: " + userName);

        //TODO: check the security

        List queryResult = db.doHQL(DatabaseQueries.EVALUATED_PROJECTS_LIST);

        return makeUnoinByStoredProjectId(queryResult);
    }
    
    public WSMetric[] retrieveMetrics4SelectedProject(String userName,
            String password, String projectId) {
        
        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_PROJECT_PARAM, Long.parseLong(projectId));
        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_PROJECT, queryParameters);
        
        return convertToWSMetrics(queryResult);
    }
    
    public WSMetric retrieveSelectedMetric(String userName, String password,
            String projectId, String metricId) {
        
        logger.info("Retrieve selected metric! user: " + userName +
                "; project id: " + projectId + "; metricId: " + metricId);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(2);
        queryParameters.put(DatabaseQueries.RETRIEVE_SELECTED_METRIC_PARAM_PR, Long.parseLong(projectId));
        queryParameters.put(DatabaseQueries.RETRIEVE_SELECTED_METRIC_PARAM_METRIC, Long.parseLong(metricId));

        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_SELECTED_METRIC, queryParameters);
        
        if (queryResult.size() == 1) {
            Object[] elem = (Object[]) queryResult.get(0);
            return new WSMetric((Metric) elem[0], (MetricType) elem[1]);
        } else {
            return null;
        }
    }
    //5.1.1
    
    //5.1.2
    public WSProjectFile[] retrieveFileList(String userName, String password, String projectId) {
        logger.info("Retrieve file list! user: " + userName + "; project id: " + projectId);
        
        //TODO: check the security
        
        return null;
        //TODO: uncomment after DB schema fix
//        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
//        queryParameters.put(DatabaseQueries.RETRIEVE_FILE_LIST_PARAM, Long.parseLong(projectId));
//        
//        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_FILE_LIST, queryParameters);
//        
//        if (queryResult.size() == 0) {
//            return null;
//        } else {
//            WSProjectFile[] result = new WSProjectFile[queryResult.size()];
//            Object[] currentElem;
//            for (int i = 0; i < result.length; i++) {
//                currentElem = (Object[]) queryResult.get(i);
//                result[i] = new WSProjectFile((ProjectFile) currentElem[0], (FileMetadata) currentElem[1]);
//            }
//            return result;
//        }
    }
    
    public WSMetric[] retrieveMetrics4SelectedFiles(String userName, String password,
            String projectId, String[] folders, String[] fileNames) {
        logger.info("Retrieve metrics for selected files! user: " + userName + "; project id: " + projectId);

        //TODO: check the security

        long projectIdValue = Long.parseLong(projectId);
        
        Set<String> fileNamesSet;
        if ((fileNames.length == 0) || (fileNames[0] == null)) {
            fileNamesSet = new HashSet<String>();
        } else {
            fileNamesSet = new HashSet<String>(Arrays.asList(fileNames));
        }
        
        if ((folders.length != 0) && (folders[0] != null)) {
            Map<String, Object> folderNameParameters = new Hashtable<String, Object>(1);
            List currentFileNames;
            for (String folder : folders) {
                folderNameParameters.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR,
                        projectIdValue);
                folderNameParameters.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM,
                        folder + "%");
                currentFileNames = db.doHQL(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_DIRS,
                        folderNameParameters);
                fileNamesSet.addAll(currentFileNames);
            }
        }
        
        List result = null;
        
        if (fileNamesSet.size() != 0) {
            //TODO remake after db feature request - 27.12.2007
            Session dbSession = db.getSession(this);
            Query dbQuery = dbSession.createQuery(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES);
            dbQuery.setParameter(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR,
                    projectIdValue);
            dbQuery.setParameterList(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_LIST,
                    fileNamesSet);
            result = dbQuery.list();
            db.returnSession(dbSession);
        }
        
        return convertToWSMetrics(result);
    }
    //5.1.2
    
    //5.1.3
    public void requestEvaluatin4Project(String userName, String password,
            String projectName, String projectVersion,
            String srcRepositoryLocation, String srcRepositoryType,
            String mailingListLocation, String BTSLocation) {
    }
    //5.1.3
    
    //5.1.4
    public WSPair[] requestPastEvolEstimProjects(String userName, String password) {
        return null;
    }
    
    public String[] requestProjectEvolutionEstimates(String userName, String password,
            String projectId, String startDate, String endDate) {
        return null;
    }
    
    public String[] requestProjectEvolutionEstimatesDuration(String userName, String password,
            String projectId, String duration) {
        return null;
    }
    
    public String[] requestEvolEstimates4Project(String userName, String password,
            String projectName, String projectVersion, String srcRepositoryLocation,
            String srcRepositoryType, String mailingListLocation, String BTSLocation) {
        return null;
    }
    //5.1.4
    
    //5.1.5
    public WSPair[] requestProjectsWithBTS(String userName, String password) {
        return null;
    }
    
    public String[] requestDefectStatistics(String userName, String password,
            String prokectId, String searchQuery, String statisticalScheme) {
        return null;
    }
    //5.1.5
    
    //5.1.6
    public WSPair[] retrieveDevelopers4SelectedProject(String userName, String password,
            String projectId) {
        return null;
    }
    
    public WSPair[] retrieveCriteria4SelectedDeveloper(String userName, String password,
            String projectId, String developerId) {
        return null;
    }
    
    public String displayDeveloperInfoTimeDiagram(String userName, String password,
            String projectId, String developerId, String criterioId,
            String tdStart, String tdEnd) {
        return null;
    }
    
    public String displayDeveloperInfo(String userName, String password,
            String projectId, String developerId, String criterioId, String display) {
        return null;
    }
    //5.1.6
    
    //5.1.7
    public WSPair[] evaluatedProjectsListScore(String userName, String password) {
        return null;
    }
    
    public void submitScores(String userName, String password, String projectId,
            String[] scores, String textOpinion) {
        
    }
    
    public String[] viewScores(String userName, String password, String projectId) {
        return null;
    }
    
    public String[] viewComments(String userName, String password, String projectId) {
        return null;
    }
    //5.1.7
    
    //5.1.8
    public WSPair[] ratedProjectsList(String userName, String password) {
        return null;
    }
    
    public String[] retrieveProjectRatings(String userName, String password,
            String projectId) {
        return null;
    }
    //5.1.8
    
    //5.1.9
    public String subscriptionsStatus(String userName, String password) {
        return null;
    }
    
    public void modifySubscriptions(String userName, String password,
            String newProjectNotification, String newMetricPlugin,
            String projectEvalFinished, String newProjectVersion,
            String newQualityRatings, String statistics) {
    }
    //5.1.9
    
    //5.1.10
    public void submitUser(String userNameForAccess, String passwordForAccess,
            String newAccountUserName, String newAccountSurname,
            String newAccountPassword, String newAccountUserClass) {
    }
    //5.1.10
    
    //5.1.11
    public WSPair[] displayUser(String userName, String password) {
        return null;
    }
    
    public void modifyUser(String userNameForAccess, String passwordForAccess,
            String modifyAccountUserName, String modifyAccountSurname,
            String modifyAccountPassword, String modifyAccountUserClass) {
    }
    
    public void deleteUser(String userNameForAccess, String passwordForAccess, String userId) {
        
    }
    //5.1.11
    
    /* project's methods */
    
//    private boolean checkSecurity(String userName, String password, String Url) {
//        SecurityManager security = (SecurityManager)securityTracker.getService();
//        if (security != null) {
//            return security.checkPermission(Url, userName, password);
//        } else {
//            return false;
//        }
//    }
//    
//    private boolean checkSecurity(String userName, String password, Hashtable<String, String> privileges, String resourceUrl) {
//        SecurityManager security = (SecurityManager)securityTracker.getService();
//        if (security != null) {
//            return security.checkPermission(resourceUrl, privileges, userName, password);
//        } else {
//            return false;
//        }
//    }
    
    private String makeUrl(String[] args) {
        int argsLength = args.length;
        if (argsLength >= 1) {
            StringBuffer url = new StringBuffer();
            url.append(args[0]);
            if ((argsLength >= 3) && ((argsLength % 2) == 1)) {
                url.append("?");
                for (int i = 1; i < argsLength; i+=2) {
                    url.append(args[i]);
                    url.append("=");
                    url.append(args[i+1]);
                    url.append("&");
                }
                url.deleteCharAt(url.length()-1); //delete last &
            }
            return url.toString();
        } else {
            return null;
        }
        
    }
    
    /**
     * The list's element must be an array from Objects.
     * The first object represents the StoredProject.
     * The second object represents the ProjectVersion.
     * The list must be sorted by StoredProject's id.
     * The method returns the array of the WSStoredProject.
     * Each WSStoredProject contains the stored project and its project versions.
     * @param list
     * @return
     */
    private WSStoredProject[] makeUnoinByStoredProjectId(List list) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        int storedProjectIndex = 0;
        int projectVersionIndex = 1;
        int listSize = list.size();
        WSStoredProject[] result;
        WSStoredProject currentWSStoredProject;
        Object[] currentListElem = (Object[])list.get(0);
        StoredProject currentStoredProject = (StoredProject)currentListElem[storedProjectIndex];
        ProjectVersion currentProjectVersion = (ProjectVersion)currentListElem[projectVersionIndex];
        if (listSize == 1) {
            currentWSStoredProject = new WSStoredProject(currentStoredProject, currentProjectVersion);
            result = new WSStoredProject[] {currentWSStoredProject};
        } else {
            Object[] nextListElem;
            StoredProject nextStoredProject;
            ProjectVersion nextProjectVersion;
            List<WSStoredProject> union = new Vector<WSStoredProject>();
            List<ProjectVersion> projectVersions = new Vector<ProjectVersion>();
            projectVersions.add(currentProjectVersion);
            for (int i = 1; i < listSize; i++) {
                nextListElem = (Object[])list.get(i);
                nextStoredProject = (StoredProject)nextListElem[storedProjectIndex];;
                nextProjectVersion = (ProjectVersion)nextListElem[projectVersionIndex];
                if (currentStoredProject.getId() == nextStoredProject.getId()) {
                    projectVersions.add(nextProjectVersion);
                } else {
                    currentWSStoredProject = new WSStoredProject(currentStoredProject, projectVersions);
                    union.add(currentWSStoredProject);
                    projectVersions.clear();
                    projectVersions.add(nextProjectVersion);
                    currentStoredProject = nextStoredProject;
                }
            }
            if (!projectVersions.isEmpty()) {
                currentWSStoredProject = new WSStoredProject(currentStoredProject, projectVersions);
                union.add(currentWSStoredProject);
            }
            result = union.toArray(new WSStoredProject[0]);
        }
        return result;
    }
    
    private WSMetric[] convertToWSMetrics(List metricsWithTypes) {
        WSMetric[] result = null;
        if ((metricsWithTypes != null) && (metricsWithTypes.size() != 0)) {
            result = new WSMetric[metricsWithTypes.size()];
            Object[] currentElem;
            for (int i = 0; i < result.length; i++) {
                currentElem = (Object[]) metricsWithTypes.get(i);
                result[i] = new WSMetric((Metric) currentElem[0], (MetricType) currentElem[1]);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
