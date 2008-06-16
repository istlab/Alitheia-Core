package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.User;
import eu.sqooss.service.security.UserManager;

import eu.sqooss.impl.service.dsl.SpUser;
import eu.sqooss.impl.service.dsl.SpGroup;

@RunWith(ConcordionRunner.class)
public class UserAdd
{
    private DBService db = SpecsActivator.alitheiaCore.getDBService();
    private UserManager um = SpecsActivator.alitheiaCore.getSecurityManager().getUserManager();

    public void addUser(String userName, String email, String password, String group)
    {
        new SpUser(userName, password, email).create();
        SpGroup gr = new SpGroup(group);
        gr.create();
        gr.addUser(userName);
    }
    
    //tries to add an user. returns "true" if the addition FAILED
    public boolean addUserExtended(String userName, String email, String password)
    {
        db.startDBSession();
        User user = um.createUser(userName, password, email);
        db.commitDBSession();
        return (user == null);
    }

    public ArrayList<SpUser> getUsers()
    {
        return SpUser.allUsers();
    }
    
    public boolean memberOf(String userName, String groupName)
    {
        return new SpUser(userName).isMemberOf(groupName);
    }

    public boolean groupExists(String groupName)
    {
        return SpGroup.groupExists(groupName);
    }

  
}
