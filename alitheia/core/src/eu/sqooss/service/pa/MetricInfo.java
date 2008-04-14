package eu.sqooss.service.pa;

import org.osgi.framework.ServiceReference;
import eu.sqooss.service.util.StringUtils;
import java.util.HashMap;

/**
 * The Class MetricInfo.
 */
public class MetricInfo {
    private long        bundleID        = -1;
    private String      bundleName      = null;
    private Long        serviceID       = new Long(-1);
    private ServiceReference serviceRef = null;
    private String      metricName      = null;
    private String      metricVersion   = null;
    private String[]    objectClass     = null;
    private String[]    metricType      = null;
    public boolean      installed       = false;
    public HashMap      attributes      = null;

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
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * @return the metricName
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * @param metricVersion the metricVersion to set
     */
    public void setMetricVersion(String metricVersion) {
        this.metricVersion = metricVersion;
    }

    /**
     * @return the metricVersion
     */
    public String getMetricVersion() {
        return metricVersion;
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
    public String[] getMetricType() {
        return metricType;
    }

    /**
     * @param metricClass the metricClass to set
     */
    public void setMetricType(String[] objectType) {
        this.metricType = objectType;
    }

    public boolean isType(String class_name) {
        return StringUtils.contains(metricType, class_name);
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
        b.append(getMetricName());
        b.append(" ");
        b.append(getMetricVersion());
        b.append(" [");
        b.append(StringUtils.join(getMetricType(),","));
        b.append(" : ");
        b.append(StringUtils.join(getObjectClass(),","));
        b.append("]");
        return b.toString();
    }

    public void setAttributes(HashMap attributes) {
        this.attributes = attributes;
    }

    public HashMap getAttributes() {
        return attributes;
    }
}
