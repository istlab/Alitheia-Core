package eu.sqooss.tool;

import org.apache.commons.cli.*;

/**
 * Class that contains common code for command line parsing
 */
public class CLI {
    public static final String HEADER = "SQO-OSS metrics tool\n\n"
        + "Copyright (c) Members of the SQO-OSS Collaboration, 2007\n"
        + "All rights reserved by respective owners.\n"
        + "See http://www.sqo-oss.eu/ for details on the copyright holders.\n";

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
            System.out.println(HEADER);
            formatter.printHelp(" ", options);
            return null;
        }
    }

    /**
     * Tries to ensure that a set of given options appears to the given cmd line
     * 
     * @param cmdLine
     *            The CommandLine to search into
     * @param options
     *            Space sererated list of options
     * @return True if all options present to the options spec string also
     *         appear to the given command line, false otherwise.
     */
    protected boolean ensureOptions(CommandLine cmdLine, String options) {
        String[] opts = options.split(" ");

        for (String s : opts) {
            if (!cmdLine.hasOption(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print an error message and exit with exit code 1
     * 
     * @param msg
     *            The message to be printed
     */
    protected void error(String msg, CommandLine pcli) {
        System.err.println("ERROR:" + msg);
        System.out.println(HEADER);
        formatter.printHelp("usage", options);
        System.exit(1);
    }

    protected void error(String msg) {
        System.err.println("ERROR: " + msg);
        System.exit(1);
    }

    protected void log(String msg) {
        System.err.println("INFO: " + msg);
    }

    /**
     * Gets the value of an option if it exists
     * 
     * @param cmdLine
     *            The command line to be parsed for the value of the option
     * @param option
     *            The option name
     * @return An empty string if the option is not found, its value otherwise
     */
    protected String getOptionValue(CommandLine cmdLine, String option) {
        String result = "";
        if (cmdLine.hasOption(option)) {
            result = cmdLine.getOptionValue(option).trim();
        }
        return result;
    }

    /**
     * Parses the arguments specific to a command
     * 
     * @param args
     */
    protected void parse(String[] args) {

    }
}
