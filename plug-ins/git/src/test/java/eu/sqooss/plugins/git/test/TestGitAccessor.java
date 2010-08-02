package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.NullProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.junit.Before;
import org.junit.Test;

public class TestGitAccessor {

    private Repository local;
    private RemoteConfig remoteConfig;
    
    public TestGitAccessor() throws IOException, URISyntaxException {
        File repo = new File("test", Constants.DOT_GIT);
        local = new Repository(repo);
        if (!repo.exists())
            local.create();
        
        remoteConfig = new RemoteConfig(local.getConfig(), "test");
        remoteConfig.addURI(new URIish("git://github.com/petdance/ack.git"));
        
        final String dst = Constants.R_REMOTES + remoteConfig.getName();
        RefSpec wcrs = new RefSpec();
        wcrs = wcrs.setForceUpdate(true);
        wcrs = wcrs.setSourceDestination(Constants.R_HEADS + "*", dst + "/*");
        remoteConfig.addFetchRefSpec(wcrs);
        
        local.getConfig().setBoolean("core", null, "bare", false);
        remoteConfig.update(local.getConfig());

        local.getConfig().save();

        Transport t = Transport.open(local, remoteConfig);
        
        t.fetch(NullProgressMonitor.INSTANCE, null);
        
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testGetName() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSupportedURLSchemes() {
        fail("Not yet implemented");
    }

    @Test
    public void testInit() {
        fail("Not yet implemented");
    }

    @Test
    public void testNewRevisionDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testNewRevisionString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetHeadRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFirstRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNextRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPreviousRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsValidRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCheckout() {
        fail("Not yet implemented");
    }

    @Test
    public void testUpdateCheckout() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFileStringRevisionFile() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFileStringRevisionOutputStream() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCommitLogRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCommitLogRevisionRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCommitLogStringRevisionRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetCommitLogStringRevision() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetDiff() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetChange() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNodeType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSubProjectPath() {
        fail("Not yet implemented");
    }

    @Test
    public void testListDirectory() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNodeChangeType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNodeAnnotations() {
        fail("Not yet implemented");
    }
}
