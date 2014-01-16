package eu.sqooss.test.service.pa;

import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.framework.ServiceEvent;

import static org.mockito.Mockito.*;

import eu.sqooss.impl.service.pa.PAServiceImpl;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.test.testutils.TestUtils;

public class PAServiceImplTest 
{
	@Test
	public void testSchedulerInjection() 
	{
		Scheduler sched = mock(Scheduler.class);
		try {
			doNothing().when(sched).enqueue(any(Job.class));
		} catch (SchedulerException e) {
			fail("Test failed: " + e.getMessage());
		}
		PAServiceImpl pa = new PAServiceImpl(null, TestUtils.provide(sched));
		pa.uninstallPlugin(new Long(1));
		
		try {
			verify(sched).enqueue(any(Job.class));
		} catch (SchedulerException e) {
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testDBInjection() 
	{
		DBService dbs = mock(DBService.class);
		
		PAServiceImpl pa = new PAServiceImpl(TestUtils.provide(dbs), null);
		
		ServiceEvent se = mock(ServiceEvent.class);
		pa.serviceChanged(se);
		
		verify(dbs).startDBSession();
		verify(dbs).commitDBSession();		
	}

}
