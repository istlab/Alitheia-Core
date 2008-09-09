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

package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionCategory;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

public class ProductivityActionType extends DAObject {

    private String actionCategory;
    private String actionType;
    private boolean isPositive;
    
    public ProductivityMetricActions.ActionCategory getCategory(){
        return ProductivityMetricActions.ActionCategory.fromString(actionCategory);
    }
    
    public String getActionCategory(){
        return actionCategory;
    }
    
    public void setCategory(ProductivityMetricActions.ActionCategory s) {
        this.actionCategory = s.toString();
    }
    
    public void setActionCategory(String s) {
        this.actionCategory = s;
    }
    
    public ProductivityMetricActions.ActionType getType(){
        return ProductivityMetricActions.ActionType.fromString(actionType);
    }
    
    public String getActionType(){
        return actionType;
    }
    
    public void setType(ProductivityMetricActions.ActionType s) {
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
    
    public static ProductivityActionType getProductivityActionType(
            ProductivityMetricActions.ActionType actionType,
            Boolean isPositive) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("actionType", actionType.toString());
        
        List<ProductivityActionType> atl = dbs.findObjectsByProperties(
                ProductivityActionType.class, parameterMap);
        
        if (atl != null) {
            if (!atl.isEmpty() )
                return atl.get(0);
        }
       
        if (isPositive == null)
            return null;
            
        ProductivityActionType at = new ProductivityActionType();
        at.setCategory(ActionCategory.getActionCategory(actionType));
        at.setType(actionType);
        at.setIsPositive(isPositive);
            
        if ( !dbs.addRecord(at) )
            return null;
            
        return at;
    }
}
