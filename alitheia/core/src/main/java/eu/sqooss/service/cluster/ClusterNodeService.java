/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.service.cluster;


import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.ClusterNode;

/**
 * The clusternode service is the gateway in Alitheia to control the clusternode
 * in means of assigning a project on that node, and getting core information
 * The clusternode service offers an HTTP interface to prompt the system. The URL
 * supported by the clusternode service lives underneath the web administration
 * site (which is localhost:8088 in the default Alitheia installation)
 * as /clusternode. Actions are controlled through the value of the GET
 * parameter action; Each action accepts parameters described bellow.
 * The acceptable values of action are taken from the ClusterNodeAction enum.
 * Sample clusternode URLs are:
 *
 *  http://localhost:8088/clusternode?action=xxx&param=yyyy
 *  
 * Note that action values are not case-sensitive, though they must match 
 * exactly the enum names.
 *
 * The rest of the interface contains implementation parts which typically
 * won't be called from code; it would be unusual for an internal part
 * of the system to call the clusternode, as it is intended as a way to poke
 * the system from the outside.
 */
public interface ClusterNodeService extends AlitheiaCoreService {

    /**
     * Targets for an action over a ClusterNode. 
     * These names are used in the <webadmin>/clusternode  URLs (case-insensitive) 
     * and in the rest of the system code.
     *
     */
    public enum ClusterNodeAction {
        /** Request to show assigned projects */
        GET_ASSIGNED_PROJECTS,
        /** Request to show known ClusterNode names */
        GET_KNOWN_SERVERS,
        /** Request to assign a project*/
        ASSIGN_PROJECT;
        
        public static String[] toStringArray() {
            String[] actions = new String[ClusterNodeAction.values().length];
            for (ClusterNodeAction a : ClusterNodeAction.values()) {
               actions[a.ordinal()] = a.toString();
            }

            return actions;
        }
    }

    
 
    
    // return this Node's name
    String getClusterNodeName();
    
    // assign a project to this ClusterNode
    boolean assignProject(StoredProject project) throws ClusterNodeActionException;

    // assign a project to that ClusterNode
    boolean assignProject(ClusterNode node, StoredProject project) throws ClusterNodeActionException;


    /**
     * Check if project is assigned to this ClusterNode
     * @param project the project to check
     * @return true if project is assigned to this Cluster Node
     */
    boolean isProjectAssigned(StoredProject project);


  }
