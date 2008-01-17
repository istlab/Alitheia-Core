package eu.sqooss.service.pa;

/**
 * The Class MetricInfo.
 */
public class MetricInfo {
    private long        bundleID        = -1;
    private String      bundleName      = null;
    private Long        serviceID       = new Long(-1);
    private String      metricName      = null;
    private String      metricVersion   = null;
    private String[]    objectClass     = null;
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
        if ((objectClass != null) && (objectClass.length > 0)) {
            for (int i=0 ; i < objectClass.length ; i++) {
                if (objectClass[i].equals(class_name)) {
                    return true;
                }
            }
        }
        
        return false;
    }

}
