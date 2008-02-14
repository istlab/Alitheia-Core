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
package eu.sqooss.impl.service.pa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Constants;

import org.eclipse.osgi.framework.console.CommandProvider;

import eu.sqooss.service.abstractmetric.Metric;
import eu.sqooss.service.abstractmetric.FileGroupMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.ConfigUtils;
import eu.sqooss.service.pa.MetricConfig;
import eu.sqooss.service.pa.MetricInfo;
import eu.sqooss.service.pa.PluginAdmin;

public class PAServiceImpl implements PluginAdmin, ServiceListener {

    /* ===[ Constants: System and configuration related ]================= */

    // The folder (relative to the Equinox root) where the default
    // configuration files are located
    // TODO: Better store this parameter in the Equinox's config.ini
    private static final String CONF_DIR =
        "configuration";

    // File separator (as retrieved from the host system)
    private static final String FILE_SEP =
        System.getProperty("file.separator");

    // Current working directory (expected to point to the Equinox's root)
    private static final String CWD_PATH =
        System.getProperty("user.dir");

    /* ===[ Constants: Service search filters ]=========================== */

    private static final String SREF_FILTER_METRIC =
        "(" + Constants.OBJECTCLASS + "=" + PluginAdmin.METRICS_CLASS + ")";

    /* ===[ Constants: Common log messages ]============================== */

    private static final String NO_MATCHING_SERVICES =
        "No matching services were found!";
    private static final String NOT_A_METRIC =
        "Not a metric service!";
    private static final String INVALID_FILTER_SYNTAX =
        "Invalid filter syntax!";
    private static final String CANT_GET_SOBJ =
        "The service object can not be retrieved!";


    /* ===[ Global variables ]============================================ */

    // Store our parent's bundle context here
    private BundleContext bc;

    // Keeps a list of registered metric services, indexed by service ID
    private HashMap<Long, MetricInfo> registeredMetrics =
        new HashMap<Long, MetricInfo>();

    // Holds the current set of metrics configurations, indexed by class name
    private HashMap<String, MetricConfig> metricConfigurations =
        new HashMap<String, MetricConfig>();

    // Provides routines for accessing a specified configuration file
    ConfigUtils configReader = null;

    /* ===[ Constructors ]================================================ */

    public PAServiceImpl (BundleContext bc) {
        this.bc = bc;

        // Read the default configuration file
        // TODO: Refactor the XSD schema and XML data file name constructors
        configReader = new XMLConfigParser(
                CWD_PATH + FILE_SEP + CONF_DIR + FILE_SEP + "plugins.xml",
                CWD_PATH + FILE_SEP + CONF_DIR + FILE_SEP + "plugins.xsd");
        // ... and retrieve all available metrics configurations
        if (configReader != null) {
            metricConfigurations = configReader.getMetricsConfiguration();
        }

        // Collect information about pre-existing metric services
        this.collectMetricsInfo();

        // Attach this object as a listener for metric services
        try {
            bc.addServiceListener(this, SREF_FILTER_METRIC);
        } catch (InvalidSyntaxException e) {
            logError(INVALID_FILTER_SYNTAX);
        }

        // Register an extension to the Equinox console, in order to
        // provide commands for managing metric services
        bc.registerService(
                CommandProvider.class.getName(),
                new PACommandProvider(this) ,
                null);
    }

    /**
     * Constructs a MetricInfo object, from the available information
     * regarding the selected metric service reference
     *
     * @param srefMetric the service reference object
     *
     * @return a MetricInfo object containing the extracted metric
     * information
     */
    private MetricInfo getMetricInfo (ServiceReference srefMetric) {

        if (srefMetric != null) {
            MetricInfo metricInfo = new MetricInfo();

            // Set the metric's service ID
            metricInfo.setServiceID(
                    (Long) srefMetric.getProperty(Constants.SERVICE_ID));

            // Set the class name(s) of the object(s) used in the
            // service registration
            String[] metric_classes =
                (String[]) srefMetric.getProperty(Constants.OBJECTCLASS);
            metricInfo.setObjectClass(metric_classes);

            // Set the ID and name of the bundle which has registered
            // this service
            metricInfo.setBundleID(
                    srefMetric.getBundle().getBundleId());
            metricInfo.setBundleName(
                    srefMetric.getBundle().getSymbolicName());

            // SQO-OSS related info fields
            Metric metric_object = (Metric) bc.getService(srefMetric);
            if (metric_object != null) {
                metricInfo.setMetricName(metric_object.getName());
                metricInfo.setMetricVersion(metric_object.getVersion());
            }

            return metricInfo;
        }

        return null;
    }

    /**
     * Collects information about all registered metrics
     */
    private void collectMetricsInfo() {
        // Retrieve a list of all references to registered metric services
        ServiceReference[] metricsList = null;
        try {
            metricsList = bc.getServiceReferences(null, SREF_FILTER_METRIC);
        } catch (InvalidSyntaxException e) {
            logError(INVALID_FILTER_SYNTAX);
        }

        // Retrieve information about all registered metrics found
        if ((metricsList != null) && (metricsList.length > 0)) {

            for (int nextMetric = 0;
            nextMetric < metricsList.length;
            nextMetric++) {

                ServiceReference sref_metric = metricsList[nextMetric];
                MetricInfo metric_info = getMetricInfo(sref_metric);

                // Add this metric's info to the list
                if (metric_info != null) {
                    registeredMetrics.put(
                            metric_info.getServiceID(),
                            metric_info);
                }
            }
        }
        else {
            logInfo("No pre-existing metrics were found!");
        }
    }

    /**
     * Performs various maintenance operations upon registration of a new
     * metric service
     *
     * @param srefMetric the reference to the registered metric service
     */
    private void metricRegistered (ServiceReference srefMetric) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefMetric.getProperty(Constants.SERVICE_ID);
        logInfo("A metric service was registered with ID " + serviceId);

        // Dispose from the list of available metric any old metric, that
        // uses the same ID. Should not be required, as long as metric
        // services got properly unregistered.
        if (!registeredMetrics.containsKey(serviceId)) {
            registeredMetrics.remove(serviceId);
        }

        // Retrieve information about this metric and add this metric to the
        // list of registered/available metrics
        MetricInfo metricInfo = getMetricInfo(srefMetric);
        registeredMetrics.put(serviceId, metricInfo);

        // Search for an applicable configuration set and apply it
        Iterator<String> configSets =
            metricConfigurations.keySet().iterator();
        while (configSets.hasNext()) {
            // Match is performed against the metric's class name(s)
            String className = configSets.next();
            // TODO: It could happen that a service get registered with more
            // than one class. In this case a situation can arise where
            // two or more matching configuration sets exists.
            if (metricInfo.usesClassName(className)) {
                // Apply the current configuration set to this metric
                logInfo(
                        "A configuration set was found for metric with"
                        + " object class name " + className
                        + " and service ID "    + serviceId);
                MetricConfig configSet =
                    metricConfigurations.get(className);

                // Execute the necessary post-registration actions
                if (configSet != null) {
                    // Checks if this metric has to be automatically
                    // installed upon registration
                    if ((configSet.containsKey(MetricConfig.KEY_AUTOINSTALL)
                            && (configSet.getString(MetricConfig.KEY_AUTOINSTALL)
                                    .equalsIgnoreCase("true")))) {
                        if (installMetric(serviceId)) {
                            logInfo (
                                    "The install method of metric with"
                                    + " service ID " + serviceId
                                    + " was successfully executed.");
                        }
                        else {
                            logError (
                                    "The install method of metric with"
                                    + " service ID " + serviceId
                                    + " failed.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Performs various maintenance operations during unregistering of a
     * metric service
     *
     * @param srefMetric the reference to the registered metric service
     */
    private void metricUnregistering (ServiceReference srefMetric) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefMetric.getProperty(Constants.SERVICE_ID);
        logInfo(
                "A metric service with ID "
                + serviceId + " is unregistering.");

        // Remove this service from the list of available metric services
        if (registeredMetrics.containsKey(serviceId)) {
            registeredMetrics.remove(serviceId);
        }
    }

    /**
     * Performs various maintenance operations upon a change in an existing
     * metric service
     *
     * @param srefMetric the reference to the registered metric service
     */
    private void metricModified (ServiceReference srefMetric) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefMetric.getProperty(Constants.SERVICE_ID);
        logInfo(
                "A metric service with ID "
                + serviceId + " was modified.");
    }

/* ===[ Implementation of the ServiceListener interface ]================= */

    public void serviceChanged(ServiceEvent event) {
        // Get a reference to the affected service
        ServiceReference affectedService = event.getServiceReference();

        // Find out what happened to the service
        switch (event.getType()) {
        // New service was registered
        case ServiceEvent.REGISTERED:
            metricRegistered(affectedService);
            break;
        // An existing service is unregistering
        case ServiceEvent.UNREGISTERING:
            metricUnregistering(affectedService);
            break;
        // The configuration of an existing service was modified
        case ServiceEvent.MODIFIED:
            metricModified (affectedService);
        }
    }

/* ===[ Implementation of the PluginAdmin interface ]===================== */

    public Collection<MetricInfo> listMetrics() {
        if (registeredMetrics.isEmpty() == false) {
            return registeredMetrics.values();
        }
        return null;
    }

    public boolean installMetric(Long sid) {
        // Format a search filter for the metric service with <sid> serviceId
        String serviceFilter =
            "(" + Constants.SERVICE_ID +"=" + sid + ")";
        logInfo (
                "Installing metric with service ID " + sid);

        final String INSTALL_FAILED =
            "The installation of metric with"
            + " service ID "+ sid
            + " failed : ";

        try {
            ServiceReference[] matchingServices =
                bc.getServiceReferences(null, serviceFilter);
            if ((matchingServices != null)
                    && (matchingServices.length == 1)) {
                // Since the search was performed using a serviceId, it must
                // be only one service reference that is found
                ServiceReference sref = matchingServices[0];

                if (sref != null) {
                    try {
                        // Retrieve the Metric object registered with this
                        // service
                        Metric sobj = (Metric) bc.getService(sref);
                        if (sobj != null) {
                            // Try to execute the install() method of this
                            // metric
                            boolean installed = sobj.install();

                            // If the install() is successful, then note this
                            // into the metric's information object
                            if ((installed) &&
                                    (registeredMetrics.containsKey(sid))) {
                                // Retrieve the corresponding information
                                // object
                                MetricInfo metricInfo =
                                    registeredMetrics.get(sid);
                                if (metricInfo != null) {
                                    metricInfo.installed = true;
                                }
                            }
                            return installed;
                        }
                        else {
                            logError (INSTALL_FAILED + CANT_GET_SOBJ);
                        }
                    } catch (ClassCastException e) {
                        logError (INSTALL_FAILED + NOT_A_METRIC);
                    } catch (Error e) {
                        logError (INSTALL_FAILED + e);
                    }
                }
                else {
                    logWarning(NO_MATCHING_SERVICES);
                }
            }
            else {
                logWarning(NO_MATCHING_SERVICES);
            }
        } catch (InvalidSyntaxException e) {
            logError(INVALID_FILTER_SYNTAX);
        }

        return false;
    }

    public ServiceReference[] listMetricProviders(DAObject o) {
        String targetClassName = null;
        if (o instanceof StoredProject)
            targetClassName = StoredProjectMetric.class.getName();
        else if (o instanceof ProjectVersion)
            targetClassName = ProjectVersionMetric.class.getName();
        else if (o instanceof ProjectFile)
            targetClassName = ProjectFileMetric.class.getName();
        else if (o instanceof FileGroup)
            targetClassName = FileGroupMetric.class.getName();
        else {
            // Just bail out if we don't know what to do with this
            return null;
        }

        ServiceReference[] metricsList = null;
        try {
            metricsList = bc.getServiceReferences(targetClassName, SREF_FILTER_METRIC);
        } catch (InvalidSyntaxException e) {
            logError(INVALID_FILTER_SYNTAX);
            return null;
        }

        return metricsList;
    }
    
    public ServiceReference[] listProjectVersionMetrics() {
        try {
            return bc.getServiceReferences(ProjectVersionMetric.class.getName(), SREF_FILTER_METRIC);
        } catch (InvalidSyntaxException e) {
            logError(INVALID_FILTER_SYNTAX);
            return null;
        }
    }

    private void logError(String msgText) {
        // TODO Use Logger instead
        System.out.println ("[ERROR] " + msgText);

    }

    private void logWarning(String msgText) {
        // TODO Use Logger instead
        System.out.println ("[WARNING] " + msgText);

    }

    private void logInfo(String msgText) {
        // TODO Use Logger instead
        System.out.println ("[INFO] " + msgText);

    }

}

//vi: ai nosi sw=4 ts=4 expandtab
