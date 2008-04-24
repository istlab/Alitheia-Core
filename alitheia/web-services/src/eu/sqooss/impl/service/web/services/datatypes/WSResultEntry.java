package eu.sqooss.impl.service.web.services.datatypes;

import eu.sqooss.service.abstractmetric.ResultEntry;

// TODO: Auto-generated Javadoc
/**
 * The Class WSResultEntry.
 */
public class WSResultEntry {
    /** The ResultEntry instance wrapped by this class */
    private ResultEntry result;

    /** Database Id of the metric that generated this result */
    private long metricID;

    /**
     * Instantiates a new WSResult entry, that wraps the ResultEntry
     * parameter.
     * 
     * @param result a ResultEntry instance
     */
    public WSResultEntry(ResultEntry result) {
        this.result = result;
    }

    /**
     * Gets the Id of the metric that generated this result.
     * 
     * @return the metric Id
     */
    public long getMetricID() {
        return metricID;
    }

    /**
     * Sets the Id of the metric that generated this result
     * 
     * @param metricID the metric Id
     */
    public void setMetricID(long metricID) {
        this.metricID = metricID;
    }

    /**
     * Gets the MIME type of the stored result.
     * 
     * @return The MIME type of the <code>ResultEntry</code>.
     */
    public String getMimeType() {
        return result.getMimeType();
    }

    /**
     * Gets the <code>byte[]</code> representation of the stored result.
     * 
     * @return The byte array representation of the <code>ResultEntry</code>.
     */
    public byte[] getByteArray() {
        return result.getByteArray();
    }

    /**
     * Gets the <code>int</code> value of the stored result.
     * 
     * @return The integer value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>int</code>
     */
    public int getInteger() throws IllegalStateException {
        return result.getInteger();
    }

    /**
     * Gets the <code>long</code> value of the stored result.
     * 
     * @return The long value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>long</code>
     */
    public long getLong() throws IllegalStateException {
        return result.getLong();
    }

    /**
     * Gets the <code>float</code> value of the stored result.
     * 
     * @return The float value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>float</code>
     */
    public float getFloat() throws IllegalStateException {
        return result.getFloat();
    }

    /**
     * Gets the <code>double</code> value of the stored result.
     * 
     * @return The double value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>double</code>
     */
    public double getDouble() throws IllegalStateException {
        return result.getDouble();
    }
    
    /**
     * Gets the <code>String</code> value of the stored result.
     * 
     * @return The string value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>String</code>
     */
    public String getString() throws IllegalStateException {
        return result.getString();
    }
    
    /**
     * Gets the object that internally stores the result's value.
     * 
     * @return The object that stores the <code>ResultEntry</code>'s value.
     */
    public Object getObject() {
        return result.getObject();
    }

}
