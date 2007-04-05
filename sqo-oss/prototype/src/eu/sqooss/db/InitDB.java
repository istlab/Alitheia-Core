package eu.sqooss.db;

import org.hibernate.Session;

import eu.sqooss.util.HibernateUtil;

/**
 * Initializes the database with the default values
 * (invoke only once)
 */
public class InitDB {
    private static MetricType addMetricType(String type) {
        MetricType mt = new MetricType();
        
        mt.setType(type);
        
        return mt;
    }
    
    public static void main(String[] args) {
        Session session =
            HibernateUtil.getSessionFactory().getCurrentSession();
        
        
        session.beginTransaction();
        
        // add metric_type
        session.save(addMetricType("Code Metric"));
        session.save(addMetricType("Statistical measure"));
        
        // add plugins
        
        // add metrics
        
        session.getTransaction().commit();
        
        // close the session
        HibernateUtil.getSessionFactory().close();
    }
}
