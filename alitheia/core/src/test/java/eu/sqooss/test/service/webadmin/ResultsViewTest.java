package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.impl.service.webadmin.ResultsView;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.logging.Logger;

@RunWith(PowerMockRunner.class)
public class ResultsViewTest {
	@Mock private HttpServletRequest request;
	@Mock private Bundle bundle;
	@Mock private Logger sobjLogger;
	
	@Mock private BundleContext bc;
	@Mock private VelocityContext vc;
	@Mock private AlitheiaCore core;
	@Mock private AdminService as;
	@Mock private AdminAction aa;
	
	private ResultsView rView;
	
	private void initRView(){
		rView = new ResultsView(bc, vc);
		
		Whitebox.setInternalState(ResultsView.class, sobjLogger);
		Whitebox.setInternalState(ResultsView.class, core);
		
	}

	@Test
	public void testConstructor() {
		initRView();
		
		assertNotNull(rView);
	}
	
	@Test
	public void testExecStop() throws BundleException {
		initRView();
		
		when(request.getPathInfo()).thenReturn("/stop");
		when(bc.getBundle(0)).thenReturn(bundle);		
		
		rView.exec(request);

		verify(bundle, times(1)).stop();
	}

	@Test
	public void testExecRestart() {
		initRView();
		
		when(request.getPathInfo()).thenReturn("/restart");
		rView.exec(request);
		
		verify(vc, times(1)).put(any(String.class), any(String.class));
	}
	@Test
	public void testExecAddProject() {
		initRView();
		
		when(request.getPathInfo()).thenReturn("/addproject");	
		
		rView.exec(request);	
	}

	@Test
	public void testExecDirAddProject() {
		initRView();

		when(core.getAdminService()).thenReturn(as);		
		when(as.create(AddProject.MNEMONIC)).thenReturn(aa);		
		when(request.getPathInfo()).thenReturn("/diraddproject");		

		rView.exec(request);
		
		verify(aa, times(1)).addArg(any(String.class), any(String.class));
		verify(as, times(1)).execute(aa);
	}
	@Test
	public void testExecDirAddProjectFails() {
		initRView();
		
		when(core.getAdminService()).thenReturn(as);
		when(as.create(AddProject.MNEMONIC)).thenReturn(aa);		
		when(request.getPathInfo()).thenReturn("/diraddproject");
		when(aa.hasErrors()).thenReturn(true);

		rView.exec(request);
		
		verify(aa, times(1)).addArg(any(String.class), any(String.class));
		verify(aa, times(1)).errors();
	}
	
	


}
