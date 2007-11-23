package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityResourceURL;

/**
 * This class implements the <code>SecurityAuthorizationRule</code> interface.
 */
public class SecurityAuthorizationRuleImpl implements SecurityAuthorizationRule {

    private long groupId;
    private long urlId;
    private long privilegeValueId;

    public SecurityAuthorizationRuleImpl(long groupId, long urlId, long privilegeValueId) {
        this.groupId = groupId;
        this.urlId = urlId;
        this.privilegeValueId = privilegeValueId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getGroup()
     */
    public SecurityGroup getGroup() {
        return new SecurityGroupImpl(groupId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getUrl()
     */
    public SecurityResourceURL getUrl() {
        return new SecurityResourceURLImpl(urlId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getPrivilegeValueId()
     */
    public long getPrivilegeValueId() {
        //TODO check if the rule exist
        return privilegeValueId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#remove()
     */
    public void remove() {
        DatabaseUtility.removeAuthorizationRule(groupId, urlId, privilegeValueId);

        SecurityActivator.log(this + " is removed", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Authorization rule (group id = " + groupId +
        "; url id = " + urlId + "; privilege value id = " + privilegeValueId + ")"; 
    }

    /**
     * Two <code>SecurityAuthorizationRuleImpl</code> objects are equal
     * if their identifiers of the group, url and privilege value are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityAuthorizationRuleImpl)) {
            return false;
        } else {
            SecurityAuthorizationRuleImpl other = (SecurityAuthorizationRuleImpl)obj;
            return ((groupId == other.getGroup().getId()) &&
                    (urlId == other.getUrl().getId()) &&
                    (privilegeValueId == other.getPrivilegeValueId()));
        }
    }

    public int hashCode() {
        return (int)((groupId << 20)|(urlId << 10) | privilegeValueId);
    }

}
