package eu.sqooss.service.abstractmetric;

import java.util.HashMap;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.util.Pair;

public class LockManager {
	private AbstractMetric parent;
	private Logger log;
	private Map<Long,Pair<Object,Integer>> locks = new HashMap<Long,Pair<Object,Integer>>();
	
	public LockManager( AbstractMetric parent, Logger log ) {
		this.parent = parent;
		this.log = log;
	}
    
    public Object lockObject(DAObject o) throws AlreadyProcessingException {
    	synchronized (locks) {
            if (!locks.containsKey(o.getId())) {
                locks.put(o.getId(), 
                        new Pair<Object, Integer>(new Object(),0));
            }
            Pair<Object, Integer> p = locks.get(o.getId());
            if (p.second + 1 > 1) {
                /*
                 * Break and reschedule the calculation of each call to the
                 * getResult method if it originates from another thread than
                 * the thread that has currently locked the DAO object. 
                 * This is required for the DB transaction in the stopped
                 * job to see the results of the calculation of the original
                 * job.
                 */ 
                log.debug("DAO Id:" + o.getId() + 
                        " Already locked - failing job");
                try {
                    throw new AlreadyProcessingException();
                } finally {
                    MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
                    ma.runMetric(o, parent);
                }
            }
            p.second = p.second + 1;
            return p.first;
        }
    }
    
    public void unlockObject(DAObject o) {
    	synchronized(locks) {
    		Pair<Object,Integer> p = locks.get(o.getId());
    		p.second = p.second - 1;
    		if (p.second == 0) {
    			locks.remove(o.getId());
    		} else {
    		log.debug("Unlocking DAO Id:" + o.getId());
    		}
    	}
    }
}
