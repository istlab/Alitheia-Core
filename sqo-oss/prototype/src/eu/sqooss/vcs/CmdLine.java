/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package eu.sqooss.vcs;

import org.apache.commons.cli.*;

public final class CmdLine {
    
    private static final String help = 
        "MVCS: Frontend to multiple version control " +
        "systems\nUsage:\n" +
        "mvcs -uri repo-uri -l local-path <action> or \n" +
        "mvcs -s server -l local-path -u user -p passwd -t repo-type <action>\n\n" +
        "repo-uri has the following syntax: \n" +
        "\t <svn,cvs>://user@server/path/to/repo?passwd=passwd\n" + 
        "Currently supported actions are (with parameters): checkout, " +
        "update, diff, getlog, curver";
    
    public static void main(String[] args) {
        Repository currentRepository = null;
        Options opts = new Options();
        HelpFormatter formatter = new HelpFormatter();

        opts.addOption("uri", "repository-location", true, "URI with repository connection details");
        opts.addOption("s", "server", true, "Remote repository server");
        opts.addOption("l", "local-path", true, "Local path for repository");
        opts.addOption("u", "user", true, "User name for repository access");
        opts.addOption("p", "password", true, "Password for repository access");
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
        // retrieve any left-over non-recognized options and arguments
        String[] leftOverArgs = null;
        leftOverArgs = cmdline.getArgs();
        
        if (cmdline.hasOption("uri")) {
			/* 1st usage */
			if (!cmdline.hasOption("l")) {
				System.err.println("No local path for repository specified");
				formatter.printHelp( help, opts );
        	} else {
        		try {
        			currentRepository = RepositoryFactory.getRepository(
        					cmdline.getOptionValue("l"),
        					cmdline.getOptionValue("uri"));
        		}catch (InvalidRepositoryException exp) {
			    	System.err.println("Couldn't get the specified repository.  " +
			    			"Reason: " + exp.getMessage());
            		formatter.printHelp( help, opts );
				}
			}
            System.err.println("No repository uri or server specified");
            formatter.printHelp( help, opts );
        } else if (cmdline.hasOption("s")) {
			/* 2nd usage */
			if (!cmdline.hasOption("l") && !cmdline.hasOption("t")) {
				System.err.println("No local path for repository or " +
						"repository type specified");
				formatter.printHelp( help, opts );
			} else {
				try {
					RepositoryType type = null;
				    if (cmdline.getOptionValue("t").equalsIgnoreCase("svn")) {
				    	type = RepositoryType.SVN;
				    } else if (cmdline.getOptionValue("t").equalsIgnoreCase("cvs")) {
				        type = RepositoryType.CVS;
				    }
				        currentRepository = RepositoryFactory.getRepository(
				        		cmdline.getOptionValue("l"),
				                cmdline.getOptionValue("s"),
				                cmdline.getOptionValue("u"),
				                cmdline.getOptionValue("p"), type);
				    } catch (InvalidRepositoryException exp) {
				    	System.err.println("Couldn't get the specified repository.  " +
				    			"Reason: " + exp.getMessage());
                		formatter.printHelp( help, opts );
					}
			}
        } else {
        	System.err.println("No repository uri or server specified");
            formatter.printHelp( help, opts );
		}
        
        // leftOverArgs[0] is "mvcs"
        if (leftOverArgs[1].equals("checkout"))
        {
        	
        } else if (leftOverArgs[1].equals("update")) {
        	
        } else if (leftOverArgs[1].equals("diff")) {
        	
        } else if (leftOverArgs[1].equals("getLog")) {
        	
        } else if (leftOverArgs[1].equals("curver")) {
        	
        } else {
        	System.err.println("No supported action specified");
        	formatter.printHelp( help, opts );
        }
        
        //TODO: Parse cmd line options for actions
    }
}
// vim: ts=4:sw=4:expandtab:
