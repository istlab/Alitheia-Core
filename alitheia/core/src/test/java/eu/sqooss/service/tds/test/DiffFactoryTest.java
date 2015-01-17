package eu.sqooss.service.tds.test;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;  
import static org.powermock.api.mockito.PowerMockito.*;
import eu.sqooss.impl.service.tds.diff.UnifiedDiffParser;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.DiffFactory;


@RunWith(MockitoJUnitRunner.class)
public class DiffFactoryTest {

	static Diff udpMock;
	
	@Test
	public void DiffCallParseDiff(){
		udpMock = Mockito.mock(UnifiedDiffParser.class);

		testDiffCallparseDiff();
		verify(udpMock).parseDiff();
	}
	
	
	public Diff testDiffCallparseDiff(){
        if (udpMock.parseDiff())
            return udpMock;
        
		return null;
	}
}
