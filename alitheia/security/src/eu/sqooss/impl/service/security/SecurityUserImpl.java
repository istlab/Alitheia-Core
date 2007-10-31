package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityUser;

public class SecurityUserImpl implements SecurityUser {

    private long userId;

    public SecurityUserImpl(long userId) {
        this.userId = userId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#addToGroup(eu.sqooss.service.security.SecurityGroup)
     */
    public void addToGroup(SecurityGroup group) {
        if (!(group instanceof SecurityGroupImpl)) {
            throw new IllegalArgumentException("The group must be created with security manager!");
        }

        DatabaseUtility.addUserToGroup(userId, group.getId());
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getGroups()
     */
    public SecurityGroup[] getGroups() {
        long[] groupsId = DatabaseUtility.getUserGroupsId(userId);

        SecurityGroup[] groups = new SecurityGroup[groupsId.length];

        for (int i = 0; i < groupsId.length; i++) {
            groups[i] = new SecurityGroupImpl(groupsId[i]);
        }

        return groups;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getId()
     */
    public long getId() {
        return userId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getUserName()
     */
    public String getUserName() {
        return DatabaseUtility.getUserName(userId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#removeFromGroup(eu.sqooss.service.security.SecurityGroup)
     */
    public void removeFromGroup(SecurityGroup group) {
        if (!(group instanceof SecurityGroupImpl)) {
            throw new IllegalArgumentException("The group must be created with security manager!");
        }

        DatabaseUtility.removeUserFromGroup(userId, group.getId());
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        ValidateUtility.validateValue(password);
        DatabaseUtility.setUserPassword(userId, password);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#setUserName(java.lang.String)
     */
    public void setUserName(String userName) {
        ValidateUtility.validateValue(userName);
        DatabaseUtility.setUserName(userId, userName);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#remove()
     */
    public void remove() {
        DatabaseUtility.removeUser(userId);

        SecurityActivator.log(this + " is removed", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "User (user id = " + userId + ")";
    }

    /**
     * Two <code>SecurityUserImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityUserImpl)) {
            return false;
        } else {
            SecurityUserImpl other = (SecurityUserImpl)obj;
            return (userId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(userId^(userId>>>32));
    }

}
