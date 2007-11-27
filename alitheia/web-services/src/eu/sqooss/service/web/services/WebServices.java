package eu.sqooss.service.web.services;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.sqooss.impl.service.web.services.WebServicesImpl;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

/* 
 * NOTES:
 * 
 * 1. The implementation is specially here. The Axis2's wsdl generator is the reason.
 * It doesn't work correct with the interfaces and the abstract classes.
 * 
 * 2. java2wsdl doesn't support methods overloading.
 * 
 */

/**
 * Web services service exports a single interface with all required function calls.
 * The URL is: http:/.../[web.service.context]/services/[web.service.name]
 * The wsdl file is: http:/.../[web.service.context]/services/[web.service.name]?wsdl
 */
public class WebServices {
    
    private WebServicesImpl webServices;
    
    public WebServices(BundleContext bc, ServiceTracker securityTracker) {
        webServices = new WebServicesImpl(bc, securityTracker);
    }
    
    
    /*metric's methods*/
    
    /**
     * 
     * Adds the metric from the given url.
     * 
     * @param userName
     * @param password
     * @param url
     * @return the metric id
     * @throws WebServicesException - if the metric's installation fails
     */
    public String addMetric(String userName, String password, String url) throws WebServicesException {
        return webServices.addMetric(userName, password, url);
    }
    
    /**
     * 
     * Removes the metric with the given id.
     * 
     * @param userName
     * @param password
     * @param metricId
     * @throws WebServicesException - if the remove operation fails
     */
    public void removeMetric(String userName, String password, String metricId) throws WebServicesException {
        webServices.removeMetric(userName, password, metricId);
    }
    
    /**
     * 
     * Returns the metric's id
     * 
     * @param userName
     * @param password
     * @param metricName
     * @param metricVersion
     * @return
     * @throws WebServicesException - if the operation fails
     */
    public String getMetricId(String userName, String password, String metricName, String metricVersion) throws WebServicesException {
        return webServices.getMetricId(userName, password, metricName, metricVersion);
    }
    
    /**
     * 
     * Returns a metric results for the project version.
     * 
     * @param userName
     * @param password
     * @param metricId
     * @param projectVersion
     * @return see eu.sqooss.metrics.abstractmetric.MetricResult.toXML()
     */
    public String getFileGroupMetricResult(String userName, String password,
            String metricId, ProjectVersion projectVersion) {
        return webServices.getFileGroupMetricResult(userName, password, metricId, projectVersion);
    }
    
    /**
     * 
     * Return a metric results for the file.
     * 
     * @param userName
     * @param password
     * @param metricId
     * @param projectFile
     * @return see eu.sqooss.metrics.abstractmetric.MetricResult.toXML()
     * @throws WebServicesException - if the operation fails
     */
    public String getProjectFileMetricResult(String userName, String password,
            String metricId, ProjectFile projectFile) throws WebServicesException {
        return webServices.getProjectFileMetricResult(userName, password, metricId, projectFile);
    }
    
    /**
     * 
     * Returns a metric results for the project version.
     * 
     * @param userName
     * @param password
     * @param metricId
     * @param projectVersion
     * @return see eu.sqooss.metrics.abstractmetric.MetricResult.toXML()
     * @throws WebServicesException - if the operation fails
     */
    public String getProjectVersionMetricResult(String userName, String password,
            String metricId, ProjectVersion projectVersion) throws WebServicesException {
        return webServices.getProjectVersionMetricResult(userName, password, metricId, projectVersion);
    }
    
    /**
     * 
     * Returns a metric results for the stored project.
     * 
     * @param userName
     * @param password
     * @param metricId
     * @param storedProject
     * @return see eu.sqooss.metrics.abstractmetric.MetricResult.toXML()
     * @throws WebServicesException - if the operation fails
     */
    public String getStoredProjectMetricResult(String userName, String password,
            String metricId, StoredProject storedProject) throws WebServicesException {
        return webServices.getStoredProjectMetricResult(userName, password, metricId, storedProject);
    }
    /*metric's methods*/

    /*project's methods*/
    
    /**
     * 
     * Starts the project's evaluation.
     * 
     * @param userName
     * @param password
     * @param storedProject
     * @throws WebServicesException - if the operation fails
     */
    public void startEvaluateProject(String userName, String password, StoredProject storedProject) {
        webServices.startEvaluateProject(userName, password, storedProject);
    }
    
    /**
     * 
     * Stops the project's evaluation.
     * 
     * @param userName
     * @param password
     * @param storedProject
     * @throws WebServicesException - if the operation fails
     */
    public void stopEvaluateProject(String userName, String password, StoredProject storedProject) {
        webServices.stopEvaluateProject(userName, password, storedProject);
    }
    /*project's methods*/
    
}
