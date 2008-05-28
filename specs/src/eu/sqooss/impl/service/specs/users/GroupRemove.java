package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.User;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.UserManager;

@RunWith(ConcordionRunner.class)
public class GroupRemove
{
    private DBService db = SpecsActivator.alitheiaCore.getDBService();
    private GroupManager gm = SpecsActivator.alitheiaCore.getSecurityManager().getGroupManager();
    private UserManager um = SpecsActivator.alitheiaCore.getSecurityManager().getUserManager();

    public void addGroup(String groupName)
    {
        db.startDBSession();
        gm.createGroup(groupName);
        db.commitDBSession();
    }

    public void addUserToGroup(String userName, String groupName)
    {
        db.startDBSession();
        um.createUser(userName, userName, userName+"@sqo-oss.org");
        gm.addUserToGroup(gm.getGroup(groupName).getId(),
                          um.getUser(userName).getId());
        db.commitDBSession();
    }

    public void removeGroup(String groupName)
    {
        db.startDBSession();
        gm.deleteGroup(gm.getGroup(groupName).getId());
        db.commitDBSession();
    }
    
    public ArrayList<String> getGroups()
    {
        ArrayList<String> result = new ArrayList<String>();
        
        db.startDBSession();
        List<Group> groups = db.findObjectsByProperties(Group.class, new HashMap<String,Object>());
        
        for (Group group : groups)
        {
            result.add(group.getDescription());
        }
        db.commitDBSession();
        
        return result;
    }
    
    public ArrayList<SpUser> getUsers()
    {
        ArrayList<SpUser> result = new ArrayList<SpUser>();
        
        db.startDBSession();
        List<User> users = db.findObjectsByProperties(User.class, new HashMap<String,Object>());
        
        for (User user : users)
        {
            SpUser u = new SpUser();
            u.name = user.getName();
            u.groupList = "";
            
            TreeSet<String> groupNames = new TreeSet<String>();
            for (Object obj : user.getGroups())
            {
                Group g = (Group)obj;
                groupNames.add(g.getDescription());
            }
            
            boolean first = true;
            for (String groupName : groupNames)
            {
                if (!first) u.groupList+=", ";
                u.groupList+=groupName;
                first = false;
            }
            
            result.add(u);
        }
        db.commitDBSession();
        
        return result;        
    }
    
    public class SpUser
    {
        public String name;
        public String groupList;
    }
}
