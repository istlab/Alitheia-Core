package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionCategory;
import eu.sqooss.impl.service.CoreActivator;
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
        
        DBService dbs = CoreActivator.getDBService();

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
