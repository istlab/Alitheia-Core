package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.MetricType;

/**
 * Abstract wrapper for a type independent metric used for interaction
 * with Corba metrics.
 * @author christoph
 *
 */
abstract public class CorbaMetricImpl extends AbstractMetric {

    eu.sqooss.impl.service.corba.alitheia.AbstractMetric m;

    public CorbaMetricImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.AbstractMetric m) {
        super(bc);
        this.m = m;
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
        return addSupportedMetrics(desc, mnemonic, type);
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
        return super.install() && m.doInstall();
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
