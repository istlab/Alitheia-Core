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
import eu.sqooss.scl.axis2.datatypes.WSUser;
import eu.sqooss.scl.axis2.datatypes.WSUserGroup;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.result.WSResultEntry;

/**
 * This utility parses the data from the web services service.  
 */
public class WSResponseParser {
    
    /**
     * This method parses the array of <code>WSStoredProject</code>s to the <code>WSResult</code>.
     * The <code>WSResult</code>'s rows consist of fields in the following format:
     * <p>
     * <table border=1>
     *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
     *  <tr><td> 0 </td><td> type/long    </td><td> id         </td></tr>
     *  <tr><td> 1 </td><td> text/plain   </td><td> name       </td></tr>
     *  <tr><td> 2 </td><td> text/plain   </td><td> repository </td></tr>
     *  <tr><td> 3 </td><td> text/plain   </td><td> bugs       </td></tr>
     *  <tr><td> 4 </td><td> text/plain   </td><td> mail       </td></tr>
     *  <tr><td> 5 </td><td> text/plain   </td><td> contact    </td></tr>
     *  <tr><td> 6 </td><td> text/plain   </td><td> website    </td></tr>
     *  <tr><td> 7 </td><td> type/integer </td><td> version1   </td></tr>
     *  <tr><td> 8 </td><td> type/integer </td><td> version2   </td></tr>
     *  <tr><td>...</td><td> type/integer </td><td> versionN   </td></tr>
     * <table>
     * </p><br>
     */
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
    
    /**
     * This method parses the array of <code>WSMetric</code>s to the <code>WSResult</code>.
     * The <code>WSResult</code>'s rows consist of fields in the following format:
     * <p>
     * <table border=1>
     *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
     *  <tr><td> 0 </td><td> type/long    </td><td> id          </td></tr>
     *  <tr><td> 1 </td><td> text/plain   </td><td> description </td></tr>
     *  <tr><td> 2 </td><td> text/plain   </td><td> type        </td></tr>
     * <table>
     * </p><br>
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
     * The <code>WSResult</code>'s rows consist of fields in the following format:
     * <p>
     * <table border=1>
     *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
     *  <tr><td>  0 </td><td> text/plain   </td><td> name        </td></tr>
     *  <tr><td>  1 </td><td> text/plain   </td><td> status      </td></tr>
     *  <tr><td>  2 </td><td> text/plain   </td><td> protection  </td></tr>
     *  <tr><td>  3 </td><td> type/integer </td><td> links       </td></tr>
     *  <tr><td>  4 </td><td> type/long    </td><td> user's id   </td></tr>
     *  <tr><td>  5 </td><td> type/long    </td><td> group's id  </td></tr>
     *  <tr><td>  6 </td><td> type/long    </td><td> access time </td></tr>
     *  <tr><td>  7 </td><td> type/long    </td><td> modification time </td></tr>
     *  <tr><td>  8 </td><td> text/plain   </td><td> file status change </td></tr>
     *  <tr><td>  9 </td><td> type/integer </td><td> size        </td></tr>
     *  <tr><td> 10 </td><td> type/integer </td><td> blocks      </td></tr>
     * </table>
     * </p><br>
     */
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
    
    /**
     * This method parses the array of <code>WSUser</code>s to the <code>WSResult</code>.
     * The <code>WSResult</code>'s rows consist of fields in the following format:
     * <p>
     * <table border=1>
     *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
     *  <tr><td> 0 </td><td> type/long    </td><td> user's id            </td></tr>
     *  <tr><td> 1 </td><td> text/plain   </td><td> user name            </td></tr>
     *  <tr><td> 2 </td><td> type/long    </td><td> group1's is          </td></tr>
     *  <tr><td> 3 </td><td> text/plain   </td><td> groups1' description </td></tr>
     *  <tr><td> 4 </td><td> type/long    </td><td> group2's is          </td></tr>
     *  <tr><td> 5 </td><td> text/plain   </td><td> groups2' description </td></tr>
     *  <tr><td>...</td><td> type/long    </td><td> groupN's is          </td></tr>
     *  <tr><td>...</td><td> text/plain   </td><td> groupsN' description </td></tr>
     * </table>
     * </p><br>
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
