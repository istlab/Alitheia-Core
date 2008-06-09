package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.service.db.GroupType;

@RunWith(ConcordionRunner.class)
public class GroupAdd
{
    public void addGroup(String groupName, GroupType.Type groupType)
    {
        new SpGroup(groupName, groupType).create();
    }
    
    public ArrayList<SpGroup> getGroups()
    {
        return SpGroup.allGroups();
    }
    
    public boolean groupHasNoPrivilege(String groupName, GroupType.Type groupType)
    {
        return new SpGroup(groupName, groupType).getPrivileges().isEmpty();
    }
}
