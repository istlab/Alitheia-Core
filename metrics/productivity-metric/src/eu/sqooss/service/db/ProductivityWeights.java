package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions;
import eu.sqooss.impl.service.CoreActivator;

public class ProductivityWeights extends DAObject{

    private String actionCategory;
    private String actionType;
    private double weight;
    
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
    
    public double getWeight(){
        return weight;
    }
    
    public void setWeight(long weight){
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
