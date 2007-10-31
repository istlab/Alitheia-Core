package eu.sqooss.impl.service.security.utils;

public class PrivilegeDatabaseUtility {

    /* create methods */
    public static long createPrivilege(String description) {
        return 0;
    }

    public static long addPrivilegeValue(long privilegeId, String privilegeValue) {
        return 0; //privilege value id
    }
    /* create methods */

    /* remove methods */
    public static void removePrivilege(long privilegeId) {
    }

    public static boolean removePrivilegeValue(long privilegeId, long privilegeValueId) {
        return false;
    }

    public static void removePrivilegeValues(long privilegeId) {

    }
    /* remove methods */

    /* get methods */
    public static String getPrivilegeDescription(long privilegeId) {
        return null;
    }

    public static long getPrivilegeValueId(long privilegeId, String value) {
        return 0;
    }

    public static String[] getPrivilegeValues(long privilegeId) {
        return null;
    }

    public static boolean isExistentPrivilege(long privilegeId) {
        return false;
    }

    public static boolean isExistentPrivilege(String privilegeName) {
//      return false;
        if (DatabaseUtility.privilegeName.equals(privilegeName)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExistentPrivilegeValue(long privilegeValueId) {
        return false;
    }
    /* get methods */

    /* set methods */
    public static void setPrivilegeDescription(long privilegeId, String description) {
    }

    public static void setPrivilegeValues(long privilegeId, String[] privilegeValues) {
    }
    /* set methods */

}
