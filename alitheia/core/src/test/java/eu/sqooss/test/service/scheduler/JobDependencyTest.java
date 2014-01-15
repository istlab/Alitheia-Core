package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.JobDependency;
import eu.sqooss.test.EqualsHashCodeTest;

@RunWith(MockitoJUnitRunner.class)
public class JobDependencyTest extends EqualsHashCodeTest {
	
	private JobDependency dependency;
	@Mock private Job job1;
	@Mock private Job job2;

	@Override
	protected JobDependency makeInstance() {
		return new JobDependency(this.job1, this.job2);
	}

	@Override
	protected Object makeNotInstance() {
		return new JobDependency(mock(Job.class), this.job1);
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.dependency = this.makeInstance();
	}

	@Test
	public void testGetFrom() {
		assertEquals(this.job1, this.dependency.getFrom());
	}

	@Test
	public void testGetTo() {
		assertEquals(this.job2, this.dependency.getTo());
	}

	@Test
	public void testEqualsFalseFrom() {
		assertFalse(this.dependency.equals(new JobDependency(mock(Job.class), this.job2)));
	}
	
	@Test
	public void testEqualsFalseTo() {
		assertFalse(this.dependency.equals(new JobDependency(this.job1, mock(Job.class))));
	}
	
	@Test
	public void testToString() {
		assertEquals("<JobDependency[job1, job2]>", this.dependency.toString());
	}
}
