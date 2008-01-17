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
import eu.sqooss.service.pa.ConfigUtils;
import eu.sqooss.service.pa.MetricConfig;
import eu.sqooss.service.pa.MetricInfo;
import eu.sqooss.service.pa.PluginAdmin;

public class PAServiceImpl implements PluginAdmin, ServiceListener {

    // The folder (relative to the Equinox root) where the default
    // configuration files are located
    private static final String CONF_DIR =
        "configuration";

    // File separator (as retrieved from the system)
    private static final String FILE_SEP =
        System.getProperty("file.separator");

    // Current working directory (expected to point to the Equinox's root)
    private static final String CWD_PATH =
        System.getProperty("user.dir");

    /************************************************************************
     * STANDARD LOG MESSAGE
     */ 
    private static final String NO_MATCHING_SERVICES =
        "No matching services were found!";
    private static final String NOT_A_METRIC =
        "Not a metric service!";
    private static final String INVALID_FILTER_SYNTAX =
        "Invalid filter syntax!";
    private static final String CANT_GET_SOBJ =
        "The service object can not be retrieved!";

    // Store our parent bundle's context here
    private BundleContext bc;

    // Keeps a list of registered metric services, indexed by service ID
    private HashMap<Long, MetricInfo> metrics_available =
        new HashMap<Long, MetricInfo>();

    // Holds the current set of metrics configurations, indexed by class name
    private HashMap<String, MetricConfig> metrics_configurations =
        new HashMap<String, MetricConfig>();

    // Provides access to plug-ins configuration
    ConfigUtils config_reader = null;

    public PAServiceImpl (BundleContext bc) {
        this.bc = bc;

        // Read the default configuration file
        config_reader = new XMLConfigParser(
                CWD_PATH + FILE_SEP + CONF_DIR + FILE_SEP + "plugins.xml",
                CWD_PATH + FILE_SEP + CONF_DIR + FILE_SEP + "plugins.xsd");
        if (config_reader != null) {
            // ... and retrieve the available metrics configurations
            metrics_configurations = config_reader.getMetricsConfiguration();
        }

        // Collect information from pre-existing metric services
        this.collectMetricsInfo();

        // Attach this object as a listener for metric services
        try {
            bc.addServiceListener(
                    this,
                    "(" + Constants.OBJECTCLASS
                    + "=" + PluginAdmin.METRICS_CLASS + ")");
        } catch (InvalidSyntaxException e) {
            // TODO Use the Logger here
            System.out.println("Invalid filter string!");
        }

        // Register an extension to the Equinox console.
        //   - provides commands for managing metric services
        bc.registerService(
                CommandProvider.class.getName(),
                new PACommandProvider(this) ,
                null);
    }

/*    private Set<Long> getIDsFromClass (String class_name){
        Set<Long> matching_services = new TreeSet<Long>();
        
        // Retrieve the service IDs for all registered metrics
        Iterator<Long> service_ids = metrics_available.keySet().iterator();
        if (service_ids != null) {
            while (service_ids.hasNext()) {
                Long next_id = service_ids.next();
                
                MetricInfo next_metric = metrics_available.get(next_id);
                if ((next_metric != null)
                        && (next_metric.usesClassName(class_name))) {
                    matching_services.add(next_id);
                }
            }
        }
        
        return matching_services;
    }*/

    private MetricInfo getMetricInfo (ServiceReference sref_metric) {

        if (sref_metric != null) {
            MetricInfo metric_info = new MetricInfo();

            // Set the metric's service ID
            metric_info.setServiceID(
                    (Long) sref_metric.getProperty(Constants.SERVICE_ID));

            // Set the class name(s) of the object(s) used in the
            // service registration
            String[] metric_classes =
                (String[]) sref_metric.getProperty(Constants.OBJECTCLASS);
            metric_info.setObjectClass(metric_classes);

            // Set the ID and name of the bundle which has registered
            // this service
            metric_info.setBundleID(
                    sref_metric.getBundle().getBundleId());
            metric_info.setBundleName(
                    sref_metric.getBundle().getSymbolicName());

            // SQO-OSS Specific info fields
            Metric metric_object = (Metric) bc.getService(sref_metric);
            if (metric_object != null) {
                metric_info.setMetricName(metric_object.getName());
                metric_info.setMetricVersion(metric_object.getVersion());
            }

            return metric_info;
        }

        return null;
    }

    private void collectMetricsInfo() {
        // Format the search filter for metric services
        String metrics_filter =
            "(" + Constants.OBJECTCLASS +"=" + METRICS_CLASS + ")";

        // Retrieve a list of all registered metric services
        ServiceReference[] metrics_list = null;
        try {
            metrics_list = bc.getServiceReferences(null, metrics_filter);
        } catch (InvalidSyntaxException e) {
            logError("Invalid filter string!");
        }

        // Produce a list of registered metrics, if any where found
        if ((metrics_list != null) && (metrics_list.length > 0)) {

            for (int next_metric = 0;
            next_metric < metrics_list.length;
            next_metric++) {

                ServiceReference sref_metric = metrics_list[next_metric];
                MetricInfo metric_info = getMetricInfo(sref_metric);

                // Store this metric's info
                if (metric_info != null) {
                    metrics_available.put(
                            metric_info.getServiceID(),
                            metric_info);
                }
            }
        }
        else {
            logInfo("No pre-existing metrics were found!");
        }
    }

    private void metricRegistered (ServiceReference sref_metric) {
        // Retrieve the service ID
        Long service_id =
            (Long) sref_metric.getProperty(Constants.SERVICE_ID);
        logInfo("A metric service was registered with ID " + service_id);

        // Dispose from the list of available metric any old metric that
        // uses the same ID. Should not be required, as long as metric
        // services got properly unregistered.
        if (!metrics_available.containsKey(service_id)) {
            metrics_available.remove(service_id);
        }

        // Create an info object for this metric and add it to the list of
        // available metrics
        MetricInfo metric_info = getMetricInfo(sref_metric);
        metrics_available.put(service_id, metric_info);

        // Search for an applicable configuration set and apply it
        Iterator<String> config_sets =
            metrics_configurations.keySet().iterator();
        while (config_sets.hasNext()) {
            String class_name = config_sets.next();
            if (metric_info.usesClassName(class_name)) {
                // Apply the current configuration set to this metric
                logInfo(
                        "A configuration set was found for metric with"
                        + " object class name " + class_name
                        + " and service ID "    + service_id);
                MetricConfig config_set =
                    metrics_configurations.get(class_name);

                // Execute the necessary post-registration actions
                if (config_set != null) {
                    // Check if this metric needs to be automatically
                    // installed upon registration
                    if ((config_set.containsKey(MetricConfig.CFG_AUTOINSTALL)
                            && (config_set.get(MetricConfig.CFG_AUTOINSTALL)
                                    .equalsIgnoreCase("true")))) {
                        if (installMetric(service_id)) {
                            logInfo (
                                    "The install method of metric with"
                                    + " service ID " + service_id
                                    + " was successfully executed.");
                        }
                        else {
                            logError (
                                    "The install method of metric with"
                                    + " service ID " + service_id
                                    + " failed.");
                        }
                    }
                }
            }
        }

    }

    private void metricUnregistering (ServiceReference sref_metric) {
        logInfo("A metric service is unregistering.");

        // Retrieve the service ID
        Long service_ID =
            (Long) sref_metric.getProperty(Constants.SERVICE_ID);

        // Remove this service from the list of available metric services
        if (metrics_available.containsKey(service_ID)) {
            metrics_available.remove(service_ID);
        }
    }

    private void metricModified (ServiceReference sref_metric) {
        logInfo("A metric service configuration change.");
    }

/* ===[ Implementation of the ServiceListener Interface ]================= */

    public void serviceChanged(ServiceEvent event) {
        // Get a reference to the affected service
        ServiceReference affected_service = event.getServiceReference();

        // Find out what happened to the service
        switch (event.getType()) {
        // New service was registered
        case ServiceEvent.REGISTERED:
            metricRegistered(affected_service);
            break;
        // An existing service is unregistering  
        case ServiceEvent.UNREGISTERING:
            metricUnregistering(affected_service);
            break;
        // The configuration of an existing service was modified
        case ServiceEvent.MODIFIED:
            metricModified (affected_service);
        }
    }

/* ===[ Implementation of the PluginAdmin Interface ]===================== */

    public Collection<MetricInfo> listMetrics() {
        if (metrics_available.isEmpty() == false) {
            return metrics_available.values();
        }
        return null;
    }

    public boolean installMetric(Long sid) {
        // Format the search filter for metric services
        String service_filter =
            "(" + Constants.SERVICE_ID +"=" + sid + ")";
        logInfo (
                "Installing metric with service ID " + sid);

        final String INSTALL_FAILED =
            "The installation of metric with"
            + " service ID "+ sid
            + " failed : ";

        try {
            ServiceReference[] matching_services =
                bc.getServiceReferences(null, service_filter);
            if ((matching_services != null)
                    && (matching_services.length == 1)) {
                ServiceReference sref = matching_services[0];

                if (sref != null) {
                    try {
                        Metric sobj = (Metric) bc.getService(sref);
                        if (sobj != null) {
                            boolean installed = sobj.install();
                            if ((installed) &&
                                    (metrics_available.containsKey(sid))) {
                                MetricInfo metric_info =
                                    metrics_available.get(sid);
                                if (metric_info != null) {
                                    metric_info.installed = true;
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

    private void logError(String msg_text) {
        // TODO Use Logger instead
        System.out.println ("[ERROR] " + msg_text);
        
    }

    private void logWarning(String msg_text) {
        // TODO Use Logger instead
        System.out.println ("[WARNING] " + msg_text);
        
    }

    private void logInfo(String msg_text) {
        // TODO Use Logger instead
        System.out.println ("[INFO] " + msg_text);
        
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
