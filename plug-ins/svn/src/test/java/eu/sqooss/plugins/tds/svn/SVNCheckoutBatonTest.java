package eu.sqooss.plugins.tds.svn;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNReporter;

import eu.sqooss.service.logging.Logger;

@RunWith(MockitoJUnitRunner.class)
public class SVNCheckoutBatonTest {
	
	private SVNCheckoutBaton svnCheckoutBaton;
	
	private long revisionCurrent = 10l;
	private long revisionSource = 5l;
	private long revisionTarget = 15l;
	
	@Mock
	private Logger logger;
	@Mock
	private ISVNReporter reporter;
	
	@Before
	public void setUp() {
		SVNCheckoutBaton.logger = logger;
	}

	@Test
	public void testConstructorOneRevision() {
		svnCheckoutBaton = new SVNCheckoutBaton(revisionCurrent);
	}
	
	@Test
	public void testConstructorSourceTargetRevision() {
		svnCheckoutBaton = new SVNCheckoutBaton(revisionSource, revisionTarget);
	}

	// The exact arguments for report.sertPath are tested here ONLY to guarantee identical behaviour. 
	// Hence, if this test fails, it must be updated to test the actual functionality.
	@Test
	public void testReportZeroSourceRevision() throws SVNException {
		svnCheckoutBaton = new SVNCheckoutBaton(0l, revisionTarget);
		svnCheckoutBaton.report(reporter);
		
		verify(reporter).setPath("", null, revisionTarget, true);
	}

	// The exact arguments for report.sertPath are tested here ONLY to guarantee identical behaviour. 
	// Hence, if this test fails, it must be updated to test the actual functionality.
	@Test
	public void testReportPositiveSourceRevision() throws SVNException {
		svnCheckoutBaton = new SVNCheckoutBaton(revisionSource, revisionTarget);
		svnCheckoutBaton.report(reporter);
		
		// Verify that start empty is set
		verify(reporter).setPath("", null, revisionSource, false);
	}
	
	@Test
	public void testReportException() throws SVNException {
		doThrow(new SVNException(mock(SVNErrorMessage.class))).when(reporter).
			setPath(anyString(), anyString(), anyLong(), anyBoolean());
		
		svnCheckoutBaton = new SVNCheckoutBaton(revisionSource, revisionTarget);
		svnCheckoutBaton.report(reporter);
	}
}
