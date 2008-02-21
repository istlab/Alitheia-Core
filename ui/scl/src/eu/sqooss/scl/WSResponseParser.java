/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.scl;

import java.util.ArrayList;

import eu.sqooss.ws.client.datatypes.WSFileMetadata;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSUser;
import eu.sqooss.ws.client.datatypes.WSUserGroup;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.result.WSResultEntry;

/**
 * This utility parses the data from the web services service.  
 */
class WSResponseParser {
    
//    /**
//     * This method parses the array of <code>WSStoredProject</code>s to the <code>WSResult</code>.
//     */
//    public static WSResult parseStoredProjects(WSStoredProject[] storedProjects) {
//        WSResult result = new WSResult();
//        //if the web service returns null, it is the first element in the array
//        if (storedProjects[0] != null) {
//            ArrayList<WSResultEntry> currentRow;
//            for (WSStoredProject sp: storedProjects) {
//                currentRow = new ArrayList<WSResultEntry>();
//                currentRow.add(new WSResultEntry(sp.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
//                currentRow.add(new WSResultEntry(sp.getName(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                currentRow.add(new WSResultEntry(sp.getRepository(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                currentRow.add(new WSResultEntry(sp.getBugs(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                currentRow.add(new WSResultEntry(sp.getMail(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                currentRow.add(new WSResultEntry(sp.getContact(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                currentRow.add(new WSResultEntry(sp.getWebsite(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
//                WSProjectVersion[] projectVersions = sp.getProjectVersions();
//                for (WSProjectVersion projectVersion: projectVersions) {
//                    currentRow.add(new WSResultEntry(projectVersion.getVersion(), WSResultEntry.MIME_TYPE_TYPE_INTEGER));
//                }
//                result.addResultRow(currentRow);
//            }
//        }
//        return result;
//    }
    
    /**
     * This method parses the array of <code>WSMetric</code>s to the <code>WSResult</code>.
     */
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
    
    /**
     * This method parses the array of <code>WSProjectFile</code>s to the <code>WSResult</code>.
     */
    public static WSResult parseProjectFiles(WSProjectFile[] projectFiles) {
        WSResult result = new WSResult();
        //if the web service returns null, it is the first element in the array
        if (projectFiles[0] != null) {
            ArrayList<WSResultEntry> currentRow;
            WSFileMetadata currentFileMetadata;
            for (WSProjectFile projectFile : projectFiles) {
                currentRow = new ArrayList<WSResultEntry>();
                currentRow.add(new WSResultEntry(projectFile.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(projectFile.getName(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                currentRow.add(new WSResultEntry(projectFile.getStatus(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                
                currentFileMetadata = projectFile.getProjectFileMetadata();
                currentRow.add(new WSResultEntry(currentFileMetadata.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
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
    
    /**
     * This method parses the array of <code>WSUser</code>s to the <code>WSResult</code>.
     */
    public static WSResult parseUsers(WSUser[] wsUsers) {
        WSResult result = new WSResult();
        if (wsUsers[0] != null) {
            ArrayList<WSResultEntry> currentRow;
            WSUserGroup[] userGroups;
            for (WSUser user : wsUsers) {
                currentRow = new ArrayList<WSResultEntry>();
                currentRow.add(new WSResultEntry(user.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                currentRow.add(new WSResultEntry(user.getUserName(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                userGroups = user.getUserGroups();
                for (WSUserGroup userGroup : userGroups) {
                    currentRow.add(new WSResultEntry(userGroup.getId(), WSResultEntry.MIME_TYPE_TYPE_LONG));
                    currentRow.add(new WSResultEntry(userGroup.getDescription(), WSResultEntry.MIME_TYPE_TEXT_PLAIN));
                }
                result.addResultRow(currentRow);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
