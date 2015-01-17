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

import eu.sqooss.metrics.contrib.ContributionActions;
import eu.sqooss.metrics.contrib.ContributionActions.ActionCategory;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

public class ContribActionType extends DAObject {

    private long id;
    private String actionCategory;
    private String actionType;
    private boolean isPositive;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ContributionActions.ActionCategory getCategory(){
        return ContributionActions.ActionCategory.fromString(actionCategory);
    }
    
    public String getActionCategory(){
        return actionCategory;
    }
    
    public void setCategory(ContributionActions.ActionCategory s) {
        this.actionCategory = s.toString();
    }
    
    public void setActionCategory(String s) {
        this.actionCategory = s;
    }
    
    public ContributionActions.ActionType getType(){
        return ContributionActions.ActionType.fromString(actionType);
    }
    
    public String getActionType(){
        return actionType;
    }
    
    public void setType(ContributionActions.ActionType s) {
        this.actionType = s.toString();
    }
    
    public void setActionType(String s) {
        this.actionType = s;
    }
    
    public boolean getIsPositive(){
        return isPositive;
    }
    
    public void setIsPositive(boolean isPositive){
        this.isPositive = isPositive;
    }
    
    /**
     * Get a list of distinct action types that have been recorded per project 
     * until the provided date
     * @param sp The project to check actions for
     * @param before 
     * @return
     */
    public static List<ContribActionType> getProjectActionTypes(StoredProject sp,
            Date before) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramProject = "paramProject";
        String paramBefore = "paramBefore";
        String paramCat = "paramCat";
        
        StringBuilder q = new StringBuilder(" select distinct(cat) ");
        q.append(" from ContribAction ca, ContribActionType cat ");
        q.append(" where ca.contribActionType = cat ");
        q.append(" and ca.developer.storedProject = :").append(paramProject);
        q.append(" and ca.changedResourceTimestamp <= :").append(paramBefore);
        //q.append(" and cat.actionCategory = :").append(paramCat);
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(paramProject, sp);
        params.put(paramBefore, before);
        //params.put(paramCat, ac);
        
        return (List<ContribActionType>) dbs.doHQL(q.toString(),params);  
    }
    
    public static ContribActionType getContribActionType(
            ContributionActions.ActionType actionType,
            Boolean isPositive) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
  
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("actionType", actionType.toString());

        List<ContribActionType> atl = dbs.findObjectsByProperties(
                ContribActionType.class, parameterMap);

        if (!atl.isEmpty())
            return atl.get(0);

        if (isPositive == null)
            return null;

        ContribActionType at = new ContribActionType();
        at.setCategory(ActionCategory.getActionCategory(actionType));
        at.setType(actionType);
        at.setIsPositive(isPositive);

        if (!dbs.addRecord(at)) {
            return null;
        }
        return at;
    }
}
