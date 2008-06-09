package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpUser;
import eu.sqooss.service.db.GroupType;

@RunWith(ConcordionRunner.class)
public class GroupEdit
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

    public void renameGroup(String groupName, String newGroupName, GroupType.Type groupType)
    {
        new SpGroup(groupName, groupType).rename(newGroupName);
    }
    
    public ArrayList<SpUser> getUsersForGroup(String groupName, GroupType.Type groupType)
    {
        return new SpGroup(groupName, groupType).getUsers();
    }
}
