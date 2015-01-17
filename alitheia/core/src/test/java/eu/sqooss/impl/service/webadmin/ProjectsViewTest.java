package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.cluster.ClusterNodeServiceImpl;
import eu.sqooss.impl.service.pa.PAServiceImpl;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.PluginAdmin;
import eu.sqooss.service.abstractmetric.PluginInfo;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

@PrepareForTest(ClusterNode.class)
@RunWith(PowerMockRunner.class)
public class ProjectsViewTest {
	BundleContext bc;
	AlitheiaCore core;

	public void setUp() {
		bc = mock(BundleContext.class);
		when(bc.getProperty("eu.sqooss.db")).thenReturn("H2");
		when(bc.getProperty("eu.sqooss.db.host")).thenReturn("localhost");
		when(bc.getProperty("eu.sqooss.db.schema")).thenReturn(
				"alitheia;LOCK_MODE=3;MULTI_THREADED=true");
		when(bc.getProperty("eu.sqooss.db.user")).thenReturn("sa");
		when(bc.getProperty("eu.sqooss.db.passwd")).thenReturn("");
		when(bc.getProperty("eu.sqooss.db.conpool")).thenReturn("c3p0");

		core = new AlitheiaCore(bc);

	}

	@Test
	public void createFormTest() throws Exception {
		setUp();
		VelocityContext vc = mock(VelocityContext.class);
		new ProjectsView(bc, vc);
		
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getLocale()).thenReturn(new Locale("en_US"));

		ClusterNode clusternode = mock(ClusterNode.class);
		Set<StoredProject> projects = new HashSet<StoredProject>();
		when(clusternode.getProjects()).thenReturn(projects);
		PowerMockito.mockStatic(ClusterNode.class);
		PowerMockito.when(ClusterNode.thisNode()).thenReturn(clusternode);

		ClusterNodeService cnservice = mock(ClusterNodeService.class);
		when(cnservice.getClusterNodeName()).thenReturn("name");
		AbstractView.sobjClusterNode = cnservice;
		
		String output = ProjectsView.render(req);
		output = output.replaceAll("[\\n\\t]", "");
		output = output.replace("  ", "");
		
		String expected = "<form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table><thead><tr class=\"head\"><td class='head'style='width: 10%;'>Project Id</td><td class='head' style='width: 35%;'>Project Name</td><td class='head' style='width: 15%;'>Last Version</td><td class='head' style='width: 15%;'>Last Email</td><td class='head' style='width: 15%;'>Last Bug</td><td class='head' style='width: 10%;'>Evaluated</td><td class='head' style='width: 10%;'>Host</td></tr></thead><tr><td colspan=\"6\" class=\"noattr\">No projects found.</td></tr><tr class=\"subhead\"><td>View</td><td colspan=\"6\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location='/projects';\"></td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add project\" onclick=\"javascript:document.getElementById('reqAction').value='reqAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Delete project\" onclick=\"javascript:document.getElementById('reqAction').value='reqRemProject';document.projects.submit();\" disabled></td></tr><tr class='subhead'><td>Update</td><td colspan='4'><input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdate';document.projects.submit();\" disabled><input type=\"button\" class=\"install\" value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAll';document.projects.submit();\" disabled></td><td colspan=\"2\" align=\"right\"><input type=\"button\" class=\"install\" value=\"Update all on name\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAllOnNode';document.projects.submit();\"></td></tr></tbody></table></fieldset><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value=''><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form>";
		assertEquals(expected, output);
	}
}
