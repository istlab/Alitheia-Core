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

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionType;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionCategory;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

public class ProductivityMetricJob extends AbstractMetricJob {
	
    // DAO of the project version that has to be measured
    private ProjectVersion pv;

    // Reference to the metric that created this job
    AbstractMetric parent = null;
    

    public ProductivityMetricJob(AbstractMetric owner, ProjectVersion a) {
        super(owner);
        parent = owner;
        pv = a;
    }

    public int priority() {
        return 0xbeef;
    }
    
    public void run() {
        if (!db.startDBSession()) {
            log.error("No DBSession could be opened!");
            return;
        }

        pv = db.attachObjectToDBSession(pv);

        FileTypeMatcher.FileType fType;
        boolean isNew;
        ProjectFile prevFile;
        int locCurrent, locPrevious;
        Developer dev = pv.getCommitter();
        String commitMsg = pv.getCommitMsg();
        Set<ProjectFile> ProjectFiles = pv.getVersionFiles();
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin plugin = this.core.getPluginAdmin()
                .getImplementingPlugin("LOC");
        if (plugin != null) {
            locMetric = plugin.getSupportedMetrics();
        } else {
            log.error("Could not find WC plugin metrics!");
            db.rollbackDBSession();
            return;
        }

        Pattern bugNumberLabel = Pattern.compile("\\A.*(pr:|bug:).*\\Z",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Pattern pHatLabel = Pattern.compile(
                "\\A.*(ph:|pointy hat|p?hat:).*\\Z", Pattern.CASE_INSENSITIVE
                        | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher m;

        updateField(dev, ActionType.TCO, true, 1);
        updateField(dev, ActionType.TCF, true,
                ProjectFiles.size());        
        
        if (commitMsg.length() == 0) {
            updateField(dev, ActionType.CEC, false, 1);
        } else {
            m = bugNumberLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ActionType.CBN,
                        true, 1);
            }
            m = pHatLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ActionType.CPH,
                        true, 1);
            }
        }

        if (ProjectFiles.size() >
                Integer.parseInt(core.getPluginAdmin().getPluginInfo(parent.getUniqueKey()).getConfiguration().iterator().next().getValue())) {
            updateField(dev, ActionType.CMF, false, 1);
        }

        Iterator<ProjectFile> i = ProjectFiles.iterator();
        while (i.hasNext()) {
            ProjectFile pf = i.next();

            fType = FileTypeMatcher.getFileType(pf.getFileName());
            isNew = (pf.getStatus().equalsIgnoreCase("ADDED"));

            if (pf.getIsDirectory()) {
                if (isNew) {
                    updateField(dev, ActionType.CND,
                            true, 1);
                }
            } else if (fType == FileTypeMatcher.FileType.SRC) {
                try {
                    locCurrent = plugin.getResult(pf, locMetric).getRow(0).get(0).getInteger();
                    if (isNew) {
                        updateField(dev,
                                ActionType.CNS, true, 1);
                        locPrevious = 0;
                    } else {
                        prevFile = ProjectFile.getPreviousFileVersion(pf);
                        locPrevious = plugin.getResult(prevFile, locMetric).getRow(0).get(0).getInteger();
                    }
                    updateField(dev, ActionType.CAL,
                            true, abs(locCurrent - locPrevious));
                } catch (MetricMismatchException e) {
                    log.error("Results of LOC metric for project: "
                            + pv.getProject().getName() + " file: "
                            + pf.getFileName() + ", Version: "
                            + pv.getVersion() + " can not be retrieved: "
                            + e.getMessage());
                    db.rollbackDBSession();
                    return;
                }
            } else if (fType == FileTypeMatcher.FileType.BIN) {
                updateField(dev, ActionType.CBF,
                        true, 1);
            } else if (fType == FileTypeMatcher.FileType.DOC) {
                updateField(dev, ActionType.CDF,
                        true, 1);
            } else if (fType == FileTypeMatcher.FileType.TRANS) {
                updateField(dev, ActionType.CTF,
                        true, 1);
            }

        }
        db.commitDBSession();
    }
    
    private void updateField(Developer dev, ActionType actionType, boolean isPositive, int value){
    	//TODO: test updates, uncomment       
/*        ProductivityActions a = ProductivityActions.getProductivityAction(dev, actionType);
        
        if (a == null) {
            a = new ProductivityActions();
            a.setDeveloper(dev);
            a.setActionCategory(ActionCategory.getActionCategory(actionType));
            a.setActionType(actionType);
            a.setIsPositive(isPositive);
            a.setTotal(value);
            db.addRecord(a);
        } else{
            a.setTotal(a.getTotal() + value);
        }
        
        updateWeights(actionType);
*/
    }
    
    private void updateWeights(ActionType actionType){
        //TODO: test, uncomment
        
/*        ActionCategory actionCategory = ActionCategory.getActionCategory(actionType);
        
        long totalActions = ProductivityActions.getTotalActions();
        long totalActionsPerCategory = ProductivityActions.getTotalActionsPerCategory(actionCategory);
        long totalActionsPerType = ProductivityActions.getTotalActionsPerType(actionType);
        
        //update weight for the action type
        long weight = 100 * totalActionsPerType /
                            totalActionsPerCategory;
        
        ProductivityWeights a = ProductivityWeights.getWeight(actionType);
        
        if (a == null) {
            a = new ProductivityWeights();
            a.setActionType(actionType);
            a.setWeight(weight);
            db.addRecord(a);
        } else{
            a.setWeight(weight);
        }
        
        //update weight for the action category
        weight = 100 *  totalActionsPerCategory /
                        totalActions;

        a = ProductivityWeights.getWeight(actionCategory);
        
        if (a == null) {
            a = new ProductivityWeights();
            a.setActionCategory(actionCategory);
            a.setWeight(weight);
            db.addRecord(a);
        } else{
            a.setWeight(weight);
        }
  */     
    }
    
    private int abs (int value){
        if (value<0) 
            return -1 * value;
        else
            return value;
    }
}


//vi: ai nosi sw=4 ts=4 expandtab
