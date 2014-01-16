package eu.sqooss.plugins.tds.svn;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import eu.sqooss.service.tds.Diff;


@RunWith(MockitoJUnitRunner.class)
public class SVNDiffStatusHandlerTest {

	@Mock
	private Diff diff;
	
	private SVNDiffStatusHandler svnDiffStatusHandler;
	private SVNDiffStatus svnDiffStatus;
	
	@Before
	public void setUp() {
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
	}
	
	@Test
	public void testHandleDiffStatusNullDiff() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(null);
		svnDiffStatusHandler.handleDiffStatus(null);
		
		verify(svnDiffStatus, never()).getModificationType();
	}
	
	@Test
	public void testHandleDiffStatusNotFileKind() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		when(svnDiffStatus.getKind()).thenReturn(SVNNodeKind.NONE);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
		svnDiffStatusHandler.handleDiffStatus(svnDiffStatus);
		
		verify(svnDiffStatus, never()).getModificationType();
	}

	@Test
	public void testHandleDiffStatusStatusTypeChanged() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		when(svnDiffStatus.getKind()).thenReturn(SVNNodeKind.FILE);
		
		when(svnDiffStatus.getModificationType()).thenReturn(SVNStatusType.CHANGED);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
		svnDiffStatusHandler.handleDiffStatus(svnDiffStatus);
		
		// TODO: When behaviour of this branch is implemented, extends this test here.
	}
	
	@Test
	public void testHandleDiffStatusStatusTypeMerged() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		when(svnDiffStatus.getKind()).thenReturn(SVNNodeKind.FILE);
		
		when(svnDiffStatus.getModificationType()).thenReturn(SVNStatusType.MERGED);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
		svnDiffStatusHandler.handleDiffStatus(svnDiffStatus);
		
		// TODO: When behaviour of this branch is implemented, extends this test here.
	}
	
	@Test
	public void testHandleDiffStatusStatusTypeUnChanged() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		when(svnDiffStatus.getKind()).thenReturn(SVNNodeKind.FILE);
		
		when(svnDiffStatus.getModificationType()).thenReturn(SVNStatusType.UNCHANGED);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
		svnDiffStatusHandler.handleDiffStatus(svnDiffStatus);
		
		// TODO: When behaviour of this branch is implemented, extends this test here.
	}
	
	@Test
	public void testHandleDiffStatusStatusTypeOther() {
		svnDiffStatus = mock(SVNDiffStatus.class);
		when(svnDiffStatus.getKind()).thenReturn(SVNNodeKind.FILE);
		
		when(svnDiffStatus.getModificationType()).thenReturn(SVNStatusType.UNKNOWN);
		
		svnDiffStatusHandler = new SVNDiffStatusHandler(diff);
		svnDiffStatusHandler.handleDiffStatus(svnDiffStatus);
		
		// TODO: When behaviour of this branch is implemented, extends this test here.
	}
}
