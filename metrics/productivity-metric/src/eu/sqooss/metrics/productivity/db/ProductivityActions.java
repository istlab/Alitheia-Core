/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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

package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.ProjectVersion;

public class ProductivityActions extends DAObject {

    private Developer developer;
    private ProjectVersion projectVersion;
    private ProductivityActionType productivityActionType;
    private long total;

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
    
    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }
    
    public void setProjectVersion(ProjectVersion projectVersion) {
        this.projectVersion = projectVersion;
    }
    
    public ProductivityActionType getProductivityActionType() {
        return productivityActionType;
    }

    public void setProductivityActionType(ProductivityActionType actionType) {
        this.productivityActionType = actionType;
    }
    
    public long getTotal(){
        return total;
    }
    
    public void setTotal(long total){
        this.total = total;
    }

    public static ProductivityActions getProductivityAction(Developer dev, 
            ProjectVersion pv, ProductivityActionType actionType) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("developer", dev);
        properties.put("projectVersion", pv);
        properties.put("productivityActionType", actionType);

        List<ProductivityActions> pa = dbs.findObjectsByPropertiesForUpdate(
                ProductivityActions.class, properties);
        
        return pa.isEmpty() ? null : pa.get(0);
    }
  
    public static long getTotalActions(){
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String query = "select sum(total) from ProductivityActions" ;
        
        List<?> totalActions = dbs.doHQL(query);
        
        if(totalActions == null || totalActions.size() == 0 ||
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerCategory(
            ProductivityMetricActions.ActionCategory actionCategory) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramCategory = "paramCategory"; 
        
        String query = "select sum(a.total) from ProductivityActions a, " +
        		"ProductivityActionType b " +
        		"where a.productivityActionType = b.id and " +
        		"b.actionCategory = :" + paramCategory ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramCategory, actionCategory.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0 || 
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerType(
            ProductivityMetricActions.ActionType actionType) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramType = "paramType"; 
        
        String query = "select sum(a.total) from ProductivityActions a, " +
        		"ProductivityActionType b " +
                " where a.productivityActionType = b.id " +
                " and b.actionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0 || 
                totalActions.get(0) == null) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerTypePerDeveloper(
            ProductivityMetricActions.ActionType actionType, Developer dev) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramType = "paramType"; 
        String paramDeveloper = "paramDeveloper"; 
        
        String query = "select sum(a.total) from ProductivityActions a, ProductivityActionType b " +
                       " where a.productivityActionType = b.id " +
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
