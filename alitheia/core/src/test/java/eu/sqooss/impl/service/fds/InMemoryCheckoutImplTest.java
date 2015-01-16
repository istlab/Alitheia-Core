package eu.sqooss.impl.service.fds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

public class InMemoryCheckoutImplTest {
	static BundleContext bc;
	static AlitheiaCore core;

	@BeforeClass
	public static void setUp() throws MalformedURLException {

		bc = mock(BundleContext.class);
		when(bc.getProperty("eu.sqooss.db")).thenReturn("H2");
		when(bc.getProperty("eu.sqooss.db.host")).thenReturn("localhost");
		when(bc.getProperty("eu.sqooss.db.schema")).thenReturn("alitheia;LOCK_MODE=3;MULTI_THREADED=true");
		when(bc.getProperty("eu.sqooss.db.user")).thenReturn("sa");
		when(bc.getProperty("eu.sqooss.db.passwd")).thenReturn("");
		when(bc.getProperty("eu.sqooss.db.conpool")).thenReturn("c3p0");

		core = new AlitheiaCore(bc);
	}

	@Test
	public void getRootTest() {
		ProjectVersion pv = mock(ProjectVersion.class);
		when(pv.getFiles()).thenReturn(null);
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);
		assertEquals("", checkout.getRoot().getName());
	}

	@Test
	public void getFileTest() {
		ProjectVersion pv = mock(ProjectVersion.class);

		when(pv.getFiles()).thenReturn(null);
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);

		assertNull(checkout.getFile("/folder//"));
	}

	@Test
	public void getFilesTest() {
		ProjectVersion pv = mock(ProjectVersion.class);
		when(pv.getFiles()).thenReturn(null);
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);

		assertEquals(new ArrayList<ProjectFile>(), checkout.getFiles());
	}

	@Test
	public void getProjectVersionTest() {
		ProjectVersion pv = mock(ProjectVersion.class);
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);
		assertEquals(pv, checkout.getProjectVersion());
	}

}
