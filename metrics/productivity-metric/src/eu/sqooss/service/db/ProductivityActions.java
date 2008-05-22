package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.service.CoreActivator;

public class ProductivityActions extends DAObject {

    private Developer developer;
    private String actionType;
    private boolean isPositive;
    private long total;

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
    
    public ProductivityMetricActions.ActionType getActionType(){
        return ProductivityMetricActions.ActionType.fromString(actionType);
    }
    
    public String getType(){
        return actionType;
    }
    
    public void setActionType(ProductivityMetricActions.ActionType s) {
        this.actionType = s.toString();
    }
    
    public boolean getIsPositive(){
        return isPositive;
    }
    
    public void setIsPositive(boolean isPositive){
        this.isPositive = isPositive;
    }
    
    public long getTotal(){
        return total;
    }
    
    public void setTotal(long total){
        this.total = total;
    }

    public static ProductivityActions getProductivityAction(Developer dev, 
            ProductivityMetricActions.ActionType actionType, boolean isPositive) {
        
        DBService dbs = CoreActivator.getDBService();
        
        String paramDeveloper = "paramDeveloper"; 
        String paramType = "paramType"; 
        String paramPositive = "paramPositive";
        
        String query = "select a from ProductivityActions a " +
                " where a.developer = :" + paramDeveloper +
                " and a.actionType = :" + paramType +
                " and a.isPositive = :" + paramPositive ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramDeveloper, dev.getId());
        parameters.put(paramType, actionType.toString());
        parameters.put(paramPositive, isPositive);

        List<?> productivityActions = dbs.doHQL(query, parameters);
        
        if(productivityActions == null || productivityActions.size() == 0) {
            return null;
        }else {
            return (ProductivityActions) productivityActions.get(0);
        }
    }
  
}
