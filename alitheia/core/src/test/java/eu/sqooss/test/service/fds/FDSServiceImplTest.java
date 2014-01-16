package eu.sqooss.test.service.fds;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import eu.sqooss.impl.service.fds.FDSServiceImpl;
import eu.sqooss.impl.service.fds.TimelineFactory;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.test.testutils.TestUtils;

import static org.mockito.Mockito.*;

public class FDSServiceImplTest 
{

	/*Ignored because the function that is used for testing (the only public method 
	 * with a TDSProvider.get() currently either returns false if its first parameter
	 * is not null or a NullPointerException is it is (due to a later cimpl.lock()
	 * invocation. 	
	 */
	@Ignore
	@Test
	public void testDBInjection() 
	{
		TDSService tds = mock(TDSService.class);
		FDSServiceImpl fds = new FDSServiceImpl(TestUtils.provide(tds), null, null, null);
		
		OnDiskCheckout odc = mock(OnDiskCheckout.class);
		SCMAccessor scm = mock(SCMAccessor.class);
		
		when(tds.getAccessor(any(Long.class))).thenReturn((ProjectAccessor) scm);
		
		try {
			fds.updateCheckout(odc, null);
		} catch (CheckoutException e) {
			fail("Test failed: " + e.getMessage());
		}
		
		verify(tds).getAccessor(any(Long.class));
	}
	
	@Test
	public void testTimelineFactoryInjection()
	{
		TimelineFactory tlf = mock(TimelineFactory.class);
		FDSServiceImpl fds = new FDSServiceImpl(null, null, null, tlf);
		
		fds.getTimeline(null);
		
		verify(tlf).create(null);
	}
	

}
