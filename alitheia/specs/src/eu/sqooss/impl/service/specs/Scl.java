package eu.sqooss.impl.service.specs;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;

@RunWith(ConcordionRunner.class)
public class Scl
{
	public void checkConnection() throws WSException
	{
		new WSSession("alitheia", "alitheia", "http://localhost:8088/sqooss/services/ws/");
	}
}
