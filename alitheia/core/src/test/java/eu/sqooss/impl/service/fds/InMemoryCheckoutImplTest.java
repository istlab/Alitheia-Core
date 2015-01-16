package eu.sqooss.impl.service.fds;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import eu.sqooss.impl.service.fds.InMemoryCheckoutImpl;
import eu.sqooss.service.db.ProjectVersion;

public class InMemoryCheckoutImplTest {

	@Test
	public void getRootTest() {
		ProjectVersion pv = mock(ProjectVersion.class);
		when(pv.getFiles()).thenReturn(null);
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);
		assertEquals("", checkout.getRoot().getName());
	}

	@Test
	public void getFileTest() {
		assertTrue(false);
	}

	@Test
	public void getFilesTest() {
		assertTrue(false);
	}

	@Test
	public void getProjectVersionTest() {
		assertTrue(false);
	}
}
