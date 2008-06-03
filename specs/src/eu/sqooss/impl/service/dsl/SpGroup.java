package eu.sqooss.impl.service.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
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
        gm.createGroup(name);
        db.commitDBSession();
        persistent = true;
    }

    public void delete() {
        db.startDBSession();
        gm.deleteGroup(id);
        db.commitDBSession();
        persistent = false;
    }
    
    public void addUser(String userName) {
        load();
        SpUser user = new SpUser(userName);
        db.startDBSession();
        gm.addUserToGroup(id, user.id);
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
}
