/**
 * 
 */
package eu.sqooss.impl.service.dsl;

import java.util.ArrayList;

public class SpPrivilege
{
    public String group;
    public String service;
    public String type;
    public String value;
    
    public SpPrivilege(String g, String s, String t, String v) {
        group = g;
        service = s;
        type = t;
        value = v;
    }
    
    public static ArrayList<SpPrivilege> getAllPrivileges() {
        ArrayList<SpPrivilege> result = new ArrayList<SpPrivilege>();
        
        ArrayList<SpGroup> groups = new ArrayList<SpGroup>();
        for (SpGroup group : groups) {
            result.addAll(group.getPrivileges());
        }
        
        return result;
    }
}