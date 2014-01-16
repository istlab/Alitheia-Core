package eu.sqooss.test.service.updater;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.test.testutils.TestUtils;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UpdaterServiceImplTest 
{
	private TDSService tds;
	private DBService dbs;
	private UpdaterServiceImpl us;
	
	
	@Before
	public void startUp()
	{
		tds = mock(TDSService.class);
		dbs = mock(DBService.class);
		us = new UpdaterServiceImpl(TestUtils.provide(dbs),
				TestUtils.provide(tds), null, null);
		
		BundleContext bc = mock(BundleContext.class);
		Logger l = mock(Logger.class);
		
		us.setInitParams(bc, l);
		us.startUp();
	}
	
	@Test
	public void testTDSInjection() 
	{		
		StoredProject sp = mock(StoredProject.class);
		Long expected = new Long(1);
		when(sp.getId()).thenReturn(expected);
		
		ProjectAccessor pa = mock(ProjectAccessor.class);
		SCMAccessor scm = mock(SCMAccessor.class);
		BTSAccessor bts = mock(BTSAccessor.class);
		MailAccessor mail = mock(MailAccessor.class);
		
		when(tds.getAccessor(any(Long.class))).thenReturn(pa);
		try {
			when(pa.getSCMAccessor()).thenReturn(scm);
			when(pa.getBTSAccessor()).thenReturn(bts);
			when(pa.getMailAccessor()).thenReturn(mail);
		} catch(InvalidAccessorException e) {
			fail("Test failed: " + e.getMessage());
		}
		
		us.getUpdaters(sp);
		
		verify(tds).getAccessor(expected);
	}
}
