package eu.sqooss.impl.service.specs.users;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpGroup;
import eu.sqooss.impl.service.dsl.SpPrivilege;

@RunWith(ConcordionRunner.class)
public class FirstGroups
{
    public ArrayList<SpGroup> getGroups()
    {
        return SpGroup.allGroups();
    }
    
    public ArrayList<SpPrivilege> getAllPrivileges()
    {
        return SpPrivilege.getAllPrivileges();
    }
}
