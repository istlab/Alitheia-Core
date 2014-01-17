package gr.aueb.metrics.findbugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.util.FileUtils;

public class AntBuildSystem extends AbstractBuildSystem {
	
	public AntBuildSystem(ProjectVersion pv, Pattern buildPattern, File checkout, String out)
	{
		super(pv, buildPattern, checkout, out);
		
		if (System.getProperty("ant.path") != null)
            PATH = System.getProperty("ant.path");
        else
            PATH = "ant";
	}
	
	public List<File> compile(BundleContext bc) throws IOException
	{
		File antFile = getBuildFile(buildPattern, checkout);

        if (antFile == null) {
            log.warn(pv + " No build.xml found in checkout?!");
            return new ArrayList<File>();
        }

        ProcessBuilder ant = new ProcessBuilder(PATH);
        ant.directory(antFile.getParentFile());
        ant.redirectErrorStream(true);
        int retVal = OutReader.runReadOutput(ant.start(), out);

        if (retVal != 0) {
            log.warn("Build with ant failed. See file:" + out);
            return new ArrayList<File>();
        }

        return getJars(checkout);
	}
	
	public List<File> getJars(File checkout)
	{
		List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));
        List<File> result = new ArrayList<File>();
        //Exclude common maven artifacts which don't contain bytecode
        for(File f: jars) {
            // Exclude libraries commonly included in Maven repositories
            if (f.getAbsolutePath().contains("/lib/"))
                continue;
            result.add(f);
        }
        return result;
	}
}
