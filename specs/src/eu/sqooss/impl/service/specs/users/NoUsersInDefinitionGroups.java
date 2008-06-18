package eu.sqooss.impl.service.specs.users;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpUser;

@RunWith(ConcordionRunner.class)
public class NoUsersInDefinitionGroups
{
    public String groupType(String groupName)
    {
        return new SpGroup(groupName).type;
    }
    
    public void addUserToGroup(String userName, String groupName)
    {
        new SpUser(userName, userName, userName+"@sqo-oss.org").create();
        new SpGroup(groupName).addUser(userName);
    }

    public boolean groupIsEmpty(String groupName)
    {
        return new SpGroup(groupName).getUsers().isEmpty();
    }
}
