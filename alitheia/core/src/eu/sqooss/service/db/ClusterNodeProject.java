/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,
 *                Athens, Greece.
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
 
package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

public class ClusterNodeProject extends DAObject {

    private ClusterNode node;
    private StoredProject project;
    private boolean locked;


	public ClusterNodeProject() {
        setLocked(false); // mimic SQL default value
    }

    public boolean equals(Object obj) {
        if (obj instanceof ClusterNodeProject) {
            ClusterNodeProject clusternodeproject = (ClusterNodeProject) obj;
            return clusternodeproject.getNode().getId() == this.node.getId()
                    && clusternodeproject.getProject().getId() == this.project
                            .getId();
        }
        return false;
    }

    public ClusterNode getNode() {
        return node;
    }

    public void setNode(ClusterNode node) {
        this.node = node;
    }

    public StoredProject getProject() {
        return project;
    }

    public void setProject(StoredProject project) {
        this.project = project;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Get the ClusterNodeProject assignment record for a specific Project
     * 
     * @param StoredProject
     *                to check
     * @return the ClusterNodeProject record for the requested StoredProject
     */
    public static ClusterNodeProject getProjectAssignment(StoredProject project) {
        if (project == null) {
            return null;
        }
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("project", project);
        List<ClusterNodeProject> rList = dbs.findObjectsByProperties(
                ClusterNodeProject.class, parameterMap);
        if ((rList == null) || (rList.isEmpty())) {
            return null;
        }
        return rList.get(0);
    }

    // this should return only ONE record if project exists in assignments
    /**
     * Get a list of projects assigned to a specific ClusterNode
     * 
     * @param node
     *                cluster node to check against
     * @return a ClusterNodeProject list for the assigned projects
     */
    public static List<ClusterNodeProject> getNodeAssignments(ClusterNode node) {
        if (node == null) {
            return null;
        }
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("node", node);
        List<ClusterNodeProject> nList = dbs.findObjectsByProperties(
                ClusterNodeProject.class, parameterMap);
        if ((nList == null) || (nList.isEmpty())) {
            return null;
        }
        return nList;
    }
    
    /**
     * Check if a StoredProject is assigned to a specific ClusterNode
     * 
     * @param node
     *                cluster node to check against
     * @param project
     *                project to check
     * @return
     */
    public static boolean isProjectAssigned(ClusterNode node,
            StoredProject project) {
        ClusterNodeProject cnp = null;
        ClusterNode cn = null;
        if ((node == null) || (project == null)) {
            return false;
        }
        cnp = ClusterNodeProject.getProjectAssignment(project);
        if (cnp == null) {
            return false;
        }
        cn = cnp.getNode();
        if (cn == null) {
            return false;
        }

        return (cn.getId() == node.getId());

    }    

}
