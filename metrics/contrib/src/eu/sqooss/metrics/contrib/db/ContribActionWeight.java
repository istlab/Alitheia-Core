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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.contrib.ContributionActions;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionCategory;
import eu.sqooss.impl.metrics.contrib.ContributionActions.ActionType;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

public class ContribActionWeight extends DAObject{

    private String actionCategory;
    private String actionType;
    private double weight;
    private long lastUpdateVersion;
    
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
    
    public ActionType getType(){
        return ActionType.fromString(actionType);
    }
    
    public String getActionType(){
        return actionType;
    }
    
    public void setType(ActionType s) {
        this.actionType = s.toString();
    }
    
    public void setActionType(String s) {
        this.actionType = s;
    }
    
    public double getWeight(){
        return weight;
    }
    
    public void setWeight(double weight){
        this.weight = weight;
    }
    
    public static ContribActionWeight getWeight(ActionType actionType){
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("actionType", actionType.toString());
        
        List<ContribActionWeight> w = dbs.findObjectsByPropertiesForUpdate(
                ContribActionWeight.class, properties);
        
        // NOTE (romain) : This is going to lock many unrelated rows !
        // Review the query, it seems to return multiple rows but we only use the first one.
        return w.isEmpty() ? null : w.get(0);
    }
    
    public static ContribActionWeight getWeight(ActionCategory actionCategory){
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("actionCategory", actionCategory.toString());
        
        List<ContribActionWeight> w = dbs.findObjectsByPropertiesForUpdate(
                ContribActionWeight.class, properties);
        
        // NOTE (romain) : This is going to lock many unrelated rows !
        // Review the query, it seems to return multiple rows but we only use the first one.
        return w.isEmpty() ? null : w.get(0);        
    }
}
