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

package eu.sqooss.impl.metrics.contrib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionCategory;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionType;
import eu.sqooss.metrics.contrib.ContributionMetric;
import eu.sqooss.metrics.contrib.db.ContribAction;
import eu.sqooss.metrics.contrib.db.ContribActionType;
import eu.sqooss.metrics.contrib.db.ContribActionWeight;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.pa.PluginInfo;

public class ContributionMetricImpl extends AbstractMetric implements
        ContributionMetric {

    public static final String CONFIG_CMF_THRES = "CMF_threshold";
    public static final String CONFIG_WEIGHT_UPDATE_VERSIONS = "Weights_Update_Interval";
    
    public ContributionMetricImpl(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectVersion.class);
        super.addActivationType(Developer.class);
        super.addActivationType(MailMessage.class);
        super.addActivationType(Bug.class);
        
        super.addMetricActivationType("CONTRIB", Developer.class);
        
        super.addDependency("Wc.loc");
    }
    
    public boolean install() {
    	 boolean result = super.install();
         if (result) {
             result &= super.addSupportedMetrics(
                     "Developer Contribution Metric",
                     "CONTRIB",
                     MetricType.Type.PROJECT_WIDE);
         
             addConfigEntry(CONFIG_CMF_THRES, 
                 "5" , 
                 "Number of committed files above which the developer is " +
                 "penalized", 
                 PluginInfo.ConfigurationType.INTEGER);
             addConfigEntry(CONFIG_WEIGHT_UPDATE_VERSIONS, 
                 "150" , 
                 "Number of revisions between weight updates", 
                 PluginInfo.ConfigurationType.INTEGER);
         }
         return result;
    }
    
    public boolean remove() {
        boolean result = true;
        
        String[] tables = {"ContribActionWeight", 
                           "ContribAction",
                           "ContribActionType"};
        
        for (String tablename : tables) {
            result &= db.deleteRecords((List<DAObject>) db.doHQL(
                    "from " + tablename));
        }
        
        result &= super.remove();
        return result;
    }
    
    /**{@inheritDoc}*/
    public boolean cleanup(DAObject sp) {
        boolean result = true;
        
        if (!(sp instanceof StoredProject)) {
            log.warn("We only support cleaning up per stored project for now");
            return false;
        }
        result &= cleanupResource (((StoredProject)sp).getProjectVersions(), 
                ActionCategory.C);
        result &= cleanupResource(((StoredProject)sp).getBugs(), 
                ActionCategory.B);
        
        Set<MailingList> mlists = ((StoredProject) sp).getMailingLists();
        for (MailingList ml : mlists) {
            result &= cleanupResource(ml.getMessages(), ActionCategory.M);            
        }
       
        return result;
    }

    private boolean cleanupResource (Collection<? extends DAObject> c, 
            ActionCategory ac) {
        
        Map<String,Object> params = new HashMap<String,Object>();
        boolean result = false;
        
        for(DAObject o : c) {
            params.put("changedResourceId", o.getId());
            params.put("actionCategory", ac.toString());
            List<ContribAction> pas = 
                db.findObjectsByProperties(ContribAction.class, params);
            if (!pas.isEmpty()) {
                for (ContribAction pa : pas) {
                    result &= db.deleteRecord(pa);
                }
            }
            params.clear();
        }
        return result;
    }

    /*
     * The following methods are dummy implementations that just
     * check if a result has been calculated for the provided
     * DAO or not. 
     */
    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
       return checkResult(a, ActionCategory.C, m);
    }
    
    public List<ResultEntry> getResult(MailMessage mm, Metric m) {
        return checkResult(mm, ActionCategory.M, m);
    }
    
    public List<ResultEntry> getResult(Bug b, Metric m) {
        return checkResult(b, ActionCategory.B, m);
    }
    
    private List<ResultEntry> checkResult(DAObject o, ActionCategory ac, 
            Metric m) {
        ArrayList<ResultEntry> res = new ArrayList<ResultEntry>();
        String paramChResource = "paramChResource";
        String paramActionCategory = "paramActionCategory";
        
        String query = "select ca " +
            "from ContribAction ca, ContribActionType cat " +
            " where ca.contribActionType = cat " +
            " and cat.actionCategory = :" + paramActionCategory +
            " and ca.changedResourceId = :" + paramChResource ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramChResource, o.getId());
        parameters.put(paramActionCategory, ActionCategory.C.toString());

        List<ContribAction> lp = (List<ContribAction>) db.doHQL(query, parameters);
    
        if (lp == null || lp.isEmpty()) {
            return null;
        } 
        //Return a fixed result to indicate successful run on this 
        //project resource
        res.add(new ResultEntry(1, ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                m.getMnemonic()));
        return res;
    }

    /*
     * This plug-in's result is only returned per developer. 
     */
    public List<ResultEntry> getResult(Developer a, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        ContribActionWeight weight;
        double value = 0;

        ActionCategory[] actionCategories = ActionCategory.values();

        for (int i = 0; i < actionCategories.length; i++) {
            weight = ContribActionWeight.getWeight(actionCategories[i]);

            if (weight != null) {
                value = value + weight.getWeight() * 
                    getResultPerActionCategory(a, actionCategories[i]);
            }
        }

        ResultEntry entry = new ResultEntry(value,
                ResultEntry.MIME_TYPE_TYPE_DOUBLE, m.getMnemonic());
        results.add(entry);
        return results;
    }

    /**No need to activate per developer*/
    public void run(Developer v) {
        //log.debug("Running for developer " + v.toString());
    }
    
    public void run(Bug b) throws AlreadyProcessingException {
        log.debug("Running for bug " + b.toString());
    }

    public void run(MailMessage m) throws AlreadyProcessingException {
        
        log.debug("Running for email " + m.toString());
    }
    
    public void run(ProjectVersion pv) throws AlreadyProcessingException {
        /* Read config options in advance*/        
        FileTypeMatcher.FileType fType;
        ProjectFile prevFile;
        int locCurrent, locPrevious;
        Developer dev = pv.getCommitter();
        Set<ProjectFile> projectFiles = pv.getVersionFiles();
        
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin plugin = AlitheiaCore.getInstance().getPluginAdmin().getImplementingPlugin("Wc.loc");
        
        if (plugin != null) {
            locMetric = plugin.getSupportedMetrics();
        } else {
            log.error("Could not find the WC plugin");
            return;
        }
        
        int numFilesThreshold;
        int updateThreshold; 
        
        PluginConfiguration config = getConfigurationOption(
                ContributionMetricImpl.CONFIG_CMF_THRES);
        
        if (config == null || 
                Integer.parseInt(config.getValue()) <= 0) {
            log.error("Plug-in configuration option " + 
                    ContributionMetricImpl.CONFIG_CMF_THRES + " not found");
            return; 
        } else {
            numFilesThreshold = Integer.parseInt(config.getValue());
        }
        
        config = getConfigurationOption(CONFIG_WEIGHT_UPDATE_VERSIONS);
        
        if (config == null || 
                Integer.parseInt(config.getValue()) <= 0) {
            log.error("Plug-in configuration option " + 
                    CONFIG_WEIGHT_UPDATE_VERSIONS + " not found");
            return;
        } else  {
            updateThreshold = Integer.parseInt(config.getValue());
        }
            
        
        Pattern bugNumberLabel = Pattern.compile("\\A.*(pr:|bug:).*\\Z",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Pattern pHatLabel = Pattern.compile(
                "\\A.*(ph:|pointy hat|p?hat:).*\\Z", Pattern.CASE_INSENSITIVE
                        | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m;

        //Commit message is empty
        if (pv.getCommitMsg().length() == 0) {
            updateField(pv, dev, ActionType.CEC, false, 1);
        } else {
            //Commit contains a bug report number
            m = bugNumberLabel.matcher(pv.getCommitMsg());
            if (m.matches()) {
                updateField(pv, dev, ActionType.CBN, true, 1);
            }
            //Commit awards a pointy hat
            m = pHatLabel.matcher(pv.getCommitMsg());
            if (m.matches()) {
                updateField(pv, dev, ActionType.CPH, true, 1);
            }
        }
        
        //Commit more files in a commit than the provided threshold
        if (projectFiles.size() > numFilesThreshold) {
            updateField(pv, dev, ActionType.CMF, false, 1);
        }

        Iterator<ProjectFile> i = projectFiles.iterator();
        
        while (i.hasNext()) {
            ProjectFile pf = i.next();

            fType = FileTypeMatcher.getFileType(pf.getFileName());

            if (pf.getIsDirectory()) {
                //New directory added
                if (pf.isAdded()) {
                    updateField(pv, dev, ActionType.CND, true, 1);
                }
            }
            
            if (pf.getCopyFrom() != null) {
                log.debug("Contrib: Ignoring copied file" + pf);
                continue;
            }
            
            //Commit of a source file: -
            if (fType == FileTypeMatcher.FileType.SRC) {
                //Source file changed, calc number of lines commited
                try {
                    //File deleted, set current lines to 0 
                    if (pf.isDeleted()) {
                        locCurrent = 0;
                    } else {
                        locCurrent = getLOCResult(pf, plugin, locMetric);
                    }
                    //Source file just added
                    if (pf.isAdded()) {
                        updateField(pv, dev, ActionType.CNS, true, 1);
                        locPrevious = 0;
                    } else {
                        //Existing file, get lines of previous version
                        prevFile = ProjectFile.getPreviousFileVersion(pf);
                        if (prevFile != null) {
                            locPrevious = getLOCResult(prevFile, plugin, locMetric);
                        } else {
                            log.warn("Cannot get previous file " +
                                        "version for file id: " + pf.getId());
                            locPrevious = 0;
                        }
                    }
                    //The commit change some lines
                    updateField(pv, dev, ActionType.CAL, true, 
                            abs(locCurrent - locPrevious));
                } catch (MetricMismatchException e) {
                    log.error("Results of LOC metric for project: "
                            + pv.getProject().getName() + " file: "
                            + pf.getFileName() + ", Version: "
                            + pv.getRevisionId() + " could not be retrieved: "
                            + e.getMessage());
                    return;
                }
            }
            
            if (fType == FileTypeMatcher.FileType.BIN) {
                //Commit of a binary file: -
                updateField(pv, dev, ActionType.CBF, false, 1);
            } 
            
            if (fType == FileTypeMatcher.FileType.DOC) {
              //Commit of a documentation file: -
                updateField(pv, dev, ActionType.CDF, true, 1);
            } 
            
            if (fType == FileTypeMatcher.FileType.TRANS) {
              //Commit of a translation file: -
                updateField(pv, dev, ActionType.CTF, true, 1);
            }
        }
        
        //Check if it is required to update the weights
        synchronized (this) {
            long distinctVersions = calcDistinctVersions();
            //Should the weights be updated?
            if (distinctVersions % updateThreshold == 0){
                updateWeights(pv);
            }
        }   
        markEvaluation(Metric.getMetricByMnemonic("CONTRIB"), pv);
    }

    private int getLOCResult(ProjectFile pf, AlitheiaPlugin plugin, 
            List<Metric> locMetric) 
        throws MetricMismatchException, AlreadyProcessingException {
      //Get lines of current version of the file from the wc metric
        Result r = plugin.getResult(pf, locMetric);
        if (r != null && r.hasNext()) {
            return r.getRow(0).get(0).getInteger();
        }
        else { 
            log.warn("Plugin <" + plugin.getName() + "> did" +
                    " not return a result for file " + pf );
            return 0;
        }
    }
    
    /**
     * Get result per developer and per categorys
     */
    private double getResultPerActionCategory(Developer d, ActionCategory ac) {
        ContribActionWeight weight;
        long totalActions;
        double value = 0;

        for (ActionType at : ActionType.getActionTypes(ac)) {
            weight = ContribActionWeight.getWeight(at);
            
            if (weight == null) {
                continue;
            }
            
            ContribActionType cat = 
                ContribActionType.getContribActionType(at, null);
                
            totalActions = 
                ContribAction.getTotalActionsPerTypePerDeveloper(at, d);

            if(totalActions != 0){
                if (cat.getIsPositive())
                    value += weight.getWeight() * totalActions;
                else
                    value -= weight.getWeight() * totalActions;
            }
        }
        return value;
    }
    
    private int calcDistinctVersions() {
        DBService db = AlitheiaCore.getInstance().getDBService();
        List<?> distinctVersions = db.doHQL("select " +
                "count(distinct changedResourceId) from ContribAction");
        
        if (distinctVersions == null || 
            distinctVersions.size() == 0 || 
            distinctVersions.get(0) == null) {
            return 0;
        }
        
        return (Integer.parseInt(distinctVersions.get(0).toString())) ;
    }
    
    private void updateField(ProjectVersion pv, Developer dev, 
            ActionType actionType, boolean isPositive, int value) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        ContribActionType at = ContribActionType.getContribActionType(actionType,
                isPositive);
        
        if (at == null) {
            db.rollbackDBSession();
            return;
        }

        ContribAction a = ContribAction.getContribAction(dev, pv.getId(), at);

        if (a == null) {
            a = new ContribAction();
            a.setDeveloper(dev);
            a.setChangedResourceId(pv.getId());
            a.setContribActionType(at);
            a.setTotal(value);
            db.addRecord(a);
        } else {
            a.setTotal(a.getTotal() + value);
        }
    }
    
    private void updateWeights(ProjectVersion pv) {
        ActionCategory[] actionCategories = ActionCategory.values();

        long totalActions = ContribAction.getTotalActions();
        long totalActionsPerCategory;
        long totalActionsPerType;
        
        if (totalActions <= 0) {
            return;
        }
        
        for (int i = 0; i < actionCategories.length; i++) {
            //update action category weight
            totalActionsPerCategory = 
                ContribAction.getTotalActionsPerCategory(actionCategories[i]);
                
            if (totalActionsPerCategory <= 0) {
                continue;
            }
            
            updateActionCategoryWeight(actionCategories[i],
                    totalActionsPerCategory, totalActions);

            // update action types weights
            ArrayList<ActionType> actionTypes = 
                ActionType.getActionTypes(actionCategories[i]);

            for (int j = 0; j < actionTypes.size(); j++) {
                totalActionsPerType = 
                    ContribAction.getTotalActionsPerType(actionTypes.get(j));
                updateActionTypeWeight(actionTypes.get(j),totalActionsPerType, 
                        totalActionsPerCategory);
            }
        }
    }
    
    private void updateActionTypeWeight(ActionType actionType, 
            long totalActionsPerType, long totalActionsPerCategory) {
        
        DBService db = AlitheiaCore.getInstance().getDBService();
        double weight = (double)(100 * totalActionsPerType) / 
            (double)totalActionsPerCategory;

        ContribActionWeight a = ContribActionWeight.getWeight(actionType);
       
        if (a == null) {
            a = new ContribActionWeight();
            a.setType(actionType);
            a.setWeight(weight);
            db.addRecord(a);
        } else {
            a.setWeight(weight);
        }
    }
    
    private void updateActionCategoryWeight(ActionCategory actionCategory, 
            long totalActionsPerCategory, long totalActions){
        DBService db = AlitheiaCore.getInstance().getDBService();
        double weight = (double)(100 * totalActionsPerCategory) / 
            (double)totalActions;

        ContribActionWeight a = ContribActionWeight.getWeight(actionCategory);

        if (a == null) { //No weight calculated for this action yet
            a = new ContribActionWeight();
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

// vi: ai nosi sw=4 ts=4 expandtab
