package eu.sqooss.test.service.scheduler;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import eu.sqooss.test.service.scheduler.SchedulerTests;

public class SchedulerTestSuite {

	public static void run() {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for eu.sqooss.impl.service.scheduler");
		// $JUnit-BEGIN$
		suite.addTestSuite(SchedulerTests.class);
		// $JUnit-END$
		return suite;
	}

}
