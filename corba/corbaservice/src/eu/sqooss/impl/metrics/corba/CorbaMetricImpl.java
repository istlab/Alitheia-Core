package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MetricType;

/**
 * Abstract wrapper for a type independent metric used for interaction
 * with Corba metrics.
 * @author christoph
 *
 */
abstract public class CorbaMetricImpl extends AbstractMetric {

    eu.sqooss.impl.service.corba.alitheia.AbstractMetric m;

    DBService db = null;
    
    public CorbaMetricImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.AbstractMetric m) {
        super(bc);
        this.m = m;
        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        db = ((AlitheiaCore) bc.getService(serviceRef)).getDBService();
    }

    /**
     * Adds a supported metric description to the database.
     * This methods just wraps addSupportedMetrics to make it public available for
     * eu.sqooss.impl.service.corba.alitheia.core.CoreImpl.
     * @param desc 
     * @param mnemonic
     * @param type
     * @return
     */
    public boolean doAddSupportedMetrics(String desc, String mnemonic, MetricType.Type type) {
        final boolean startedSession = !db.isDBSessionActive(); 
        if (startedSession) {
            db.startDBSession();
        }
        final boolean result = addSupportedMetrics(desc, mnemonic, type);
        if (startedSession) {
            db.commitDBSession();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthor() {
        return m.getAuthor();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return m.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return m.getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return m.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    public boolean install() {
        final boolean startedSession = !db.isDBSessionActive(); 
        if (startedSession) {
            db.startDBSession();
        }
        boolean result = super.install();
        // w/o commiting here, the Plugin won't be there
        db.commitDBSession();
        db.startDBSession();
        result = result && m.doInstall();
        if (startedSession) {
            db.commitDBSession();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove() {
        return m.doRemove();
    }

    /**
     * {@inheritDoc}
     */
    public boolean update() {
        return m.doUpdate();
    }
}
