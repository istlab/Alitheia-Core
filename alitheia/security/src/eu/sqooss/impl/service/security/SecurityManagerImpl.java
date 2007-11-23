package eu.sqooss.impl.service.security;

import java.util.Enumeration;
import java.util.Hashtable;

import eu.sqooss.impl.service.SecurityActivator;
import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.impl.service.security.utils.ParserUtility;
import eu.sqooss.impl.service.security.utils.PrivilegeDatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.SecurityPrivilege;
import eu.sqooss.service.security.SecurityResourceURL;
import eu.sqooss.service.security.SecurityUser;

public class SecurityManagerImpl implements SecurityManager {

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String fullUrl, String userName, String password) {
        Hashtable < String, String > privileges = new Hashtable < String, String >();
        String resourceUrl = ParserUtility.mangleUrlWithPrivileges(fullUrl, privileges);
        return checkPermission(resourceUrl, privileges, userName, password);
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.util.Hashtable, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String resourceURL, Hashtable privileges, String userName, String password) {
        ValidateUtility.validateValue(resourceURL);

        String tmpUrl = resourceURL;
        while (true) {
            if (DatabaseUtility.isExistentResourceUrl(tmpUrl)) {
                String currentPrivilegeName;
                String currentPrivilegeValue;
                for (Enumeration keys = privileges.keys(); keys.hasMoreElements(); ) {
                    currentPrivilegeName = (String)keys.nextElement();
                    currentPrivilegeValue = (String)privileges.get(currentPrivilegeName);
                    if (!(DatabaseUtility.checkAuthorizationRule(tmpUrl, currentPrivilegeName, currentPrivilegeValue, userName, password))) {
                        return false;
                    }
                }
                return true;
            } else {
                int lastIndexOfAmpersand = tmpUrl.lastIndexOf('&');
                if (lastIndexOfAmpersand == -1) {
                    int firstIndexOfQuestionMark = tmpUrl.indexOf('?');
                    if (firstIndexOfQuestionMark == -1) {
                        return false;
                    } else {
                        tmpUrl = tmpUrl.substring(0, firstIndexOfQuestionMark);
                    }
                }
            }
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createAuthorizationRule(eu.sqooss.service.security.SecurityGroup, long, eu.sqooss.service.security.SecurityResourceURL)
     */
    public SecurityAuthorizationRule createAuthorizationRule(SecurityGroup group,
            long privilegeValueId, SecurityResourceURL resourceURL) {

        if ((PrivilegeDatabaseUtility.isExistentPrivilegeValue(privilegeValueId)) &&
                (group instanceof SecurityGroupImpl) &&
                (resourceURL instanceof SecurityResourceURLImpl)) {
            SecurityAuthorizationRule newRule = new SecurityAuthorizationRuleImpl(group.getId(), resourceURL.getId(), privilegeValueId);
            SecurityActivator.log(newRule + " is created", SecurityActivator.LOGGING_INFO_LEVEL);
            return newRule;
        } else {
            SecurityActivator.log("Can't create a authorization rule with: group id = " + group.getId() +
                    "; privilege value id = " + privilegeValueId + "; url id = " + resourceURL.getId(), SecurityActivator.LOGGING_INFO_LEVEL);
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createGroup(java.lang.String)
     */
    public SecurityGroup createGroup(String description) {
        ValidateUtility.validateValue(description);
        long groupId = DatabaseUtility.createGroup(description);
        SecurityGroup newGroup = new SecurityGroupImpl(groupId);
        SecurityActivator.log(newGroup + " is created", SecurityActivator.LOGGING_INFO_LEVEL);
        return newGroup;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createPrivilege(java.lang.String)
     */
    public SecurityPrivilege createPrivilege(String description) {
        ValidateUtility.validateValue(description);
        long privilegeId = PrivilegeDatabaseUtility.createPrivilege(description);
        SecurityPrivilege newPrivilege = new SecurityPrivilegeImpl(privilegeId);
        SecurityActivator.log(newPrivilege + " is created", SecurityActivator.LOGGING_INFO_LEVEL);
        return newPrivilege;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createResourceURL(java.lang.String)
     */
    public SecurityResourceURL createResourceURL(String resourceURL) {
        ValidateUtility.validateValue(resourceURL);

        String mangledUrl = ParserUtility.mangleUrl(resourceURL);
        long urlId = DatabaseUtility.createURL(mangledUrl);
        SecurityResourceURL newResourceUrl = new SecurityResourceURLImpl(urlId);
        SecurityActivator.log(newResourceUrl + " is created", SecurityActivator.LOGGING_INFO_LEVEL);
        return newResourceUrl;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createUser(java.lang.String, java.lang.String)
     */
    public SecurityUser createUser(String userName, String password) {
        ValidateUtility.validateValue(userName);
        ValidateUtility.validateValue(password);
        long userId = DatabaseUtility.createUser(userName, password);
        SecurityUser newUser = new SecurityUserImpl(userId);
        SecurityActivator.log(newUser + " is created", SecurityActivator.LOGGING_INFO_LEVEL);
        return newUser;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getGroup(long)
     */
    public SecurityGroup getGroup(long id) {
        if (DatabaseUtility.isExistentGroup(id)) {
            return new SecurityGroupImpl(id);
        } else {
            SecurityActivator.log("The group with id = " + id + " doesn't exist", SecurityActivator.LOGGING_INFO_LEVEL);
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getPrivilege(long)
     */
    public SecurityPrivilege getPrivilege(long id) {
        if (PrivilegeDatabaseUtility.isExistentPrivilege(id)) {
            return new SecurityPrivilegeImpl(id);
        } else {
            SecurityActivator.log("The privilege with id = " + id + " doesn't exist", SecurityActivator.LOGGING_INFO_LEVEL);
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getReourceURL(long)
     */
    public SecurityResourceURL getReourceURL(long id) {
        if (DatabaseUtility.isExistentResourceUrl(id)) {
            return new SecurityResourceURLImpl(id);
        } else {
            SecurityActivator.log("The resource url with id = " + id + " doesn't exist", SecurityActivator.LOGGING_INFO_LEVEL);
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getUser(long)
     */
    public SecurityUser getUser(long id) {
        if (DatabaseUtility.isExistentUser(id)) {
            return new SecurityUserImpl(id);
        } else {
            SecurityActivator.log("The user with id = " + id + " doesn't exist", SecurityActivator.LOGGING_INFO_LEVEL);
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getAuthorizationRules()
     */
    public SecurityAuthorizationRule[] getAuthorizationRules() {
        return DatabaseUtility.getAuthorizationRules();
    }

}
