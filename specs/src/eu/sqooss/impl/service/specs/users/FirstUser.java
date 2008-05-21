package eu.sqooss.impl.service.specs.users;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.ws.client.datatypes.WSUser;
import eu.sqooss.ws.client.datatypes.WSUserGroup;

@RunWith(ConcordionRunner.class)
public class FirstUser
{
	WSUser user;
	
	public String openSession(String userName, String password) throws WSException
	{
		user = new WSSession(userName, password, "http://localhost:8088/sqooss/services/ws/").getUser();
		return "opened";
	}
	
	public boolean userIsInGroup(String groupName)
	{
		WSUserGroup[] groups = user.getGroups();
		
		for (WSUserGroup group : groups)
		{
			if (group.getDescription().equals(groupName))
			{
				return true;
			}
		}
		
		return false;
	}
}
