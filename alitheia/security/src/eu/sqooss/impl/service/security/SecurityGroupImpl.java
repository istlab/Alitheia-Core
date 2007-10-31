package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityGroup;

public class SecurityGroupImpl implements SecurityGroup {

    private long groupId;

    public SecurityGroupImpl(long groupId) {
        this.groupId = groupId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#getDescription()
     */
    public String getDescription() {
        return DatabaseUtility.getGroupDescription(groupId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#getId()
     */
    public long getId() {
        return groupId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        ValidateUtility.validateValue(description);
        DatabaseUtility.setGroupDescription(groupId, description);
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#remove()
     */
    public void remove() {
        DatabaseUtility.removeGroup(groupId);

        SecurityActivator.log(this + " is removed", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Group (group id = " + groupId + ")";
    }

    /**
     * Two <code>SecurityGroupImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityGroupImpl)) {
            return false;
        } else {
            SecurityGroupImpl other = (SecurityGroupImpl)obj;
            return (groupId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(groupId^(groupId>>>32));
    }

}
