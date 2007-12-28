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

package eu.sqooss.scl.utils;

import java.util.ArrayList;

import eu.sqooss.scl.axis2.datatypes.WSFileMetadata;
import eu.sqooss.scl.axis2.datatypes.WSMetric;
import eu.sqooss.scl.axis2.datatypes.WSProjectFile;
import eu.sqooss.scl.axis2.datatypes.WSProjectVersion;
import eu.sqooss.scl.axis2.datatypes.WSStoredProject;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.result.WSResultEntry;

/**
 * This utility parses the data from the web services service.  
 */
public class WSResponseParser {
    
    public static WSResult parseStoredProjects(WSStoredProject[] storedProjects) {
        WSResult result = new WSResult();
        //if the web service returns null, it is the first element in the array
        if (storedProjects[0] != null) {
            ArrayList<WSResultEntry> currentRow;
            for (WSStoredProject sp: storedProjects) {
                currentRow = new ArrayList<WSResultEntry>();
                currentRow.add(new WSResultEntry(sp.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(sp.getName(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(sp.getRepository(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(sp.getBugs(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(sp.getMail(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(sp.getContact(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(sp.getWebsite(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                WSProjectVersion[] projectVersions = sp.getProjectVersions();
                for (WSProjectVersion projectVersion: projectVersions) {
                    currentRow.add(new WSResultEntry(projectVersion.getVersion(), WSResultEntry.MIME_TYPE_TYPE_INTEGER));
                }
                result.addResultRow(currentRow);
            }
        }
        return result;
    }
    
    public static WSResult parseMetrics(WSMetric[] metrics) {
        WSResult result = new WSResult();
        //if the web service returns null, it is the first element in the array
        if (metrics[0] != null) {
            ArrayList<WSResultEntry> currentRow;
            for (WSMetric metric: metrics) {
                currentRow = new ArrayList<WSResultEntry>();
                currentRow.add(new WSResultEntry(metric.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(metric.getDescription(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(metric.getMetricType().getType(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                result.addResultRow(currentRow);
            }
        }
        return result;   
    }
    
    public static WSResult parseProjectFiles(WSProjectFile[] projectFiles) {
        WSResult result = new WSResult();
        //if the web service returns null, it is the first element in the array
        if (projectFiles[0] != null) {
            ArrayList<WSResultEntry> currentRow;
            WSFileMetadata currentFileMetadata;
            for (WSProjectFile projectFile : projectFiles) {
                currentRow = new ArrayList<WSResultEntry>(); 
                currentRow.add(new WSResultEntry(projectFile.getName(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(projectFile.getStatus(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                
                currentFileMetadata = projectFile.getProjectFileMetadata();
                currentRow.add(new WSResultEntry(currentFileMetadata.getProtection(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(currentFileMetadata.getLinks(), WSResultEntry.MIME_TYPE_TYPE_INTEGER));
                currentRow.add(new WSResultEntry(currentFileMetadata.getUserId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(currentFileMetadata.getGroupId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(currentFileMetadata.getAccessTime(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(currentFileMetadata.getModificationTime(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(currentFileMetadata.getFileStatusChange(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(currentFileMetadata.getSize(), WSResultEntry.MIME_TYPE_TYPE_INTEGER));
                currentRow.add(new WSResultEntry(currentFileMetadata.getBlocks(), WSResultEntry.MIME_TYPE_TYPE_INTEGER));
                result.addResultRow(currentRow);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
