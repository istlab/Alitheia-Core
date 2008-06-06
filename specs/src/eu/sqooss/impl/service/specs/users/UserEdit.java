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
public class UserEdit
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
    
    public ArrayList<SpUser> getUsers()
    {
        return SpUser.allUsers();
    }
    
    public ArrayList<SpUser> getUserInfo(String userName)
    {
        ArrayList<SpUser> result = new ArrayList<SpUser>();
        result.add(new SpUser(userName));
        return result;
    }

    public void changeEmail(String userName, String email)
    {
        SpUser user = new SpUser(userName);
        user.changeEmail(email);
    }
 
    public void changePassword(String userName, String password)
    {
        SpUser user = new SpUser(userName);
        user.changePassword(password);
    }
}
