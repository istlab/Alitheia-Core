/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.service.metricactivator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.InvocationRule.ActionType;
import eu.sqooss.service.db.InvocationRule.ScopeType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class MetricActivatorImpl implements MetricActivator {

    /** The parent bundle's context object. */
    private BundleContext bc;

    private AlitheiaCore core;
    private Logger logger;
    private PluginAdmin pa;
    private DBService db;

    // Default action of the invocation rules chain
    private ActionType defaultAction = null;
    private Long defaultRuleId = null;
    private Long firstRuleId = null;
    private HashMap<Long,InvocationRule> rules = null;

    public MetricActivatorImpl(BundleContext bc, Logger logger) {
        this.bc=bc;

        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);

        this.logger = logger;
        this.pa = core.getPluginAdmin();
        this.db = core.getDBService();
    }

    public void initRules() {
        // Load all defined invocation rules
        if (rules == null) {
            rules = new HashMap<Long, InvocationRule>();
            InvocationRule defaultRule = InvocationRule.getDefaultRule(db);
            defaultRuleId = defaultRule.getId();
            defaultAction = ActionType.fromString(defaultRule.getAction());
            InvocationRule rule = InvocationRule.first(db);
            firstRuleId = rule.getId();
            while (rule != null) {
                rules.put(rule.getId(), rule);
                rule = rule.next(db);
            }
        }
    }

    public void reloadRule (Long ruleId) {
        // Load the rules chain, if not done yet
        if(rules == null)
            initRules();

        // Invalid rule Id
        if (ruleId == null) return;
        // Retrieve the target rule from the database
        InvocationRule rule = db.findObjectById(
                InvocationRule.class, ruleId);
        // Rule update
        if (rule != null) {
            rules.put(rule.getId(), rule);
            // Check if this is the first rule in the chain
            if (rule.getPrevRule() == null) {
                firstRuleId = rule.getId();
            }
            // Check if this is the default rule in the chain
            if (rule.getId() == defaultRuleId.longValue()) {
                defaultAction = ActionType.fromString(rule.getAction());
            }
        }
        // Rule remove
        else {
            if (rules.containsKey(ruleId))
                rules.remove(ruleId);
        }
    }

    public ActionType matchRules (AlitheiaPlugin ap, DAObject resource) {
        // Load the rules chain, if not done yet
        if (rules == null)
            initRules();

        // Retrieve the first rule
        InvocationRule rule = rules.get(firstRuleId);

        // Retrieve the plug-in DAO
        Plugin plugin = null;
        if ((ap != null) && (ap.getUniqueKey() != null)) {
            HashMap<String, Object> properties =
                new HashMap<String, Object>();
            List<Plugin> plugins =
                db.findObjectsByProperties(Plugin.class, properties);
            if ((plugins != null) && (plugins.size() > 0)) {
                plugin = plugins.get(0);
            }
        }
        //====================================================================
        // Match against a resource of type ProjectFile
        //====================================================================
        if (resource instanceof ProjectFile) {
            // Skip on project file in state "DELETED"
            if (((ProjectFile) resource).getStatus().equals("DELETED")) {
                return defaultAction;
            }
            ProjectVersion version =
                ((ProjectFile) resource).getProjectVersion();
            StoredProject project = version.getProject();
            // Check for an invalid resource parameters
            if ((project == null)
                    || (plugin == null)
                    || (version == null)) {
                return defaultAction;
            }
            // Traverse through each rule until a match is found.
            while (rule != null) {
                // Check for the default rule
                if (rule.getId() == defaultRuleId) {
                    return defaultAction;
                }
                // Retrieve the rule's metric type
                Type metricType = null;
                if (rule.getMetricType() != null) {
                    metricType = (db.attachObjectToDBSession(
                            rule.getMetricType())).getEnumType();
                }
                // Skip on a metric type which can not evaluate that resource
                if ((metricType != null)
                        && (metricType != Type.SOURCE_CODE)
                        && (metricType != Type.PROJECT_WIDE)){
                    rule = rules.get(rule.getNextRule());
                    continue;
                }
                // Skip on a different project Id
                if ((rule.getProject() != null)
                        && (rule.getProject().getId() != project.getId())) {
                    rule = rules.get(rule.getNextRule());
                    continue;
                }
                // Skip on a different plug-in Id
                if ((rule.getPlugin() != null)
                        && (rule.getPlugin().getId() != plugin.getId())) {
                    rule = rules.get(rule.getNextRule());
                    continue;
                }
                // Skip on a different metric type
                boolean metricTypeNotFound = true;
                for (Metric metric : ap.getSupportedMetrics()) {
                    metric = db.attachObjectToDBSession(metric);
                    if (metric.getMetricType().getEnumType() == metricType) {
                        metricTypeNotFound = false;
                    }
                }
                if ((metricType != null) && (metricTypeNotFound)) {
                    rule = rules.get(rule.getNextRule());
                    continue;
                }
                // Match against the current rule
                if (rule.match(
                        ScopeType.fromString(rule.getScope()),
                        rule.getValue(),
                        (ProjectFile) resource)) {
                    // TODO: Remove the debug to speed up processing
                    logger.debug("Rule match: "
                            + ((ProjectFile) resource).getFileName()
                            + " : " + rule.getAction());
                    return ActionType.fromString(rule.getAction());
                }
                // Move to the next rule
                rule = rules.get(rule.getNextRule());
            }
        }
        // No matching rule found. Return the default action.
        return defaultAction;
    }
    
    private void runMetric(DAObject object, AbstractMetric metric, List<AbstractMetric> handled) {
    	if (handled.contains(metric))
    		return;

    	handled.add(metric);
    	
    	// meet the dependencies
    	List<String> dependencies = metric.getMetricDependencies();
    	for (String dependency : dependencies) {
    		AbstractMetric dependencyMetric = (AbstractMetric) pa.getImplementingPlugin(dependency);
    		if (dependencyMetric==null) {
    			logger.error("No metric found to fulfill the dependency \"" + dependency + "\"");
    			continue;
    		}
    		runMetric(object, dependencyMetric, handled);
    	}
    	
    	try {
			metric.run(object);
		} catch (MetricMismatchException e) {
            logger.warn("Metric " + metric.getName() + " failed");
		}
    }
    
 	private void runMetric(DAObject object, Map<Class<?>,List<PluginInfo>> metricsForClass, List<DAObject> handled, List<DAObject> allToBeHandled) {
		if (handled.contains(object))
			return;

		handled.add(object);

 		Class<? extends DAObject> c = object.getClass();
		if (metricsForClass.get(c)==null) {
			metricsForClass.put(c, pa.listPluginProviders(c));
		}
		List<PluginInfo> metrics = metricsForClass.get(c);
		if (metrics == null || metrics.size() == 0) {
			logger.warn("No metrics found for activation type " + c.getName());
		    return;
		}
    	
		// if we have a StoredProject, make sure all it's ProjectVersions are handled first
		if (object instanceof StoredProject) {
			StoredProject p = (StoredProject)object;
			List<ProjectVersion> versions = p.getProjectVersions();
			
			for (ProjectVersion version : versions) {
				if (allToBeHandled.contains(version) && !handled.contains(version))
					runMetric(version, metricsForClass, handled, allToBeHandled);
			}
		}
		
		// if we have a ProjectVersion, make sure all it's ProjectFiles are handled first
		if (object instanceof ProjectVersion) {
			ProjectVersion v = (ProjectVersion)object;
			Set<ProjectFile> files = v.getVersionFiles();
			
			for (ProjectFile file : files) {
				if (allToBeHandled.contains(file) && !handled.contains(file))
					runMetric(file, metricsForClass, handled, allToBeHandled);
			}
		}
		
		List<AbstractMetric> allMetrics = new ArrayList<AbstractMetric>(metrics.size());
		List<AbstractMetric> executedMetrics = new ArrayList<AbstractMetric>(metrics.size());
		for (PluginInfo metric : metrics) {
			AbstractMetric plugin = (AbstractMetric) bc.getService(metric.getServiceRef());
			if (plugin==null)
				continue;
			allMetrics.add(plugin);
		}

		for (AbstractMetric m : allMetrics) {
			runMetric(object, m, executedMetrics);
		}
    }
    
    public void runMetrics(List<DAObject> objects) {
    	Map<Class<?>,List<PluginInfo>> metricsForClass = new HashMap<Class<?>,List<PluginInfo>>();
    	
    	// this is the list of DAObjects we already handled
    	List<DAObject> handled = new ArrayList<DAObject>(objects.size());
    	
    	for(DAObject object : objects) {
    		runMetric(object, metricsForClass, handled, objects);
    	}
    }

    public void syncMetric(AlitheiaPlugin m, StoredProject sp) {
        PluginInfo mi = pa.getPluginInfo(m);

    }

    public <T extends DAObject> void syncMetrics(StoredProject sp) {

    }
}

//vi: ai nosi sw=4 ts=4 expandtab
