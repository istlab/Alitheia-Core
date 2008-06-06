package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpUser;

@RunWith(ConcordionRunner.class)
public class UserRemove
{

    public void addUser(String userName, String email, String password)
    {
        new SpUser(userName, password, email).create();
    }
    
    public void removeUser(String userName)
    {
        new SpUser(userName).delete();
    }


    public ArrayList<SpUser> getUsers()
    {
        return SpUser.allUsers();
    }
    
}
