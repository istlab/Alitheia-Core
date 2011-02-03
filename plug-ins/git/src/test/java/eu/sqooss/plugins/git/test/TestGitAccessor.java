package eu.sqooss.plugins.git.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.plugins.tds.git.GitRevision;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMNodeType;

public class TestGitAccessor extends TestGitSetup {
    
    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
       initTestRepo();
    }

    @Before
    public void setUp() throws AccessorException, URISyntaxException {
        getGitRepo();
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
        Revision r = git.newRevision("7660a188dfd0c5e52884790bebf5637d24f990d4");
        assertNotNull(r);
        assertEquals(r.getDate().getTime(), sdf.parse("Fri Apr 8 15:04:51 2005 -0700").getTime());
        assertTrue(git.isValidRevision(r));
        
        //now check a commit on a branch
        Revision r1 = git.newRevision("e2e5e98a40d6ed04b7acf791cc2243ff32923db3");
        assertNotNull(r);
        assertEquals(r1.getDate().getTime(), sdf.parse("Wed Apr 13 01:40:09 2005 -0700").getTime());
        assertTrue(git.isValidRevision(r1));
        
        //and a commit that creates a tag
        Revision r2 = git.newRevision("a3eb250f996bf5e12376ec88622c4ccaabf20ea8");
        assertNotNull(r);
        assertEquals(r2.getDate().getTime(), sdf.parse("Sun Jul 10 15:40:43 2005 -0700").getTime());
        assertTrue(r1.compareTo(r2) < 0);
        assertTrue(git.isValidRevision(r2));
        
        //Check invalid revision
        Revision r3 = git.newRevision("0");
        assertNull(r3);
    }
    
    @Test
    public void testNewRevisionDate() throws ParseException {
        Revision r = git.newRevision(sdf.parse("Sun Jul 10 15:40:43 2005 -0700"));
        assertNotNull(r);
        assertEquals(r.getUniqueId(), "a3eb250f996bf5e12376ec88622c4ccaabf20ea8");
        assertTrue(git.isValidRevision(r));
        assertEquals(r.getParentIds().size(), 1);
        Iterator<String> i = r.getParentIds().iterator();
        assertEquals(i.next(), "013aab8265a806c8d3c9b040485839091bca30f4");
    }

    @Test
    public void testGetParents() throws InvalidProjectRevisionException {
        Revision r = git.newRevision("e27a56a676a1524036add6067948c647d3093ce8");
        assertNotNull(r);
        assertEquals(r.getParentIds().size(), 2);
        Iterator<String> i = r.getParentIds().iterator();
        while (i.hasNext()) {
            String parentId = i.next();
            Revision parent = git.newRevision(parentId);
            assertNotNull(parent);
            assertTrue(parent.getDate().getTime() < r.getDate().getTime());
            assertTrue (parent.compareTo(r) < 0);
        }
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
        assertEquals(r.getUniqueId(),"e83c5163316f89bfbde7d9ab23ca2e25604af290");
        assertTrue(git.isValidRevision(r));
        assertEquals(11, r.getChangedPathsStatus().size());
    }

    @Test
    public void testGetPreviousRevision() throws InvalidProjectRevisionException {
        Revision r1 = git.newRevision("eb38c22f535c7c973f27b62845c5136c4be0ae49");
        Revision r2 = git.getPreviousRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "59c1e249808c6ba38194733fa00efddb9e0eb488");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
        
        //Check a commit with multiple parents (a merge point with the master
        //branch)
        r1 = git.newRevision("b51ad4314078298194d23d46e2b4473ffd32a88a");
        r2 = git.getPreviousRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "a4b7dbef4ef53f4fffbda0a6f5eada4c377e3fc5");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
    }

    @Test
    public void testGetNextRevision() throws InvalidProjectRevisionException {
        Revision r1 = git.newRevision("c747fc6facdbbde4386418cfe6ad7e231a1b4eaf");
        Revision r2 = git.getNextRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "e8871e88adca0637eb0299a41d85400beac928bd");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
        
        //Check a commit with multiple children (a merge point with the master
        //branch)
        r1 = git.newRevision("6683463ed6b2da9eed309c305806f9393d1ae728");
        r2 = git.getNextRevision(r1);
        assertNotNull(r2);
        assertEquals(r2.getUniqueId(), "7d60ad7cc948b1b9e1066a3e740c91651bdc7e8d");
        assertTrue(git.isValidRevision(r1));
        assertTrue(git.isValidRevision(r2));
    }

    @Test
    public void testIsValidRevision() {
        Revision r = git.newRevision("fa06d442c6c5113fcff9991f349157bdb0c4b989");
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
            public int compareTo(Revision o) {return 0;}
            public Set<String> getParentIds() {return null;}
			public int compare(Revision o1, Revision o2) {return 0;}
        };
        
        assertFalse(git.isValidRevision(r));
    }

    @Test
    public void testGetCommitLog() 
    throws InvalidProjectRevisionException, InvalidRepositoryException, ParseException {
        Revision r1 = git.newRevision("13e897e58072678cdae3ec1db51cc91110dc559d");
        Revision r2 = git.newRevision("95fd5bf82ae28da47dcbf8e6e4570e64d71dc532");
        
        CommitLog l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 4);
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
        r1 = git.newRevision("6683463ed6b2da9eed309c305806f9393d1ae728");
        r2 = git.newRevision("839a7a06f35bf8cd563a41d6db97f453ab108129");
        l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 19);

        //Check log entry validity and ascending order
        while (i.hasNext()) {
            Revision r = i.next();
            assertTrue(git.isValidRevision(r));
            assertTrue(r.getDate().getTime() > old);
            old = r.getDate().getTime();
        }
        
        //Commit sequence including tags and branches + path filter
        r1 = git.newRevision("d19fbc3c171aa71a79b2ff0b654e3064c91628b8");
        r2 = git.newRevision("6bd9b6822f7647cb3275cf151ca92c6d6e9423aa");
        
        l = git.getCommitLog("/Documentation", r1, r2);
        assertNotNull(l);
        assertEquals(l.size(), 2);
        Iterator<Revision> it = l.iterator();
        assertEquals(it.next().getDate().getTime(), sdf.parse("Sun Jan 7 19:23:49 2007 -0500").getTime());
        assertEquals(it.next().getUniqueId(), "6bd9b6822f7647cb3275cf151ca92c6d6e9423aa");

        //Check log entry validity and ascending order
        while (i.hasNext()) {
            Revision r = i.next();
            assertTrue(git.isValidRevision(r));
            assertTrue(r.getDate().getTime() > old);
            old = r.getDate().getTime();
        }

        //Commit sequence with null second argument, should return entry for specific commit
        r1 = git.newRevision("98327e5891471e7baceda5c6543a387f0dd21d3a");

        l = git.getCommitLog("", r1, null);
        assertNotNull(l);
        assertEquals(l.size(), 1);
        Revision r = l.iterator().next();
        assertEquals(r.getUniqueId(), "98327e5891471e7baceda5c6543a387f0dd21d3a");
        
        assertTrue(r.getChangedPaths().contains("/git-svn.perl"));

        //Get the full log
        r1 = git.newRevision(git.getFirstRevision().getUniqueId());
        r2 = git.newRevision(git.getHeadRevision().getUniqueId());
        l = git.getCommitLog("", r1, r2);
        assertNotNull(l);
    }

    @Test
    public void testGetNodeType() throws InvalidRepositoryException, MissingObjectException, 
    IncorrectObjectTypeException, CorruptObjectException, IOException, URISyntaxException {
        Revision r = git.newRevision("33a59fd07d8d75e58a5b1edbcc3c5798c98aa8bf");
        
        //Basic checks
        SCMNodeType t = git.getNodeType("/compat", r);
        assertNotNull(t);
        assertEquals(SCMNodeType.DIR, t);
        
        t = git.getNodeType("/contrib/colordiff/README", r);
        assertNotNull(t);
        assertEquals(SCMNodeType.FILE, t);
        
        t = git.getNodeType("/alitheia/core/test", r);
        assertNotNull(t);
        assertEquals(SCMNodeType.UNKNOWN, t);
        
        t = git.getNodeType("/", r);
        assertNotNull(t);
        assertEquals(SCMNodeType.DIR, t);
        
        //JGit bug specific test, to check whether new versions will fix it
        /*initTestRepo();
        r = git.newRevision("b18bca3b853dee6a7bc86f09921aa3b1ee3f3d7b");
        
        RevWalk rw = new RevWalk(local);
        ObjectId treeId = local.resolve("5df04c8b946ef9c1f31bf8e722a9262b512c1928");
        RevTree tree = rw.parseTree(treeId);
        
        final TreeWalk walk = new TreeWalk(local);
		walk.setRecursive(false);
		walk.addTree(tree);
		
		FileMode a = null, b = null;
		
		while (walk.next()) {
			String pathstr = walk.getPathString();
			if (pathstr.equals("working")) {
				a = walk.getFileMode(0);
				break;
			}
		}

		assertNotNull(a);
		assertEquals(a, FileMode.TREE);
		
		RevCommit c = rw.parseCommit(local.resolve("b18bca3b853dee6a7bc86f09921aa3b1ee3f3d7b"));
		TreeWalk tw = TreeWalk.forPath(local, "tests/files/working", c.getTree());
		b = tw.getFileMode(0);
		
		assertNotNull(b);
		assertEquals(b, FileMode.REGULAR_FILE);
		assertFalse(a.equals(b)); //This should fail when the correspoding bug in Jgit gets fixed*/
    }
    
    @Test
    public void testGetTags() {
    	Map<String, String> tags = git.allTags();
    	assertNotNull(tags);
    	assertEquals(tags.keySet().size(), 342);
    	assertTrue(tags.values().contains("v1.7.3-rc0"));
    	assertTrue(tags.values().contains("gitgui-0.7.0-rc1"));
    }

    @Test
    public void testGetCommitChidren() throws AccessorException, InvalidProjectRevisionException {
    	Revision normal = git.newRevision("15000d78996db926d18dd68e6f5f5770de09cad3");
    	String[] children = git.getCommitChidren(normal.getUniqueId());
    	assertEquals(children.length, 1);
    	Revision child = git.newRevision(children[0]);
    	assertNotNull(child);
    	assertTrue(child.getParentIds().contains(normal.getUniqueId()));
    	
    	Revision branch = git.newRevision("6683463ed6b2da9eed309c305806f9393d1ae728");
    	children = git.getCommitChidren(branch.getUniqueId());
    	assertEquals(children.length, 2);
    	Revision first = git.newRevision(children[0]);
    	assertNotNull(first);
    	assertTrue(first.getParentIds().contains(branch.getUniqueId()));
    	Revision second = git.newRevision(children[1]);
    	assertNotNull(second);
    	assertTrue(second.getParentIds().contains(branch.getUniqueId()));
    	assertTrue(first.compareTo(second) < 0);
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
