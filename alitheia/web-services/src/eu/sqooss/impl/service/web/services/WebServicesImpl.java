/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
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
import eu.sqooss.service.tds.TDSService;

public class WebServicesImpl {
    
    private SecurityManager securityManager;
    private Logger logger;
    private DBService db;
    private TDSService tds;
    
    public WebServicesImpl(BundleContext bc, SecurityManager securityManager,
            DBService db, TDSService tds, Logger logger) {
        this.securityManager = securityManager;
        this.db = db;
        this.tds = tds;
        this.logger = logger;
    }
    
    /* project's methods */
    
    //5.1.1
    /**
     * @see eu.sqooss.service.web.services.WebServices#evaluatedProjectsList(String, String)
     */
    public WSStoredProject[] evaluatedProjectsList(String userName, String password) {
        logger.info("Gets the evaluated project list! user: " + userName);

        //TODO: check the security

        List queryResult = db.doHQL(DatabaseQueries.EVALUATED_PROJECTS_LIST);

        return makeUnoinByStoredProjectId(queryResult);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveMetrics4SelectedProject(String, String, String)
     */
    public WSMetric[] retrieveMetrics4SelectedProject(String userName,
            String password, long projectId) {
        
        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_PROJECT_PARAM, projectId);
        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_PROJECT, queryParameters);
        
        return convertToWSMetrics(queryResult);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveSelectedMetric(String, String, String, String)
     */
    public WSMetric retrieveSelectedMetric(String userName, String password,
            long projectId, long metricId) {
        
        logger.info("Retrieve selected metric! user: " + userName +
                "; project id: " + projectId + "; metricId: " + metricId);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(2);
        queryParameters.put(DatabaseQueries.RETRIEVE_SELECTED_METRIC_PARAM_PR, projectId);
        queryParameters.put(DatabaseQueries.RETRIEVE_SELECTED_METRIC_PARAM_METRIC, metricId);

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
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveFileList(String, String, String)
     */
    public WSProjectFile[] retrieveFileList(String userName, String password, long projectId) {
        logger.info("Retrieve file list! user: " + userName + "; project id: " + projectId);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(DatabaseQueries.RETRIEVE_FILE_LIST_PARAM, projectId);
        
        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_FILE_LIST, queryParameters);
        
        return convertToWSProjectFiles(queryResult);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveMetrics4SelectedFiles(String, String, String, String[], String[])
     */
    public WSMetric[] retrieveMetrics4SelectedFiles(String userName, String password,
            long projectId, String[] folders, String[] fileNames) {
        logger.info("Retrieve metrics for selected files! user: " + userName + "; project id: " + projectId);

        //TODO: check the security

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
                        projectId);
                folderNameParameters.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM,
                        folder + "%");
                currentFileNames = db.doHQL(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_DIRS,
                        folderNameParameters);
                fileNamesSet.addAll(currentFileNames);
            }
        }
        
        List result = null;
        
        if (fileNamesSet.size() != 0) {
            Map<String, Object> projectIdParameter = new Hashtable<String, Object>(1);
            projectIdParameter.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR,
                    projectId);
            Map<String, Collection> fileNamesParameter = new Hashtable<String, Collection>(1);
            fileNamesParameter.put(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_LIST,
                    fileNamesSet);
            result = db.doHQL(DatabaseQueries.RETRIEVE_METRICS_4_SELECTED_FILES,
                    projectIdParameter, fileNamesParameter);
        }
        
        return convertToWSMetrics(result);
    }
    //5.1.2
    
    //5.1.3
    /**
     * @see eu.sqooss.service.web.services.WebServices#requestEvaluation4Project(String, String, String, int, String, String, String, String, String)
     */
    public WSStoredProject requestEvaluation4Project(String userName, String password,
            String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress, String website) {
        logger.info("Request evaluation for project! user: " + userName +
                "; project name: " + projectName + "; projectVersion: " + projectVersion +
                ";\n source repository: " + srcRepositoryLocation +
                "; mailing list: " + mailingListLocation +
                ";\n BTS: " + BTSLocation + "; user's e-mail: " + userEmailAddress +
                "; website: " + website);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(2);
        queryParameters.put(DatabaseQueries.REQUEST_EVALUATION_4_PROJECT_PARAM_PR_NAME, projectName);
        queryParameters.put(DatabaseQueries.REQUEST_EVALUATION_4_PROJECT_PARAM_PR_VERSION, projectVersion);
        
        List result;
        
        result = db.doHQL(DatabaseQueries.REQUEST_EVALUATION_4_PROJECT, queryParameters);
        
        if (result.size() == 0) {
            StoredProject newStoredProject = new StoredProject();
            newStoredProject.setBugs(BTSLocation);
            newStoredProject.setContact(userEmailAddress);
            newStoredProject.setMail(mailingListLocation);
            newStoredProject.setName(projectName);
            newStoredProject.setRepository(srcRepositoryLocation);
            newStoredProject.setWebsite(website);
            long newStoredProjectId;
            
            ProjectVersion newProjectVersion = new ProjectVersion();
            newProjectVersion.setVersion(projectVersion);
            
            Session dbSession = null;
            Transaction transaction = null;
            try {
            dbSession = db.getSession(this);
            transaction = dbSession.beginTransaction();
//            db.addRecord(dbSession, newStoredProject);
            dbSession.save(newStoredProject);
            newStoredProjectId = newStoredProject.getId();
            newProjectVersion.setProject(newStoredProject);
//            db.addRecord(dbSession, newProjectVersion);
            dbSession.save(newProjectVersion);
            transaction.commit();
            db.returnSession(dbSession);
            } catch (HibernateException he) {
                if (transaction != null) {
                    transaction.rollback();
                }
                db.returnSession(dbSession);
                throw he;
            }
            if (!tds.projectExists(newStoredProjectId)) {
                tds.addAccessor(newStoredProjectId, projectName, BTSLocation,
                        mailingListLocation, srcRepositoryLocation);
            }
            return new WSStoredProject(newStoredProject, newProjectVersion);
        } else if (result.size() == 1) {
            return makeUnoinByStoredProjectId(result)[0];
        } else {
            String message = "The database contains more than 1 project! name:" + 
            projectName + "; version: " + projectVersion;
            logger.warn(message);
            throw new RuntimeException(message);
        }
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
    /**
     * @see eu.sqooss.service.web.services.WebServices#submitUser(String, String, String, String, String, String, String)
     */
    public WSUser submitUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) {
        
        //TODO: check the security
        
        //TODO: add all fields to the security
        return new WSUser(securityManager.createUser(newUserName, newPassword));
        
    }
    //5.1.10
    
    //5.1.11
    /**
     * @see eu.sqooss.service.web.services.WebServices#displayUser(String, String, long)
     */
    public WSUser displayUser(String userNameForAccess, String passwordForAccess,
            long userId) {
        
        //TODO: check the security
        
        return new WSUser(securityManager.getUser(userId));
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#modifyUser(String, String, String, String, String, String, String)
     */
    public void modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) {
        
        //TODO: check the security and implement
        
        securityManager.modifyUser(null);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#deleteUser(String, String, long)
     */
    public void deleteUser(String userNameForAccess, String passwordForAccess, long userId) {
        
        //TODO: check the security
        
        securityManager.deleteUser(userId);
    }
    //5.1.11
    
    //retrieve methods
    public long retrieveProjectId(String userName, String passwrod, String projectName) {
        
        logger.info("Retrieve project id! user: " + userName +
                "; project name: " + projectName);
        
        //TODO: check the security
        
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(DatabaseQueries.RETRIEVE_PROJECT_ID_PARAM_PR_NAME, projectName);
        List queryResult = db.doHQL(DatabaseQueries.RETRIEVE_PROJECT_ID_PARAM, queryParameters);
        
        Long projectId;
        
        if (queryResult.size() != 0) {
            projectId = (Long) queryResult.get(0);
            return projectId;
        } else {
            throw new IllegalArgumentException("Can't find the project with name: " + projectName);
        }
        
    }
    //retrieve methods
    
    //validation
    public boolean validateAccount(String userName, String password) {
        //TODO:
        return true;
    }
    //validation
    
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
    
    private WSProjectFile[] convertToWSProjectFiles(List projectFilesWithMetadata) {
        WSProjectFile[] result = null;
        if ((projectFilesWithMetadata != null) && (projectFilesWithMetadata.size() != 0)) {
            result = new WSProjectFile[projectFilesWithMetadata.size()];
            Object[] currentElem;
            for (int i = 0; i < result.length; i++) {
                currentElem = (Object[]) projectFilesWithMetadata.get(i);
                result[i] = new WSProjectFile((ProjectFile) currentElem[0], (FileMetadata) currentElem[1]);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
