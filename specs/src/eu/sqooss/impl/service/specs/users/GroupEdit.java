package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpUser;

@RunWith(ConcordionRunner.class)
public class GroupEdit
{
    public void addGroup(String groupName)
    {
        new SpGroup(groupName).create();
    }

    public void addUserToGroup(String userName, String groupName)
    {
        new SpUser(userName, userName, userName+"@sqo-oss.org").create();
        new SpGroup(groupName).addUser(userName);
    }

    public void renameGroup(String groupName, String newGroupName)
    {
        new SpGroup(groupName).rename(newGroupName);
    }
    
    public ArrayList<SpUser> getUsersForGroup(String groupName)
    {
        return new SpGroup(groupName).getUsers();
    }
}
