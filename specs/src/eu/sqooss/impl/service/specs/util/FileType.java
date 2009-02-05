package eu.sqooss.impl.service.specs.util;

import java.util.Collection;
import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.service.fds.FileTypeMatcher;

@RunWith(ConcordionRunner.class)
public class FileType
{
    // Left over from the example
    public String status()
    {
        return "connected";
    }

    public int splitTest() {
    	return ":".split(":",-1).length;
    }
    
    public int splitTestEmpty() {
    	return "".split(":",-1).length;
    }
    
    public Collection<FileExtension> getFiles()
    {
        return new ArrayList<FileExtension>() {
       	    public static final long serialVersionUID = -1;
       	    {
       	    	// Just some regular filenames
                add(new FileExtension("foo.c"));
                add(new FileExtension("foo.h"));
                add(new FileExtension("foo.moose"));
                add(new FileExtension("foo.moose.c"));
                add(new FileExtension("foo.c.moose"));
                add(new FileExtension("foo.tar.gz"));
                // Unusual numbers of dots
                add(new FileExtension("foo..c"));
                add(new FileExtension("fooc"));
                // Just dots
                add(new FileExtension("."));
                add(new FileExtension(".."));
                add(new FileExtension("...c"));
                add(new FileExtension(".pl"));
                // Slashes
                add(new FileExtension("/"));
                add(new FileExtension("/."));
                add(new FileExtension("///"));
                add(new FileExtension("///./py"));
                // Misleading directory name
                add(new FileExtension("///.c/py"));
                add(new FileExtension("///./.py"));
                add(new FileExtension("///.el"));
                add(new FileExtension("///./foo.py"));
                add(new FileExtension("///foo.xml"));
                // Repeat with fewer empty path components
                add(new FileExtension("/foo/bar.pl"));
                add(new FileExtension("/foo/bar.py/baz.c"));
                add(new FileExtension("/foo/bar.py/"));
                // Check it's null-safe
                add(new FileExtension(null));
                // Look for different file types
                add(new FileExtension("FileList.java"));
                add(new FileExtension("FileList.xml"));
                add(new FileExtension("FileList.docbook"));
                add(new FileExtension("FileList.tex"));
                add(new FileExtension("FileList.xbm"));
                add(new FileExtension("FileList.png"));
                add(new FileExtension("FileList.po"));
                // And some .po files
                add(new FileExtension("/foo/en_GB.properties.c"));
                add(new FileExtension("/foo/en_GB.properties"));
                add(new FileExtension("/foo/en_GB.c.po"));
       	    }
        };
    }

    class FileExtension
    {
        public FileExtension(String name)
        {
        	if (null == name) {
        		this.name = "(null)";
        	} else {
        		this.name = name;
        	}
            this.extension = FileTypeMatcher.getFileExtension(name);
            FileTypeMatcher ftm = FileTypeMatcher.getInstance();
            FileTypeMatcher.FileType ft = ftm.getFileType(name);
            if (null == this.extension) {
	        this.extension = "(null)";
            }
            if (null == ft) {
	        this.type = "(null)";
            } else {
	        this.type = ft.toString();
           }
        }

        public String name;
        public String extension;
	public String type;
    }
}
