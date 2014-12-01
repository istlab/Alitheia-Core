package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.RulesView;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;

@RunWith(PowerMockRunner.class)
public class RulesViewTest {
	@Mock private HttpServletRequest request;
	@Mock private Bundle bundle;
	
	@Mock private BundleContext bc;
	@Mock private VelocityContext vc;
	@Mock private AlitheiaCore core;
	@Mock private AdminService as;
	@Mock private AdminAction aa;
	
	private RulesView rView;
	
	private void initRView(){
		rView = new RulesView(bc, vc);
	}

	@Test
	public void testConstructor() {
		initRView();
		
		assertNotNull(rView);
	}
	
	@Test
	public void testExec() throws BundleException {
		initRView();
		
		rView.exec(request);
	}
}