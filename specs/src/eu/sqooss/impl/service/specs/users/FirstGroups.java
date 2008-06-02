package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.User;

@RunWith(ConcordionRunner.class)
public class FirstGroups
{
    public ArrayList<SpGroup> getGroups()
    {
        return SpGroup.allGroups();
    }
    
    public ArrayList<Priv> getAllPrivileges()
    {
        ArrayList<Priv> result = new ArrayList<Priv>();
        
        DBService db = SpecsActivator.alitheiaCore.getDBService();

        db.startDBSession();
        List<Group> groups = db.findObjectsByProperties(Group.class, new HashMap<String,Object>());
        
        for (Group group : groups)
        {
            for (Object obj : group.getGroupPrivileges())
            {
                GroupPrivilege priv = (GroupPrivilege)obj;

                String privValue = priv.getPv().getValue(); 
                if (priv.getPv().getPrivilege().getDescription().equals("user_id"))
                {
                    User user = db.findObjectById(User.class, Long.valueOf(privValue));
                    privValue = user.getName();
                }
                
                result.add(new Priv(
                        group.getDescription(),
                        priv.getUrl().getUrl(),
                        priv.getPv().getPrivilege().getDescription(),
                        privValue
                ));
            }
        }
        db.commitDBSession();
        
        return result;
    }
    
    class Priv
    {
        public String group;
        public String service;
        public String type;
        public String value;
        
        public Priv(String g, String s, String t, String v)
        {
            group = g;
            service = s;
            type = t;
            value = v;
        }
    }
}
