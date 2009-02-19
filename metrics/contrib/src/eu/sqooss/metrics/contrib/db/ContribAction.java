/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  *                Athens, Greece.
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

package eu.sqooss.metrics.contrib.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.contrib.ContributionActions;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionCategory;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionType;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.StoredProject;

public class ContribAction extends DAObject {

    private Developer developer;
    private Long changedResourceId;
    private ContribActionType contribActionType;
    private long total;
    private Date changedResourceTimestamp;    

    public Date getChangedResourceTimestamp() {
        return changedResourceTimestamp;
    }

    public void setChangedResourceTimestamp(Date changedResourceTimestamp) {
        this.changedResourceTimestamp = changedResourceTimestamp;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
    
    public Long getChangedResourceId() {
        return changedResourceId;
    }
    
    public void setChangedResourceId(Long changedResourceId) {
        this.changedResourceId = changedResourceId;
    }
    
    public ContribActionType getContribActionType() {
        return contribActionType;
    }

    public void setContribActionType(ContribActionType actionType) {
        this.contribActionType = actionType;
    }
    
    public long getTotal(){
        return total;
    }
    
    public void setTotal(long total){
        this.total = total;
    }

    public static ContribAction getContribAction(Developer dev, 
            Long resourceId, ContribActionType actionType) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("developer", dev);
        properties.put("changedResourceId", resourceId);
        properties.put("contribActionType", actionType);
        
        List<ContribAction> pa = dbs.findObjectsByProperties(ContribAction.class, properties);
        
        return pa.isEmpty() ? null : pa.get(0);
    }
    
    public static Long getDevActionsPerType(Developer dev, 
            Date timestamp, ContribActionType actionType) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramTimestamp = "paramTimemstamp";
        String paramDeveloper = "paramDeveloper";
        String paramContribActionType = "paramContribActionType";
        
        StringBuffer q = new StringBuffer("select sum(ca.total) ");
        q.append(" from ContribAction ca " );
        q.append(" where ca.changedResourceTimestamp <= :").append(paramTimestamp);
        q.append(" and ca.developer = :").append(paramDeveloper);
        q.append(" and ca.contribActionType = :").append(paramContribActionType);
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(paramTimestamp, timestamp);
        params.put(paramDeveloper, dev);
        params.put(paramContribActionType, actionType);
        
        
        List<Long> result = (List<Long>) dbs.doHQL(q.toString(),params);
        
        if (!result.isEmpty()) {
            if (result.get(0) != null)
                return result.get(0);
            else
                return 0L;
        }
        
        return 0L;
    }
    
    public static Long getTotalActionsPerTypePerProject(StoredProject sp, 
            Date timestamp, ContribActionType actionType) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramTimestamp = "paramTimemstamp";
        String paramProject = "paramProject";
        String paramContribActionType = "paramContribActionType";
        
        StringBuffer q = new StringBuffer("select sum(ca.total) ");
        q.append(" from ContribAction ca " );
        q.append(" where ca.changedResourceTimestamp <= :").append(paramTimestamp);
        q.append(" and ca.developer.storedProject = :").append(paramProject);
        q.append(" and ca.contribActionType = :").append(paramContribActionType);
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(paramTimestamp, timestamp);
        params.put(paramProject, sp);
        params.put(paramContribActionType, actionType);
        
        List<Long> result = (List<Long>) dbs.doHQL(q.toString(),params);
        
        if (!result.isEmpty()) {
            if (result.get(0) != null)
                return result.get(0);
            else
                return 0L;
        }
        
        return 0L;
    }
    
  
    public static long getTotalActions(){
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String query = "select sum(total) from ContribAction" ;
        
        List<?> totalActions = dbs.doHQL(query);
        
        if(totalActions == null || totalActions.size() == 0 ||
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerCategory(ActionCategory ac) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramCategory = "paramCategory"; 
        
        String query = "select sum(a.total) " +
        		"from ContribAction a, ContribActionType b " +
        		"where a.contribActionType = b " +
        		"and b.actionCategory = :" + paramCategory ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramCategory, ac.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0 || 
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerType(
            ContributionActions.ActionType actionType) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramType = "paramType"; 
        
        String query = "select sum(a.total) " +
                "from ContribAction a, ContribActionType b " +
                "where a.contribActionType = b " +
                "and b.actionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0 || 
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerTypePerDeveloper(ActionType actionType,
            Developer dev) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramType = "paramType"; 
        String paramDeveloper = "paramDeveloper"; 
        
        String query = "select sum(a.total) " +
            "from ContribAction a, ContribActionType b " +
            "where a.contribActionType = b " +
            " and b.actionType = :" + paramType +
            " and a.developer = :" + paramDeveloper ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType.toString());
        parameters.put(paramDeveloper, dev);
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0 || 
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
}
