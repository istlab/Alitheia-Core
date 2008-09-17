/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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
import eu.sqooss.metrics.productivity.db.ProductivityActionType;
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
import eu.sqooss.service.fds.FileTypeMatcher;
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
        ProjectFile prevFile;
        int locCurrent, locPrevious;
        Developer dev = pv.getCommitter();
        String commitMsg = pv.getCommitMsg();
        Set<ProjectFile> projectFiles = pv.getVersionFiles();
        
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin plugin = 
            core.getPluginAdmin().getImplementingPlugin("Wc.loc");
        
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

            if (pf.getIsDirectory()) {
                //New directory added
                if (pf.isAdded()) {
                    updateField(dev, ActionType.CND, true, 1);
                }
            } else if (fType == FileTypeMatcher.FileType.SRC) {
                //Source file change, calc number of lines commited
                try {
                    //File deleted, set current lines to 0 
                    if (pf.isDeleted()) {
                        locCurrent = 0;
                    } else {
                        //Get lines of current version of the file from the wc metric
                        locCurrent = plugin.getResult(pf, locMetric).getRow(0).get(0).getInteger();
                    }
                    //Source file just added
                    if (pf.isAdded()) {
                        updateField(dev, ActionType.CNS, true, 1);
                        locPrevious = 0;
                    } else {
                        //Existing file, get lines of previous version
                        prevFile = ProjectFile.getPreviousFileVersion(pf);
                        if (prevFile != null) {
                            locPrevious = plugin.getResult(prevFile, locMetric).getRow(0).get(0).getInteger();
                        } else {
                            log.warn("Cannot get previous file " +
                            		"version for file id: " + pf.getId());
                            locPrevious = 0;
                        }
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
        
        //Check if it is required to update the weights
        pluginConf = parent.getConfigurationOption(
                ProductivityMetricImpl.CONFIG_WEIGHT_UPDATE_VERSIONS);
        
        if (pluginConf == null || 
                Integer.parseInt(pluginConf.getValue()) <= 0) {
            log.error("Plug-in configuration option " + 
                    ProductivityMetricImpl.CONFIG_WEIGHT_UPDATE_VERSIONS + " not found");
            return;
        }
        
        synchronized(getClass()){
            //long distinctVersions = calcDistinctVersions();
            long ts = (System.currentTimeMillis()/1000);
        	long previousVersions = ProductivityWeights.getLastUpdateVersionsCount();
            //Should the weights be updated?
            if (ts - previousVersions 
                    >= Integer.parseInt(pluginConf.getValue())){
                updateWeights(ts);
            }
        }
    }
    
    private long calcDistinctVersions() {
        List<?> distinctVersions = db.doHQL("select " +
        		"count(distinct projectVersion) from ProductivityActions");
        
        if(distinctVersions == null || 
                distinctVersions.size() == 0 || 
                distinctVersions.get(0) == null) {
            return 0L;
        }
        
        return (Long.parseLong(distinctVersions.get(0).toString())) ;
    }
    
    private void updateField(Developer dev, ActionType actionType,
            boolean isPositive, int value) {
       
        ProductivityActionType at = ProductivityActionType.getProductivityActionType(actionType, isPositive);
        
        if (at == null){
            db.rollbackDBSession();
            return;
        }
                
        ProductivityActions a = ProductivityActions.getProductivityAction(dev, pv, at);

        if (a == null) {
            a = new ProductivityActions();
            a.setDeveloper(dev);
            a.setProjectVersion(pv);
            a.setProductivityActionType(at);
            a.setTotal(value);
            db.addRecord(a);
        } else {
            a.setTotal(a.getTotal() + value);
        }
    }
    
    private void updateWeights(long secLastUpdate) {
        ActionCategory[] actionCategories = ActionCategory.values();

        long totalActions = ProductivityActions.getTotalActions();
        long totalActionsPerCategory;
        long totalActionsPerType;
        
        if (totalActions <= 0) {
            return;
        }
        
        for (int i = 0; i < actionCategories.length; i++) {
            //update action category weight
            totalActionsPerCategory = 
                ProductivityActions.getTotalActionsPerCategory(actionCategories[i]);
                
            if (totalActionsPerCategory <= 0) {
                continue;
            }
            
            updateActionCategoryWeight(actionCategories[i],
                    totalActionsPerCategory, totalActions, secLastUpdate);

            // update action types weights
            ArrayList<ActionType> actionTypes = 
                ActionType.getActionTypes(actionCategories[i]);

            for (int j = 0; j < actionTypes.size(); j++) {
                totalActionsPerType = 
                    ProductivityActions.getTotalActionsPerType(actionTypes.get(j));
                updateActionTypeWeight(actionTypes.get(j),totalActionsPerType, 
                        totalActionsPerCategory, secLastUpdate);
            }
        }
    }
    
    private void updateActionTypeWeight(ActionType actionType, 
            long totalActionsPerType, long totalActionsPerCategory, 
            long distinctVersions) {

        double weight = (double)(100 * totalActionsPerType) / 
            (double)totalActionsPerCategory;

        ProductivityWeights a = ProductivityWeights.getWeight(actionType);
       
        if (a == null) {
            a = new ProductivityWeights();
            a.setType(actionType);
            a.setWeight(weight);
            a.setLastUpdateVersions(distinctVersions);
            db.addRecord(a);
        } else {
            a.setLastUpdateVersions(distinctVersions);
            a.setWeight(weight);
        }
    }
    
    private void updateActionCategoryWeight(ActionCategory actionCategory, 
            long totalActionsPerCategory, long totalActions, 
            long distinctVersions){

        double weight = (double)(100 * totalActionsPerCategory) / 
            (double)totalActions;

        ProductivityWeights a = ProductivityWeights.getWeight(actionCategory);

        if (a == null) { //No weight calculated for this action yet
            a = new ProductivityWeights();
            a.setCategory(actionCategory);
            a.setWeight(weight);
            a.setLastUpdateVersions(distinctVersions);
            db.addRecord(a);
        } else {
            a.setLastUpdateVersions(distinctVersions);
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
