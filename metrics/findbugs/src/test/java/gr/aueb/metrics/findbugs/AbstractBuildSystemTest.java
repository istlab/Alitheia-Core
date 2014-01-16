package gr.aueb.metrics.findbugs;

import static org.junit.Assert.*;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.sqooss.service.db.ProjectVersion;

public class AbstractBuildSystemTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		ProjectVersion pv = new ProjectVersion();
		Pattern buildPattern = Pattern.compile("test");
		File checkout = new File("test");
		String out = "test";
		
		//MavenBuildSystem bs = new MavenBuildSystem(pv, buildPattern, checkout, out);
		
		//assertNotNull(bs);
		
		assertTrue(true);
	}

}
