package eu.sqooss.metrics.productivity.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

public class ProductivityWeights extends DAObject{

    private String actionCategory;
    private String actionType;
    private double weight;
    
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
    
    public double getWeight(){
        return weight;
    }
    
    public void setWeight(double weight){
        this.weight = weight;
    }
    
    public static ProductivityWeights getWeight(ProductivityMetricActions.ActionType actionType){
        DBService dbs = CoreActivator.getDBService();
        
        String paramActionType = "paramActionType"; 
        
        String query = "select a from ProductivityWeights a " +
        " where a.actionType = :" + paramActionType ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramActionType, actionType.toString());
        
        List<?> weights = dbs.doHQL(query, parameters);
        
        if(weights == null || weights.size() == 0) {
            return null;
        }else {
            return (ProductivityWeights)weights.get(0);
        }
        
    }
    
    public static ProductivityWeights getWeight(ProductivityMetricActions.ActionCategory actionCategory){
        DBService dbs = CoreActivator.getDBService();
        
        String paramActionCategory = "paramActionCategory"; 
        
        String query = "select a from ProductivityWeights a " +
        " where a.actionCategory = :" + paramActionCategory ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramActionCategory, actionCategory.toString());
        
        List<?> weights = dbs.doHQL(query, parameters);
        //List<ProductivityWeights> w = dbs.findObjectsByProperties(ProductivityWeights.class, parameters);
        
        if(weights == null || weights.size() == 0) {
            return null;
        }else {
            return (ProductivityWeights)weights.get(0);
        }
        
    }
    
}
