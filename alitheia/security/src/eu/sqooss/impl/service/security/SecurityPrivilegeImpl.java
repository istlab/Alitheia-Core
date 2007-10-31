package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.PrivilegeDatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityPrivilege;

public class SecurityPrivilegeImpl implements SecurityPrivilege {

    private long privilegeId;

    public SecurityPrivilegeImpl(long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#addValue(java.lang.String)
     */
    public long addValue(String value) {
        ValidateUtility.validateValue(value);
        return PrivilegeDatabaseUtility.addPrivilegeValue(privilegeId, value);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getDescription()
     */
    public String getDescription() {
        return PrivilegeDatabaseUtility.getPrivilegeDescription(privilegeId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getId()
     */
    public long getId() {
        return privilegeId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getValueId(java.lang.String)
     */
    public long getValueId(String value) {
        return PrivilegeDatabaseUtility.getPrivilegeValueId(privilegeId, value);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getValues()
     */
    public String[] getValues() {
        return PrivilegeDatabaseUtility.getPrivilegeValues(privilegeId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#removeValue(java.lang.String)
     */
    public boolean removeValue(String value) {
        long privilegeValueId = PrivilegeDatabaseUtility.getPrivilegeValueId(privilegeId, value);
        return PrivilegeDatabaseUtility.removePrivilegeValue(privilegeId, privilegeValueId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#removeValue(long)
     */
    public boolean removeValue(long id) {
        return PrivilegeDatabaseUtility.removePrivilegeValue(privilegeId, id);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        ValidateUtility.validateValue(description);
        PrivilegeDatabaseUtility.setPrivilegeDescription(privilegeId, description);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#setValues(java.lang.String[])
     */
    public void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            ValidateUtility.validateValue(values[i]);
        }

        PrivilegeDatabaseUtility.removePrivilegeValues(privilegeId);
        PrivilegeDatabaseUtility.setPrivilegeValues(privilegeId, values);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#remove()
     */
    public void remove() {
        PrivilegeDatabaseUtility.removePrivilege(privilegeId);

        SecurityActivator.log(this + " is removed", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Privilege (privilege id = " + privilegeId + ")";
    }

    /**
     * Two <code>SecurityPrivilegeImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityPrivilegeImpl)) {
            return false;
        } else {
            SecurityPrivilegeImpl other = (SecurityPrivilegeImpl) obj;
            return (privilegeId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(privilegeId^(privilegeId>>>32));
    }

}
