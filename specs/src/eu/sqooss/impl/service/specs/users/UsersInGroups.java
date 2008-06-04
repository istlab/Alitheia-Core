package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpUser;

@RunWith(ConcordionRunner.class)
public class UsersInGroups
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

    public void ensureGroupIsEmpty(String groupName)
    {
        if (!new SpGroup(groupName).getUsers().isEmpty()) {
            throw new RuntimeException(groupName + " is supposed to be empty");
        }
    }
    
    public void moveUser(String userName, String from, String to)
    {
        new SpGroup(from).removeUser(userName);
        new SpGroup(to).addUser(userName);
    }
    
    public ArrayList<SpUser> getUsersForGroup(String groupName)
    {
        return new SpGroup(groupName).getUsers();
    }
}
