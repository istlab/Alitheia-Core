package eu.sqooss.service.fds;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.sqooss.service.fds.InMemoryDirectory;

public class InMemoryDirectoryTest {

	@Test
	public void getNameNoNameTest() {
		InMemoryDirectory dir = new InMemoryDirectory();
		assertEquals("", dir.getName());
	}

	@Test
	public void getNameTest1() {
		InMemoryDirectory dir = new InMemoryDirectory("the name");
		assertEquals("the name", dir.getName());
	}

	@Test
	public void getNameTest2() {
		InMemoryDirectory dir = new InMemoryDirectory(null, "a name");
		assertEquals("a name", dir.getName());
	}

	@Test
	public void getNameNullTest() {
		String name = null;
		InMemoryDirectory dir = new InMemoryDirectory(name);
		assertNull(dir.getName());
	}

	@Test
	public void getPathTest1() {
		InMemoryDirectory dir = new InMemoryDirectory("folder");
		assertEquals("/folder", dir.getPath());
	}

	@Test
	public void getPathTest2() {
		InMemoryDirectory parent = new InMemoryDirectory("folder");
		InMemoryDirectory dir = new InMemoryDirectory(parent, "subfolder");
		assertEquals("/folder/subfolder", dir.getPath());
	}

	@Test
	public void getParentDirectoryTest1() {
		InMemoryDirectory dir = new InMemoryDirectory("folder");
		assertEquals(null, dir.getParentDirectory());
	}

	@Test
	public void getParentDirectoryTest2() {
		InMemoryDirectory parent = new InMemoryDirectory("folder");
		InMemoryDirectory dir = new InMemoryDirectory(parent, "subfolder");
		assertEquals(parent, dir.getParentDirectory());
	}

	@Test
	public void getSubDirectoriesTest() {
		InMemoryDirectory dir = new InMemoryDirectory("folder");
		List<InMemoryDirectory> list = new ArrayList<InMemoryDirectory>();
		assertEquals(list, dir.getSubDirectories());
	}

	@Test
	public void getFileNames() {
		InMemoryDirectory dir = new InMemoryDirectory();
		List<String> list = new ArrayList<String>();
		assertEquals(list, dir.getFileNames());
	}

	@Test
	public void pathExistsTest() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		assertEquals(false, dir.pathExists("/folder"));
	}

	@Test
	public void addFileTest() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		dir.addFile("file.txt");
		assertTrue(dir.getFileNames().contains("file.txt"));
	}

	@Test
	public void deleteFileTest() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		dir.addFile("file.txt");
		dir.deleteFile("file.txt");
		assertFalse(dir.getFileNames().contains("file.txt"));
	}

	@Test
	public void getSubdirectoryByNameTest1() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		assertEquals(dir, dir.getSubdirectoryByName(null));
		assertEquals(dir, dir.getSubdirectoryByName(""));
	}

	@Test
	public void getSubdirectoryByNameTest2() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		assertEquals(null, dir.getSubdirectoryByName("folder"));
	}

	@Test
	public void createSubDirectoryTest1() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		dir.createSubDirectory("subfolder");
		assertEquals("subfolder", dir.getSubdirectoryByName("subfolder")
				.getName());
	}

	@Test
	public void createSubDirectoryTest2() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		dir.createSubDirectory("subfolder/subsubfolder");

		InMemoryDirectory subdir = dir.getSubdirectoryByName("subfolder");

		assertEquals(1, dir.getSubDirectories().size());
		assertEquals("subsubfolder",
				subdir.getSubdirectoryByName("subsubfolder").getName());
	}

	@Test
	public void toStringNoIndentationTest() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		assertEquals("name\n", dir.toString());
	}

	@Test
	public void toStringWithIndentationTest() {
		InMemoryDirectory dir = new InMemoryDirectory("name");
		dir.createSubDirectory("subfolder/subsubfolder");
		assertEquals("name\n subfolder\n  subsubfolder\n", dir.toString());
	}
}
