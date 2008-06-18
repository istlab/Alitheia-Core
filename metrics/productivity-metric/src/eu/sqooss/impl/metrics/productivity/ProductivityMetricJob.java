/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.productivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionCategory;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionType;
import eu.sqooss.metrics.productivity.db.ProductivityActions;
import eu.sqooss.metrics.productivity.db.ProductivityWeights;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.logging.Logger;

public class ProductivityMetricJob {
	
    //DAO of the project version that has to be measured
    private ProjectVersion pv;

    // Reference to the metric that created this job
    AbstractMetric parent = null;

    Logger log;
    AlitheiaCore core;
    DBService db = null;
    
    public ProductivityMetricJob(BundleContext bc, AbstractMetric owner, 
            ProjectVersion a) {
        parent = owner;
        pv = a;   
        
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);
        log = core.getLogManager().createLogger(Logger.NAME_SQOOSS_METRIC);
        db = core.getDBService();
    }
  
    public void run() {
        
        /* Read config options in advance*/        
        FileTypeMatcher.FileType fType;
        boolean isNew;
        ProjectFile prevFile;
        int locCurrent, locPrevious;
        Developer dev = pv.getCommitter();
        String commitMsg = pv.getCommitMsg();
        Set<ProjectFile> projectFiles = pv.getVersionFiles();
        
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin plugin = 
            core.getPluginAdmin().getImplementingPlugin("LOC");
        
        if (plugin != null) {
            locMetric = plugin.getSupportedMetrics();
        } else {
            log.error("Could not find the WC plugin");
            return;
        }
        
        PluginConfiguration pluginConf = parent.getConfigurationOption(
                ProductivityMetricImpl.CONFIG_CMF_THRES);
        
        if (pluginConf == null || 
                Integer.parseInt(pluginConf.getValue()) <= 0) {
            log.error("Plug-in configuration option " + 
                    ProductivityMetricImpl.CONFIG_CMF_THRES + " not found");
            return; 
        }
        
        Pattern bugNumberLabel = Pattern.compile("\\A.*(pr:|bug:).*\\Z",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Pattern pHatLabel = Pattern.compile(
                "\\A.*(ph:|pointy hat|p?hat:).*\\Z", Pattern.CASE_INSENSITIVE
                        | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m;

        //updateField(dev, ActionType.TCO, true, 1);
        //updateField(dev, ActionType.TCF, true, projectFiles.size());

        if (commitMsg.length() == 0) {
            updateField(dev, ActionType.CEC, false, 1);
        } else {
            m = bugNumberLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ActionType.CBN, true, 1);
            }
            m = pHatLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ActionType.CPH, true, 1);
            }
        }
      
        if (projectFiles.size() > Integer.parseInt(pluginConf.getValue())) {
            updateField(dev, ActionType.CMF, false, 1);
        }

        Iterator<ProjectFile> i = projectFiles.iterator();
        
        while (i.hasNext()) {
            ProjectFile pf = i.next();

            fType = FileTypeMatcher.getFileType(pf.getFileName());
            isNew = (pf.getStatus().equalsIgnoreCase("ADDED"));

            if (pf.getIsDirectory()) {
                if (isNew) {
                    updateField(dev, ActionType.CND, true, 1);
                }
            } else if (fType == FileTypeMatcher.FileType.SRC) {
                try {
                    locCurrent = plugin.getResult(pf, locMetric).getRow(0).get(0).getInteger();
                    if (isNew) {
                        updateField(dev, ActionType.CNS, true, 1);
                        locPrevious = 0;
                    } else {
                        prevFile = ProjectFile.getPreviousFileVersion(pf);
                        locPrevious = plugin.getResult(prevFile, locMetric).getRow(0).get(0).getInteger();
                    }
                    updateField(dev, ActionType.CAL, true, abs(locCurrent - locPrevious));
                    
                } catch (MetricMismatchException e) {
                    log.error("Results of LOC metric for project: "
                            + pv.getProject().getName() + " file: "
                            + pf.getFileName() + ", Version: "
                            + pv.getVersion() + " could not be retrieved: "
                            + e.getMessage());
                    return;
                }
            } else if (fType == FileTypeMatcher.FileType.BIN) {
                updateField(dev, ActionType.CBF, true, 1);
            } else if (fType == FileTypeMatcher.FileType.DOC) {
                updateField(dev, ActionType.CDF, true, 1);
            } else if (fType == FileTypeMatcher.FileType.TRANS) {
                updateField(dev, ActionType.CTF, true, 1);
            }
        }
    }
    
    private void updateField(Developer dev, ActionType actionType,
            boolean isPositive, int value) {
        
        ProductivityActions a = ProductivityActions.getProductivityAction(dev,
                actionType);

        if (a == null) {
            a = new ProductivityActions();
            a.setDeveloper(dev);
            a.setCategory(ActionCategory.getActionCategory(actionType));
            a.setType(actionType);
            a.setIsPositive(isPositive);
            a.setTotal(value);
            db.addRecord(a);
        } else {
            a.setTotal(a.getTotal() + value);
        }

        updateWeights(actionType);
    }
    
    private void updateWeights(ActionType actionType) {

        ActionCategory actionCategory = 
            ActionCategory.getActionCategory(actionType);

        long totalActions = ProductivityActions.getTotalActions();
        long totalActionsPerCategory = 
            ProductivityActions.getTotalActionsPerCategory(actionCategory);
        long totalActionsPerType = 
            ProductivityActions.getTotalActionsPerType(actionType);

        // update weight for the action type
        double weight = (double)(100 * totalActionsPerType) / (double)totalActionsPerCategory;

        ProductivityWeights a = ProductivityWeights.getWeight(actionType);
        
        if (a == null) {
            a = new ProductivityWeights();
            a.setType(actionType);
            a.setWeight(weight);
            db.addRecord(a);
        } else {
            a.setWeight(weight);
        }

        // update weight for the action category
        weight = (double)(100 * totalActionsPerCategory) / (double)totalActions;

        a = ProductivityWeights.getWeight(actionCategory);

        if (a == null) {
            a = new ProductivityWeights();
            a.setCategory(actionCategory);
            a.setWeight(weight);
            db.addRecord(a);
        } else {
            a.setWeight(weight);
        }
    }
    
    private int abs (int value){
        if (value < 0) 
            return -1 * value;
        else
            return value;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

