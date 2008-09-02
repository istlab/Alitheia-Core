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

    public Collection<FileExtension> getFiles()
    {
        return new ArrayList<FileExtension>() {
       	    public static final long serialVersionUID = -1;
       	    {
                add(new FileExtension("foo.c"));
                add(new FileExtension("foo.h"));
                add(new FileExtension("foo.moose"));
                add(new FileExtension("foo.tar.gz"));
       	    }
        };
    }

    class FileExtension
    {
        public FileExtension(String name)
        {
            this.name = name;
            this.extension = FileTypeMatcher.getFileExtension(name);
            FileTypeMatcher.FileType ft = FileTypeMatcher.getFileType(name);
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
