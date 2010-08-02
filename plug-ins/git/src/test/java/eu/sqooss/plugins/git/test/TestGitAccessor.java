package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.Revision;

public class TestGitAccessor {

    public static Repository local;
    public static RemoteConfig remoteConfig;
    
    public GitAccessor git;
    
    public static String url = "git://github.com/petdance/ack.git";
    public static String localrepo = System.getProperty("user.dir") + "/test";
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        File repo = new File(localrepo, Constants.DOT_GIT);
        local = new Repository(repo);
        if (!repo.exists())
            local.create();
        
        remoteConfig = new RemoteConfig(local.getConfig(), "test");
        remoteConfig.addURI(new URIish(url));
        
        final String dst = Constants.R_REMOTES + remoteConfig.getName();
        RefSpec wcrs = new RefSpec();
        wcrs = wcrs.setForceUpdate(true);
        wcrs = wcrs.setSourceDestination(Constants.R_HEADS + "*", dst + "/*");
        remoteConfig.addFetchRefSpec(wcrs);
        
        local.getConfig().setBoolean("core", null, "bare", false);
        remoteConfig.update(local.getConfig());

        local.getConfig().save();

        Transport t = Transport.open(local, remoteConfig);
        t.fetch(new TextProgressMonitor(), null);
    }
    
    @Before
    public void setUp() throws AccessorException, URISyntaxException {
        git = new GitAccessor();
        git.testInit(new URI("git-file://" + localrepo), "ack");
    }
    
    @Test
    public void testGetName() {
        assertEquals(git.getName(), "GitAccessor");
    }

    @Test
    public void testGetSupportedURLSchemes() {
        List<URI> schemes = git.getSupportedURLSchemes();
        assertEquals(schemes.size(), 1);
        assertEquals(schemes.get(0).getScheme(), "git-file");
    }
    
    @Test
    public void testNewRevisionString() throws InvalidProjectRevisionException {
        //Check commit resolution on a known commit
        Revision r = git.newRevision("1eaecb1e55d2e0e72fedd0499283345b6edfa097");
        assertNotNull(r);
        assertEquals(r.getDate().getTime(), 1204868094 * 1000L);
        
        //now check a commit on a branch
        Revision r1 = git.newRevision("0f5a145948f18e51095e1b635b7b55932fa3121d");
        assertNotNull(r);
        assertEquals(r1.getDate().getTime(), 1272470853 * 1000L);
        
        //and a commit that creates a tag
        Revision r2 = git.newRevision("f5556c48eb46100e1733f5a21b45a00f6c190061");
        assertNotNull(r);
        assertEquals(r2.getDate().getTime(), 1260553849 * 1000L);
        assertFalse(r1.compareTo(r2) < 0);
    }
    
    @Test
    public void testNewRevisionDate() {
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
