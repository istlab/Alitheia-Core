package eu.sqooss.plugins.git.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import eu.sqooss.plugins.tds.git.GitRevision;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
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
        assertTrue(git.isValidRevision(r));
        
        //now check a commit on a branch
        Revision r1 = git.newRevision("257fd8db3e60fb655af3c42e224d0a9acaa3624e");
        assertNotNull(r);
        assertEquals(r1.getDate().getTime(), sdf.parse("Thu Feb 12 09:12:00 2009 -0800").getTime());
        assertTrue(git.isValidRevision(r1));
        
        //and a commit that creates a tag
        Revision r2 = git.newRevision("85fa6ec3a68b6ff8acfa69c59fbdede1385f63bb");
        assertNotNull(r);
        assertEquals(r2.getDate().getTime(), sdf.parse("Sun Aug 2 04:06:03 2009 -0400").getTime());
        assertTrue(r1.compareTo(r2) < 0);
        assertTrue(git.isValidRevision(r2));
    }
    
    @Test
    public void testNewRevisionDate() throws ParseException {
        Revision r = git.newRevision(sdf.parse("Thu Feb 12 09:12:00 2009 -0800"));
        assertNotNull(r);
        assertEquals(r.getUniqueId(), "257fd8db3e60fb655af3c42e224d0a9acaa3624e");
        assertTrue(git.isValidRevision(r));
    }

    @Test
    public void testGetHeadRevision() throws InvalidRepositoryException {
        Revision r = git.getHeadRevision();
        assertNotNull(r);
        assertTrue(git.isValidRevision(r));
    }

    @Test
    public void testGetFirstRevision() throws InvalidRepositoryException {
        Revision r = git.getFirstRevision();
        assertNotNull(r);
        assertEquals(r.getUniqueId(),"f5baa11a1c82dc42ade5c291e9f061c13b66bc2f");
        assertTrue(git.isValidRevision(r));
    }

    @Test
    public void testGetPreviousRevision() throws InvalidProjectRevisionException {
        Revision r1 = git.newRevision("4de1494c84fd5a5078f594f7d26ed667b1bc80ee");
        Revision r2 = git.getPreviousRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "8131f47c9d1832a685e35cc2f838edf439f7af4c");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
        
        //Check a commit with multiple parents (a merge point with the master
        //branch)
        r1 = git.newRevision("0cb54345b34fab6616f153c884fdc385180404a5");
        r2 = git.getPreviousRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "9863034427dfd13aef0c19faa140a433542064ce");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
    }

    @Test
    public void testGetNextRevision() throws InvalidProjectRevisionException {
        Revision r1 = git.newRevision("4de1494c84fd5a5078f594f7d26ed667b1bc80ee");
        Revision r2 = git.getNextRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "b86cbd6205b7c8d7769f330f8ddcf31b160f3aea");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
        
        //Check a commit with multiple children (a merge point with the master
        //branch)
        r1 = git.newRevision("a51210199671cd4fcf2fcfa5ba286829a73aeb62");
        r2 = git.getNextRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "86b45edcfe0026370cdff0ecea83348b406b3d92");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
    }

    @Test
    public void testIsValidRevision() {
        Revision r = new GitRevision("a21", new Date(12));
        assertFalse(git.isValidRevision(r));
        
        r = git.newRevision("779f21b307e7c119a56700fb14f88ba63a2cccc2");
        assertTrue(git.isValidRevision(r));
        
        //Check a custom impemementation
        r = new Revision() {
            public Date getDate() {return null;}
            public String getUniqueId() {return null;}
            public String getAuthor() {return null;}
            public String getMessage() {return null;}
            public Set<String> getChangedPaths() {return null;}
            public Map<String, PathChangeType> getChangedPathsStatus() {return null;}
            public List<CommitCopyEntry> getCopyOperations() {return null;}
            public int compareTo(Revision o) throws InvalidProjectRevisionException {return 0;}
        };
        
        assertFalse(git.isValidRevision(r));
    }

    @Test
    public void testGetCommitLog() 
    throws InvalidProjectRevisionException, InvalidRepositoryException {
        Revision r1 = git.newRevision("b5d6b907b080992c2d0220eceb66f4ffa85207cd");
        Revision r2 = git.newRevision("3cb57d82c301e9b8a16f30f468401e3007845bb7");
        
        CommitLog l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 15);
        Iterator<Revision> i = l.iterator();
        long old = 0;
        //Check log entry validity and ascending date order
        while (i.hasNext()) {
            Revision r = i.next();
            assertTrue(git.isValidRevision(r));
            assertTrue(r.getDate().getTime() > old);
            old = r.getDate().getTime();
        }
        
        //Commit sequence including tags and branches
        r1 = git.newRevision("55a5e323d241cfbd5a59d9a440c506b24b4c255a");
        r2 = git.newRevision("ae106e2a3569e5ea874852c613ed060d8e232109");
        
        l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 12);

        //Check log entry validity and ascending order
        while (i.hasNext()) {
            Revision r = i.next();
            assertTrue(git.isValidRevision(r));
            assertTrue(r.getDate().getTime() > old);
            old = r.getDate().getTime();
        }
        
        //Commit sequence including tags and branches + path filter
        r1 = git.newRevision("55a5e323d241cfbd5a59d9a440c506b24b4c255a");
        r2 = git.newRevision("ae106e2a3569e5ea874852c613ed060d8e232109");
        
        l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 12);

        //Check log entry validity and ascending order
        while (i.hasNext()) {
            Revision r = i.next();
            assertTrue(git.isValidRevision(r));
            assertTrue(r.getDate().getTime() > old);
            old = r.getDate().getTime();
        }
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
