package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpUser;
import eu.sqooss.service.db.GroupType;

@RunWith(ConcordionRunner.class)
public class UsersInGroups
{
    public void addGroup(String groupName, GroupType.Type groupType)
    {
        new SpGroup(groupName, groupType).create();
    }

    public void addUserToGroup(String userName, String groupName, GroupType.Type groupType)
    {
        new SpUser(userName, userName, userName+"@sqo-oss.org").create();
        new SpGroup(groupName, groupType).addUser(userName);
    }

    public void ensureGroupIsEmpty(String groupName, GroupType.Type groupType)
    {
        if (!new SpGroup(groupName, groupType).getUsers().isEmpty()) {
            throw new RuntimeException(groupName + " is supposed to be empty");
        }
    }
    
    public void moveUser(String userName, String from, GroupType.Type groupTypeFrom,
            String to, GroupType.Type groupTypeTo)
    {
        new SpGroup(from, groupTypeFrom).removeUser(userName);
        new SpGroup(to, groupTypeTo).addUser(userName);
    }
    
    public ArrayList<SpUser> getUsersForGroup(String groupName, GroupType.Type groupType)
    {
        return new SpGroup(groupName, groupType).getUsers();
    }
}
