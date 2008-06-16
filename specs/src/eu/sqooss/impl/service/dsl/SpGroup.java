package eu.sqooss.impl.service.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.GroupType;
import eu.sqooss.service.db.User;
import eu.sqooss.service.security.GroupManager;

public class SpGroup implements SpEntity {
    private DBService db = SpecsActivator.alitheiaCore.getDBService();
    private GroupManager gm = SpecsActivator.alitheiaCore.getSecurityManager().getGroupManager();

    long id = -1;
    boolean persistent = false;
    
    public String name;
    
    public static ArrayList<SpGroup> allGroups() {
        DBService db = SpecsActivator.alitheiaCore.getDBService();
        ArrayList<SpGroup> result = new ArrayList<SpGroup>();
        
        db.startDBSession();
        List<Group> groups = db.findObjectsByProperties(Group.class, new HashMap<String,Object>());
        
        for (Group group : groups) {
            result.add(new SpGroup(group.getDescription(), group.getId()));
        }
        db.commitDBSession();
        
        return result;
    }

    public static boolean groupExists(String groupName) {
        DBService db = SpecsActivator.alitheiaCore.getDBService();
        boolean result = false;

        db.startDBSession();
        List<Group> groups = db.findObjectsByProperties(Group.class, new HashMap<String,Object>());
        
        for (Group group : groups) {
            if (groupName.compareTo(group.getDescription()) == 0)
            {
                result = true;
            }
        }
        db.commitDBSession();
        
        return result;
    }
    
    public SpGroup(String n) {
        name = n;
        load();
    }
    
    private SpGroup(String n, long i) {
        name = n;
        id = i;
        persistent = true;
    }
    
    public void load() {
        db.startDBSession();
        Group group = gm.getGroup(name);
        
        if (group==null) {
            persistent = false;
            return;
        }
        
        id = group.getId();
        name = group.getDescription();
        db.commitDBSession();
    }

    public void create() {
        db.startDBSession();
        gm.createGroup(name, GroupType.Type.USER);
        db.commitDBSession();
        persistent = true;
    }

    public void delete() {
        db.startDBSession();
        gm.deleteGroup(id);
        db.commitDBSession();
        persistent = false;
    }
    
    public void rename(String newName) {
        db.startDBSession();
        Group group = gm.getGroup(name);
        group.setDescription(newName);
        db.commitDBSession();
        name = newName;
    }
    
    public void addUser(String userName) {
        load();
        SpUser user = new SpUser(userName);
        db.startDBSession();
        gm.addUserToGroup(id, user.id);
        db.commitDBSession();
    }

    public void removeUser(String userName) {
        load();
        SpUser user = new SpUser(userName);
        db.startDBSession();
        gm.deleteUserFromGroup(id, user.id);
        db.commitDBSession();
    }
    
    public ArrayList<SpPrivilege> getPrivileges() {
        ArrayList<SpPrivilege> result = new ArrayList<SpPrivilege>();
        
        db.startDBSession();
        Group group = gm.getGroup(name);
        
        if (group==null) {
            return result;
        }

        for (Object obj : group.getGroupPrivileges()) {
            GroupPrivilege priv = (GroupPrivilege)obj;

            String privValue = priv.getPv().getValue(); 
            if (priv.getPv().getPrivilege().getDescription().equals("user_id")) {
                User user = db.findObjectById(User.class, Long.valueOf(privValue));
                privValue = user.getName();
            }
            
            result.add(new SpPrivilege(
                    group.getDescription(),
                    priv.getUrl().getUrl(),
                    priv.getPv().getPrivilege().getDescription(),
                    privValue
            ));
        }
        
        return result;
    }
    
    public ArrayList<SpUser> getUsers() {
        ArrayList<SpUser> result = new ArrayList<SpUser>();
        
        db.startDBSession();
        Group group = gm.getGroup(name);
        
        if (group==null) {
            return result;
        }

        TreeSet<String> userNames = new TreeSet<String>();
        for (Object obj : group.getUsers()) {
            userNames.add(((User)obj).getName());
        }
        for (String name : userNames) {
            result.add(new SpUser(name));
        }
        
        return result;
    }
}
