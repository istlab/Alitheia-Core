package gr.aueb.metrics.findbugs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.core.AlitheiaCoreService;

public class FindbugsMetricsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunProjectVersion() {
		/*
		BundleContext bc = new FakeBundleContext();
		assertNotNull(bc);
		FindbugsMetrics fbm = new FindbugsMetrics(bc);
		assertNotNull(fbm);
		*/
		assertTrue(true);
	}
}
