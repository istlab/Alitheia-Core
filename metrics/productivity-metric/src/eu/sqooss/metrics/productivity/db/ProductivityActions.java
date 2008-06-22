package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.service.CoreActivator;
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

    public static ProductivityActions getProductivityAction(Developer dev, ProjectVersion pv,
            ProductivityActionType actionType) {
        
        DBService dbs = CoreActivator.getDBService();
        
        String paramDeveloper = "paramDeveloper"; 
        String paramVersion = "paramVersion";
        String paramType = "paramType"; 
        
        String query = "select a from ProductivityActions a " +
                " where a.developer = :" + paramDeveloper +
                " and a.projectVersion = :" + paramVersion +
                " and a.productivityActionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramDeveloper, dev);
        parameters.put(paramVersion, pv);
        parameters.put(paramType, actionType);

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
        
        List<?> totalActions = dbs.doHQL(query);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerCategory(ProductivityMetricActions.ActionCategory actionCategory){
        DBService dbs = CoreActivator.getDBService();
        
        String paramCategory = "paramCategory"; 
        
        String query = "select sum(a.total) from ProductivityActions a, ProductivityActionType b " +
                       " where a.PRODUCTIVITY_ACTION_TYPE_ID = b.PRODUCTIVITY_ACTION_TYPE_ID " +
                       " and b.actionCategory = :" + paramCategory ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramCategory, actionCategory.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerType(ProductivityMetricActions.ActionType actionType){
        DBService dbs = CoreActivator.getDBService();
        
        String paramType = "paramType"; 
        
        String query = "select sum(a.total) from ProductivityActions a, ProductivityActionType b " +
                       " where a.PRODUCTIVITY_ACTION_TYPE_ID = b.PRODUCTIVITY_ACTION_TYPE_ID " +
                       " and b.actionType = :" + paramType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType.toString());
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
    
    public static long getTotalActionsPerTypePerDeveloper(ProductivityActionType actionType, Developer dev){
        DBService dbs = CoreActivator.getDBService();
        
        String paramType = "paramType"; 
        String paramDeveloper = "paramDeveloper"; 
        
        String query = "select sum(total) from ProductivityActions " +
                       " where productivityActionType = :" + paramType +
                       " and developer = :" + paramDeveloper ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramType, actionType);
        parameters.put(paramDeveloper, dev);
        
        List<?> totalActions = dbs.doHQL(query, parameters);
        
        if(totalActions == null || totalActions.size() == 0) {
            return 0L;
        }
        
        return Long.parseLong(totalActions.get(0).toString());
    }
}
