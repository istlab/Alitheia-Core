package eu.sqooss.plugins.tds.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import eu.sqooss.service.logging.Logger;

@RunWith(MockitoJUnitRunner.class)
public class SVNCheckoutEditorTest {
	
	private SVNCheckoutEditor svnCheckoutEditor;
	
	@Mock
	private File localPath;
	@Mock
	private File mockedFile;
	
	@Mock
	private SVNDeltaProcessor svnDeltaProcessor;
	@Mock
	private Logger logger;
	
	private long targetRevision = 10l;
	private String path = "test-path";
	
	class TestableSVNCheckoutEditor extends SVNCheckoutEditor {
		public TestableSVNCheckoutEditor(long r, File p) {
			super(r, p);
		}

		@Override
		protected SVNDeltaProcessor createDeltaProcessor() {
			return svnDeltaProcessor;
		}
		
		@Override
		protected File createFile(File localPath, String repoFilePathName) {
			return mockedFile;
		}
	}
	
	@Before
	public void setUp() {
		SVNCheckoutEditor.logger = logger;
		SVNCheckoutEditor.filecount = 0;
	}
	
	@Test
	public void testConstructor() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
	}
	
	@Test
	public void testNormalisePathNull() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		String result = svnCheckoutEditor.normalisePath(path);
		
		assertEquals(path, result);
	}

	@Test
	public void testNormalisePathShort() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		String repoDir = "dir";
		svnCheckoutEditor.setRepoDir(repoDir);
		String path = "dir/path";
		String result = svnCheckoutEditor.normalisePath(path);
		
		assertEquals(result, "/path");
	}

	@Test
	public void testNormalisePathLong() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		String repoDir = "dir";
		svnCheckoutEditor.setRepoDir(repoDir);
		String path = "dir/interfering/more/etc/path";
		String result = svnCheckoutEditor.normalisePath(path);
		
		assertEquals(result, "/interfering/more/etc/path");
	}

	@Test
	public void testNormalisePathBackSlash() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		String repoDir = "dir";
		svnCheckoutEditor.setRepoDir(repoDir);
		String path = "dir\\interfering\\more\\etc\\path";
		String result = svnCheckoutEditor.normalisePath(path);
		
		assertEquals(result, "interfering\\more\\etc\\path");
	}

	@Test
	public void testNormalisePathDifferentStart() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		String repoDir = "dir";
		svnCheckoutEditor.setRepoDir(repoDir);
		String path = "other/ect/path";
		String result = svnCheckoutEditor.normalisePath(path);
		
		verify(logger).warn(anyString());
		assertEquals(result, path);
	}

	@Test
	public void testApplyTextDeltaNullPath() throws SVNException {
		String checksum = "checksum";
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, null);
		svnCheckoutEditor.applyTextDelta(path, checksum);
		
		verify(logger).error(anyString());
		assertEquals(SVNCheckoutEditor.filecount, 1);
	}

	@Test
	public void testApplyTextDeltaCorrectPath() throws SVNException {
		String checksum = "checksum";
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.applyTextDelta(path, checksum);
		
		assertEquals(SVNCheckoutEditor.filecount, 1);
	}

	@Test(expected = SVNException.class)
	public void testApplyTextDeltaException() throws SVNException {
		String checksum = "checksum";
		doThrow(new SVNException(mock(SVNErrorMessage.class))).when(svnDeltaProcessor).applyTextDelta(any(File.class), any(File.class), any(Boolean.class));
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.applyTextDelta(path, checksum);
		
		assertEquals(SVNCheckoutEditor.filecount, 0);
	}

	@Test
	public void testTextDeltaChunkSucceed() throws SVNException {
		OutputStream outputStream = mock(OutputStream.class);
		SVNDiffWindow svnDiffWindow = mock(SVNDiffWindow.class);
		
		when(svnDeltaProcessor.textDeltaChunk(svnDiffWindow)).thenReturn(outputStream);
			
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		OutputStream result = svnCheckoutEditor.textDeltaChunk(path, svnDiffWindow);
	}

	@Test
	public void testTextDeltaChunkFail() throws SVNException {
		SVNDiffWindow svnDiffWindow = mock(SVNDiffWindow.class);
		
		when(svnDeltaProcessor.textDeltaChunk(svnDiffWindow)).thenThrow(new NullPointerException());
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		OutputStream outputStream = svnCheckoutEditor.textDeltaChunk(path, svnDiffWindow);
		
		assertNull(outputStream);
	}

	@Test
	public void testTextDeltaEndSucceed() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.textDeltaEnd(path);
	}

	@Test
	public void testTextDeltaEndFail() {
		doThrow(new NullPointerException()).when(svnDeltaProcessor).textDeltaEnd();
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.textDeltaEnd(path);
	}

	@Test
	public void testDeleteEntryNullLocalPath() {
		long revision = 0l;
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, null);
		svnCheckoutEditor.deleteEntry(path, revision);
		
		verify(logger).error(anyString());
	}

	@Test
	public void testDeleteEntryCorrectLocalPath() {
		long revision = 0l;
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.deleteEntry(path, revision);
		
		verify(mockedFile).delete();
	}

	@Test
	public void testTargetRevisionOtherRevision() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.targetRevision(0l);
		
		verify(logger).warn(anyString());
	}
	
	@Test
	public void testTargetRevisionCorrectRevision() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.targetRevision(targetRevision);
		
		verify(logger, never()).warn(anyString());
	}
	
	/*
	 * The following methods are not yet implemented, they are either empty or only contain a logger.info call.
	 * Hence, these tests are here as a placeholder.
	 */
	@Test
	public void testOpenRoot() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.openRoot(targetRevision);
	}

	@Test
	public void testAddDir() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.addDir("Path", "source-path", targetRevision);
	}
	
	@Test
	public void testOpenDir() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.openDir("Path", targetRevision);
	}
	
	@Test
	public void testCloseDir() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.closeDir();
	}

	@Test
	public void testAbsentDir() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.absentDir(path);
	}

	@Test
	public void testAddFile() {
		String sourcePath = "source-path";
		long sourceRevision = 0l;
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.addFile(path, sourcePath, sourceRevision);
	}
	
	@Test
	public void testOpenFile() {
		long revision = 0l;
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.openFile(path, revision);
	}
	
	@Test
	public void testCloseFile() {
		String checksum = "checksum";
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.closeFile(path, checksum);
	}

	@Test
	public void testAbsentFile() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.absentFile(path);
	}

	@Test
	public void testClosedEdit() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		SVNCommitInfo svnCommitInfo = svnCheckoutEditor.closeEdit();
		
		assertNull(svnCommitInfo);
	}
	
	@Test
	public void testAbortEdit() {
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.abortEdit();
	}
	
	@Test
	public void testChangeDirProperty() {
		String name = "name";
		String value = "value";
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.changeDirProperty(name, value);
	}

	@Test
	public void testChangeDirPropertyWithSVNPropertyValue() throws SVNException {
		String arg0 = "arg0";
		SVNPropertyValue svnPropertyValue = mock(SVNPropertyValue.class);
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.changeDirProperty(arg0, svnPropertyValue);
	}

	@Test
	public void testChangeFileProperty() {
		String name = "name";
		String value = "value";
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.changeFileProperty(path, name, value);
	}

	@Test
	public void testChangeFilePropertyWithSVNPropertyValue() throws SVNException {
		String arg0 = "arg0";
		String arg1 = "arg1";
		SVNPropertyValue svnPropertyValue = mock(SVNPropertyValue.class);
		
		svnCheckoutEditor = new TestableSVNCheckoutEditor(targetRevision, localPath);
		svnCheckoutEditor.changeFileProperty(arg0, arg1, svnPropertyValue);
	}
}
