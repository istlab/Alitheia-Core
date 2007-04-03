package eu.sqooss.tool;

import org.apache.commons.cli.*;

/**
 * Class that contains common code for command line
 * parsing 
 */
public class CLI {
    protected static final String HEADER = "SQO-OSS metrics tool\n\n" +
                             "Copyright (c) Members of the SQO-OSS Collaboration, 2007\n" +
                             "All rights reserved by respective owners." +
                             "See http://www.sqo-oss.eu/ for details on the copyright holders."; 
    //
    protected Options options;
    protected HelpFormatter formatter;
    protected String[] args;
    
    protected CLI(String[] args) {
        this.options = new Options();
        this.formatter = new HelpFormatter();
        this.args = args;
    }
    
    protected CommandLine parseArgs() {
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse(options, args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            // TODO fix formatter message
            formatter.printHelp( HEADER, options );
            return null;
        }
    }
}
