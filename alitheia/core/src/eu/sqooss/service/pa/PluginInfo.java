package eu.sqooss.service.pa;

import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.util.StringUtils;
import java.util.Collection;

/**
 * The Class MetricInfo.
 */
public class PluginInfo {
    private long        bundleID        = -1;
    private String      bundleName      = null;
    private Long        serviceID       = new Long(-1);
    private ServiceReference serviceRef = null;
    private String      pluginName      = null;
    private String      pluginVersion   = null;
    private String[]    objectClass     = null;
    private String[]    pluginType      = null;
    public boolean      installed       = false;

    /**
     * @param bundleID the bundleID to set
     */
    public void setBundleID(long bundleID) {
        this.bundleID = bundleID;
    }

    /**
     * @return the bundleID
     */
    public long getBundleID() {
        return bundleID;
    }

    /**
     * @param bundleName the bundleName to set
     */
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * @return the bundleName
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * @param serviceID the serviceID to set
     */
    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    /**
     * @return the serviceID
     */
    public Long getServiceID() {
        return serviceID;
    }

    /**
     * @param metricName the metricName to set
     */
    public void setPluginName(String metricName) {
        this.pluginName = metricName;
    }

    /**
     * @return the metricName
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * @param metricVersion the metricVersion to set
     */
    public void setPluginVersion(String metricVersion) {
        this.pluginVersion = metricVersion;
    }

    /**
     * @return the metricVersion
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * @param objectClass the objectClass to set
     */
    public void setObjectClass(String[] objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * @return the objectClass
     */
    public String[] getObjectClass() {
        return objectClass;
    }

    public boolean usesClassName(String class_name) {
        return StringUtils.contains(objectClass, class_name);
    }

    /**
     * @return the metricClass
     */
    public String[] getPluginType() {
        return pluginType;
    }

    /**
     * @param metricClass the metricClass to set
     */
    public void setPluginType(String[] objectType) {
        this.pluginType = objectType;
    }

    public boolean isType(String class_name) {
        return StringUtils.contains(pluginType, class_name);
    }

    /**
     * @return the serviceRef
     */
    public ServiceReference getServiceRef() {
        return serviceRef;
    }

    /**
     * @param serviceRef the serviceRef to set
     */
    public void setServiceRef(ServiceReference serviceRef) {
        this.serviceRef = serviceRef;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getPluginName());
        b.append(" ");
        b.append(getPluginVersion());
        b.append(" [");
        b.append(StringUtils.join(getPluginType(),","));
        b.append(" : ");
        b.append(StringUtils.join(getObjectClass(),","));
        b.append("]");
        return b.toString();
    }
}
