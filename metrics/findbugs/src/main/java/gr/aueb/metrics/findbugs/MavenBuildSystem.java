package gr.aueb.metrics.findbugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.util.FileUtils;

public class MavenBuildSystem extends AbstractBuildSystem {
	
	public MavenBuildSystem(ProjectVersion pv, Pattern buildPattern, File checkout, String out)
	{
		super(pv, buildPattern, checkout, out);
		
		if (System.getProperty("mvn.path") != null)
            PATH = System.getProperty("mvn.path");
        else
            PATH = "mvn";
	}

	
	public List<File> compile(BundleContext bc) throws IOException
	{
		File pomFile = getBuildFile(buildPattern, checkout);

        if (pomFile == null) {
            log.warn(pv + " No pom.xml found in checkout?!");
            return new ArrayList<File>();
        }

        ProcessBuilder maven = new ProcessBuilder(PATH, "install", "-DskipTests=true");
        maven.directory(pomFile.getParentFile());
        maven.redirectErrorStream(true);
        int retVal = OutReader.runReadOutput(maven.start(), out);

        if (retVal != 0) {
            log.warn("Build with maven failed. See file:" + out);
            return new ArrayList<File>();
        }

        return getJars(checkout);
	}

	@Override
	public List<File> getDependencies(BundleContext bc) throws FileNotFoundException, IOException {
		
		List<File> deps = new ArrayList<File>();
		File pomFile = getBuildFile(buildPattern, checkout);
		
		// Copy the script that gathers the dependency from
        // the resource bundle
        File copyDepsScript = new File(pomFile.getParentFile(), "copy-dependencies");
        FileOutputStream fos = new FileOutputStream(copyDepsScript);
        InputStream in = bc.getBundle().getResource("copy-dependencies").openStream();

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) != -1) {
            fos.write(buff, 0, read);
        }

        copyDepsScript.setExecutable(true);
        fos.close();
        in.close();

        ProcessBuilder copyDeps = new ProcessBuilder("./copy-dependencies");
        copyDeps.directory(pomFile.getParentFile());
        copyDeps.redirectErrorStream(true);
        int retVal2 = OutReader.runReadOutput(copyDeps.start(), out);
        if (retVal2 == 0) {
            File allDeps = new File(checkout.getPath() + "/all-deps");
            if (allDeps.exists() && allDeps.isDirectory()) {
                deps = Arrays.asList(allDeps.listFiles());
            }
        }
        
		return deps;
	}
	
	public List<File> getJars(File checkout)
	{
		List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));
        List<File> result = new ArrayList<File>();
        //Exclude common maven artifacts which don't contain bytecode
        for(File f: jars) {
            if (f.getName().endsWith("-sources.jar"))
                continue;
            if (f.getName().endsWith("with-dependencies.jar"))
                continue;
            if (f.getName().endsWith("-javadoc.jar"))
                continue;
            result.add(f);
        }
        return result;
	}
	
}
