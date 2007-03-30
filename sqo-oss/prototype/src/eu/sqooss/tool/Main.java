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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.sqooss.tool;

import org.apache.commons.cli.*;

import eu.sqooss.plugin.PluginList;
import eu.sqooss.plugin.Plugin;

import eu.sqooss.util.ReadOnlyIterator;
import eu.sqooss.util.HibernateUtil;
import eu.sqooss.vcs.*;

/**
 * Main entry point class for the sqo-oss tool.
 * It provides access to the sqo-oss quality
 * measurement tool
 */
public class Main {
    private static final String help = "";
    
    public static void main(String[] args) {
    	Options opts = new Options();
        HelpFormatter formatter = new HelpFormatter();

        opts.addOption("m", "metrics", true, "Print the available metrics");
        opts.addOption("l", "list", true, "Print the available modules");
        opts.addOption("h", "help", true, "Print this help message");
        
        CommandLine cmdline = null;
        CommandLineParser parser = new GnuParser();
        try {
            // parse command line arguments
            cmdline = parser.parse(opts, args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            formatter.printHelp( help, opts );
        }
        
        // print the help for command line options
        if(cmdline.hasOption("h")) {
            formatter.printHelp( help, opts);
            return;
        }
        
        // print the metrics
        if(cmdline.hasOption("m")) {
            System.out.println("Print all available Modules:\n");
            PluginList pl = PluginList.getInstance();
            ReadOnlyIterator roi = pl.getPlugins();
            
            while(roi.hasNext()) {
        	System.out.println(((Plugin)roi.next()).toString());
            }
            return;
        }
        
        //HibernateUtil.getSessionFactory().getCurrentSession();
    }
    
    public void checkoutAndStore(String localPath, String serverPath, String username,
            String passwd /* more args needed */) throws InvalidRepositoryException {
    	SvnRepository repository = 
    		new SvnRepository(localPath, serverPath, username, passwd);
    	repository.checkout();
    	
    	//store
    }
    //the same but with a specified revision
    public void checkoutAndStore(String localPath, String serverPath, String username,
            String passwd, Revision rev /* more args needed */) throws InvalidRepositoryException {
    	SvnRepository repository = 
    		new SvnRepository(localPath, serverPath, username, passwd);
    	repository.checkout(rev);
    	
    	//store
    }
    
    public void updateAndStore(String localPath, String serverPath, String username,
            String passwd, Revision rev /* more args needed */) throws InvalidRepositoryException {
    	SvnRepository repository = 
    		new SvnRepository(localPath, serverPath, username, passwd);
    	repository.update(rev);
    	
    	//store
    }
    
}
