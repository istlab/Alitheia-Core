package gr.aueb.metrics.findbugs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.FileUtils;

public abstract class AbstractBuildSystem {
	
	protected Logger log;
	
	ProjectVersion pv;
	Pattern buildPattern;
	File checkout;
	String out = "";
	
	String PATH = "";
	
	protected AbstractBuildSystem(ProjectVersion pv, Pattern buildPattern, File checkout, String out)
	{
		this.pv = pv;
		this.buildPattern = buildPattern;
		this.checkout = checkout;
		this.out = out;
		
		log = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_METRIC);
	}
	
	public abstract List<File> compile(BundleContext bc) throws IOException;
	public abstract List<File> getJars(File checkout);
	
	public List<File> getDependencies(BundleContext bc) throws FileNotFoundException, IOException
	{
		return new ArrayList<File>();
	}
	
	public File getBuildFile(Pattern buildPattern, File checkout)
	{
		return FileUtils.findBreadthFirst(checkout, buildPattern);
	}
}
