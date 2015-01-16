package eu.sqooss.impl.service.fds;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.sqooss.impl.service.fds.InMemoryCheckoutImpl;
import eu.sqooss.service.db.ProjectVersion;

public class InMemoryCheckoutImplTest {

	@Test
	public void getRootTest() {
		ProjectVersion pv = new ProjectVersion();
		InMemoryCheckoutImpl checkout = new InMemoryCheckoutImpl(pv);
		assertTrue(false);
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
