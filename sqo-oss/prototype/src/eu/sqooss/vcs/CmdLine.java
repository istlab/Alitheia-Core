package eu.sqooss.vcs;

import org.apache.commons.cli.*;

public final class CmdLine {
    
    private static final String help = "MVCS: Frontend to multiple version control " +
    		"systems\nUsage:\n" +
    		"mvcs -uri repo-uri <action> or \n"+
    		"mvcs -s server -l repo-path -u user -p passwd -t repo-type <action>\n\n" +
    		"repo-uri has the following syntax: \n" +
    		"\t <svn,cvs>://user@server/path/to/repo?passwd=passwd\n" + 
    		"Currently supported actions are (with parameters): checkout, update, diff, getlog, curver";
    
    public static void main(String[] args) {
	
	Options opts = new Options();
	HelpFormatter formatter = new HelpFormatter();
	
	opts.addOption("uri", "repository-location", true, "URI with repository connection details");
	opts.addOption("s", "server", true, "Remote repository server");
	opts.addOption("l", "repo-path", true, "Repository path on repository server");
	opts.addOption("u", "user", true, "User name for repository access");
	opts.addOption("p", "password", true, "User name for repository access");
	opts.addOption("t", "repo-type", true, "Repository type. Currently, one of cvs, svn");
	opts.addOption("h", "help", true, "Print this help message");
	
	CommandLine cmdline = null;
	CommandLineParser parser = new GnuParser();
	try {
	    // parse the command line arguments
	    cmdline = parser.parse(opts, args);
	} catch (ParseException exp) {
	    System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	    formatter.printHelp( help, opts );
	}
	
	if (!cmdline.hasOption("uri") && !cmdline.hasOption("s")) {
	    System.err.println("No repository uri or server specified");
	    formatter.printHelp( help, opts );
	}
	
	//TODO: Call RepositoryFactory with necessary parameters
	
	//TODO: Parse cmd line options for actions

    }
}
