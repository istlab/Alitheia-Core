package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;

public class ProductivityActions extends DAObject {

    private Developer developer;
    private String actionCategory;
    private String actionType;
    private boolean isPositive;
    private long total;

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
    
    public ProductivityMetricActions.ActionCategory getActionCategory(){
        return ProductivityMetricActions.ActionCategory.fromString(actionCategory);
    }
    
    public String getCategory(){
        return actionCategory;
    }
    
    public void setActionCategory(ProductivityMetricActions.ActionCategory s) {
        this.actionCategory = s.toString();
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
            ProductivityMetricActions.ActionType actionType) {
        
        DBService dbs = CoreActivator.getDBService();
        
        String paramDeveloper = "paramDeveloper"; 
        String paramType = "paramType"; 
        
        String query = "select a from ProductivityActions a " +
                " where a.developer = :" + paramDeveloper +
                " and a.actionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramDeveloper, dev.getId());
        parameters.put(paramType, actionType.toString());

        List<?> productivityActions = dbs.doHQL(query, parameters);
        
        if(productivityActions == null || productivityActions.size() == 0) {
            return null;
        }else {
            return (ProductivityActions) productivityActions.get(0);
        }
    }
  
    public static long getTotalActions(){
        DBService dbs = CoreActivator.getDBService();
        
        String query = "select sum(total) from ProductivityActions" ;
        
        List<Long> totalActions = (List<Long>)dbs.doHQL(query);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerCategory(ProductivityMetricActions.ActionCategory actionCategory){
        DBService dbs = CoreActivator.getDBService();
        
        String paramCategory = "paramCategory"; 
        
        String query = "select sum(total) from ProductivityActions" +
                       " where a.actionCategory = :" + paramCategory ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramCategory, actionCategory.toString());
        
        List<?> totalActions = dbs.doHQL(query);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerType(ProductivityMetricActions.ActionType actionType){
        DBService dbs = CoreActivator.getDBService();
        
        String paramType = "paramType"; 
        
        String query = "select sum(total) from ProductivityActions" +
                       " where a.actionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
}
