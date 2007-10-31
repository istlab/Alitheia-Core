package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.impl.service.security.utils.ParserUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityResourceURL;

public class SecurityResourceURLImpl implements SecurityResourceURL {

    private long resourceUrlId;

    public SecurityResourceURLImpl(long resourceUrlId) {
        this.resourceUrlId = resourceUrlId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityResourceURL#getId()
     */
    public long getId() {
        return resourceUrlId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityResourceURL#getURL()
     */
    public String getURL() {
        return DatabaseUtility.getURL(resourceUrlId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityResourceURL#setURL(java.lang.String)
     */
    public void setURL(String resourceURL) {
        ValidateUtility.validateValue(resourceURL);

        String mangledUrl = ParserUtility.mangleUrl(resourceURL);
        DatabaseUtility.setResourceUrl(resourceUrlId, mangledUrl);
    }

    /**
     * @see eu.sqooss.service.security.SecurityResourceURL#remove()
     */
    public void remove() {
        DatabaseUtility.removeURL(resourceUrlId);

        SecurityActivator.log(this + " is removed", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Resource url (url id = " + resourceUrlId + ")";
    }

    /**
     * Two <code>SecurityResourceURLImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityResourceURLImpl)) {
            return false;
        } else {
            SecurityResourceURLImpl other = (SecurityResourceURLImpl)obj;
            return (resourceUrlId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(resourceUrlId^(resourceUrlId>>>32));
    }

}
