package eu.sqooss.impl.service.security.utils;

import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityPrivilege;
import eu.sqooss.service.security.SecurityResourceURL;

public class DatabaseUtility {

    public static String userName = "anonymous";
    public static String password = "anonymous";
    public static String resourceUrl = "svc://sqooss.db";
    public static String privilegeName = "testPrivilege";
    public static String privilegeValue = "testPrivilegeValue";

    /* create methods */
    public static void initializeDatabase() {

    }

    public static long createUser(String userName, String password) {
        return 0;
    }

    public static long createGroup(String description) {
        return 0;
    }

    public static long createURL(String url) {
        return 0;
    }

    public static void addUserToGroup(long userId, long groupId) {
    }

    public static void createAuthorizationRule(SecurityGroup group, SecurityResourceURL url,
            SecurityPrivilege privilege) {
        //check whether the group is from SecurityGroupImpl class,...
    }
    /* create methods */

    /* remove methods */
    public static void removeUser(long userId) {
    }

    public static void removeGroup(long groupId) {
    }

    public static void removeURL(long urlId) {
    }

    public static void removeUserFromGroup(long userId, long groupId) {
    }

    public static void removeAuthorizationRule(long groupId, long urlId, long privilegeId) {

    }
    /* remove methods */

    /* get methods */
    public static String getUserName(long userId) {
        return null;
    }

    public static long[] getUserGroupsId(long userId) {
        return null;
    }

    public static String getGroupDescription(long groupId) {
        return null;
    }

    public static String getURL(long id) {
        return null;
    }

    public static SecurityAuthorizationRule[] getAuthorizationRules() {
        return null;
    }

    public static boolean isExistentGroup(long groupId) {
        return false;
    }

    public static boolean isExistentResourceUrl(long urlId) {
        return false;
    }

    public static boolean isExistentResourceUrl(String resourceUrl) {
        if (DatabaseUtility.resourceUrl.equals(resourceUrl)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExistentUser(long userId) {
        return false;
    }
    /* get methods */

    /* set methods */
    public static void setGroupDescription(long groupId, String description) {
    }

    public static void setResourceUrl(long resourceUrlId, String url) {
    }

    public static void setUserPassword(long userId, String password) {
    }

    public static void setUserName(long userId, String userName) {
    }
    /* set methods */

    public static boolean checkAuthorizationRule(String resourceUrl, String privilegeName,
            String privilegeValue, String userName, String password) {
        if ((DatabaseUtility.resourceUrl.equals(resourceUrl) &&
                (DatabaseUtility.privilegeName.equals(privilegeName)) &&
                (DatabaseUtility.privilegeValue.equals(privilegeValue)) &&
                (DatabaseUtility.userName.equals(userName)) &&
                (DatabaseUtility.password.equals(password)))) {
            return true;
        } else {
            return false;
        }
    }

}
