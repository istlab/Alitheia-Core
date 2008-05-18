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

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.db.Developer;
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
        db.startDBSession();
        
        FileTypeMatcher.FileType fType;
        boolean isNew;
        Developer dev = pv.getCommitter();
        String commitMsg = pv.getCommitMsg();
        List<ProjectFile> ProjectFiles = pv.getVersionFiles();
        
        Pattern bugNumberLabel = Pattern.compile("\\A.*(pr:|bug:).*\\Z",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        
        Pattern pHatLabel = Pattern.compile("\\A.*(ph:|pointy hat|p?hat:).*\\Z", 
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        
        Matcher m;

        System.out.println("------- Version: " + pv.getVersion()
                + ", ProjectVersion: " + pv.getId() + ", Comment: "
                + pv.getCommitMsg());

        updateField(dev, ProductivityMetricActions.ActionType.TCO, 1);
        updateField(dev, ProductivityMetricActions.ActionType.TCF, 
                ProjectFiles.size());

        if (commitMsg.length() == 0) {
            updateField(dev, ProductivityMetricActions.ActionType.CEC, 1);
        } else {
            m = bugNumberLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ProductivityMetricActions.ActionType.CBN, 1);
            }
            m = pHatLabel.matcher(commitMsg);
            if (m.matches()) {
                updateField(dev, ProductivityMetricActions.ActionType.CPH, 1);
            }
        }

        if (ProjectFiles.size() >= 5) { // TODO in stored project activation
                                        // type after calculating mean
            updateField(dev, ProductivityMetricActions.ActionType.CMF, 1);
        }

        for (int i = 0; i < ProjectFiles.size(); i++) {

            // System.out.print(ProjectFiles.get(i).getStatus() + ", \t" +
            // ProjectFiles.get(i).getName() + ", \t" +
            // ProjectFiles.get(i).getIsDirectory() + "\n");

            fType = FileTypeMatcher.getFileType(
                    ProjectFiles.get(i).getFileName());
            isNew = (ProjectFiles.get(i).getStatus().equalsIgnoreCase("ADDED"));

            if (ProjectFiles.get(i).getIsDirectory()) {
                if (isNew) {
                    updateField(dev, ProductivityMetricActions.ActionType.CND,
                            1);
                }
            } else if (fType == FileTypeMatcher.FileType.SRC) {
                if (isNew) {
                    updateField(dev, ProductivityMetricActions.ActionType.CNS,
                            1);
                } else {
                    // TODO: metric CAL
                    ProjectFile prevFile = 
                        ProjectFile.getPreviousFileVersion(ProjectFiles.get(i));
                }
            } else if (fType == FileTypeMatcher.FileType.BIN) {
                updateField(dev, ProductivityMetricActions.ActionType.CBF, 1);
            } else if (fType == FileTypeMatcher.FileType.DOC) {
                updateField(dev, ProductivityMetricActions.ActionType.CDF, 1);
            } else if (fType == FileTypeMatcher.FileType.TRANS) {
                updateField(dev, ProductivityMetricActions.ActionType.CTF, 1);
            }

        }
        db.commitDBSession();
    }
    
    private void updateField(Developer dev, ProductivityMetricActions.ActionType actionType, int value){
    	//TODO: create dao object
//    	System.out.println("<" + actionType + " " + value + ">");
    }
}


//vi: ai nosi sw=4 ts=4 expandtab
