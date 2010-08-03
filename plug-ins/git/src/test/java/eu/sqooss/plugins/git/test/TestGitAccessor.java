package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jgit.lib.Commit;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.GitIndex;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.lib.WorkDirCheckout;
import org.eclipse.jgit.transport.FetchResult;
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
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;

public class TestGitAccessor {

    public static Repository local;
    public static SimpleDateFormat sdf;
    
    public GitAccessor git;
    
    public static String url = "git://github.com/schacon/ruby-git.git";
    public static String localrepo = System.getProperty("user.dir") + "/test";
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        File repo = new File(localrepo, Constants.DOT_GIT);
        local = new Repository(repo);
        sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        
        if (repo.exists())
            return;
            
        local.create();
        
        RemoteConfig remoteConfig = new RemoteConfig(local.getConfig(), "master");
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
        FetchResult fetchResult = t.fetch(new TextProgressMonitor(), null);
        Ref head = fetchResult.getAdvertisedRef("HEAD");
        GitIndex index = new GitIndex(local);
        Commit mapCommit = local.mapCommit(head.getObjectId());
        Tree tree = mapCommit.getTree();
        WorkDirCheckout co = new WorkDirCheckout(local, new File(localrepo), index, tree);
        co.checkout();
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
    public void testNewRevisionString() throws InvalidProjectRevisionException, 
        ParseException {
        //Check commit resolution on a known commit
        Revision r = git.newRevision("93ea66104efe1bd5e3a80cbe097c6c9d88621a25");
        assertNotNull(r);
        assertEquals(r.getDate().getTime(), sdf.parse("Mon May 5 22:52:08 2008 +0800").getTime());
        
        //now check a commit on a branch
        Revision r1 = git.newRevision("257fd8db3e60fb655af3c42e224d0a9acaa3624e");
        assertNotNull(r);
        assertEquals(r1.getDate().getTime(), sdf.parse("Thu Feb 12 09:12:00 2009 -0800").getTime());
        
        //and a commit that creates a tag
        Revision r2 = git.newRevision("85fa6ec3a68b6ff8acfa69c59fbdede1385f63bb");
        assertNotNull(r);
        assertEquals(r2.getDate().getTime(), sdf.parse("Sun Aug 2 04:06:03 2009 -0400").getTime());
        assertTrue(r1.compareTo(r2) < 0);
    }
    
    @Test
    public void testNewRevisionDate() throws ParseException {
        Revision r = git.newRevision(sdf.parse("Thu Feb 12 09:12:00 2009 -0800"));
        assertNotNull(r);
        assertEquals(r.getUniqueId(), "257fd8db3e60fb655af3c42e224d0a9acaa3624e");
    }

    @Test
    public void testGetHeadRevision() throws InvalidRepositoryException {
        Revision r = git.getHeadRevision();
        assertNotNull(r);
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
