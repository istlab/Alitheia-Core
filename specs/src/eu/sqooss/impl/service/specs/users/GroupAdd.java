package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;

@RunWith(ConcordionRunner.class)
public class GroupAdd
{
    public void addGroup(String groupName)
    {
        new SpGroup(groupName).create();
    }
    
    public ArrayList<SpGroup> getGroups()
    {
        return SpGroup.allGroups();
    }
    
    public boolean groupHasNoPrivilege(String groupName)
    {
        return new SpGroup(groupName).getPrivileges().isEmpty();
    }
}
