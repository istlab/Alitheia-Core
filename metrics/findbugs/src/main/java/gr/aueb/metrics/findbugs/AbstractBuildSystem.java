package gr.aueb.metrics.findbugs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.logging.Logger;

public abstract class AbstractBuildSystem {
	
	protected BundleContext bc;
	protected Logger log;
	
	String PATH = "";
	
	protected AbstractBuildSystem(BundleContext bc)
	{
		this.bc = bc;
		log = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_METRIC);
	}
	
	public abstract List<File> compile(ProjectVersion pv, Pattern pom,
            File checkout, String out) throws IOException;
	public abstract List<File> getJars(File checkout);
	
	public int runReadOutput(Process pr, String name) throws IOException {
        OutReader outReader = new OutReader(pr.getInputStream(), name);
        outReader.start();
        int retVal = -1;
        while (retVal == -1) {
            try {
                retVal = pr.waitFor();
            } catch (Exception ignored) {}
        }
        return retVal;
    }
}
