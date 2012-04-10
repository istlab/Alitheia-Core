/*
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/*
** That copyright notice makes sense for code residing in the 
** main SQO-OSS repository. For the FindbugsMetrics plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the FindbugsMetrics
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string '.metrics.'
** because this is how Alitheia Core discovers the metric plug-ins. 
*/

/*
** Author: dimitro
** Date: 04/01/12
*/

package gr.aueb.metrics.findbugs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RunFindBugs {
	
	String projectName;
	String outputFileName;
	String packageToAnalyze;
	String command;
	boolean success = false;
	
	/**
	 * 
	 * @params projectName, packageToAnalyze
	 * 
	 * sets the name of the project, the name of the package to be
	 * analyzed, the name of the output XML file and creates the
	 * corresponding command to run FindBugs.
	 * 
	 */
	
	public RunFindBugs (String projectName, String packageToAnalyze){
		this.projectName = projectName;
		projectName = projectName.replaceAll(".jar", "");
		this.outputFileName = projectName+".xml";
		this.packageToAnalyze = packageToAnalyze;
		
		//please modify the path accordingly
		this.command = "/Users/dimitro/Documents/Phd/FindBugs+Alitheia/trunk/findbugs/bin/findbugs" +
				" -onlyAnalyze "+this.packageToAnalyze+".- -textui -xml "+this.projectName;
	}
	
	/**
	 * 
	 * runs FindBugs with the specified arguments and
	 * copies the output to the XML file.
	 * 
	 */
	
	public boolean findBugsRun (){	
		try {
			Runtime run = Runtime.getRuntime();
			Process p = run.exec(this.command);	
			InputStream stdin = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            File file = new File(this.outputFileName);
            
            if (!file.createNewFile()) {
            	System.out.println("File already exists.");
            	success = false;
            } else {
            	FileWriter fstream = new FileWriter(this.outputFileName);
            	BufferedWriter out = new BufferedWriter(fstream);
            	String line = null;
                while ( (line = br.readLine()) != null)
                	out.write(line+"\n");
            	out.close();
            	success = true;
            }			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
		
	}
	
	public static void main(String[] args) {	
		String jarName = "eu.sqooss.alitheia.core_1.0.jar";
		String packageName = "eu.sqooss";
		
		RunFindBugs run_test = new RunFindBugs(jarName, packageName);
		
		if (run_test.findBugsRun() != false)
			System.out.println("Successfully ran FindBugs");
		else
			System.out.println("EPIC FAIL");
		
		GetFindBugResults results = new GetFindBugResults(run_test.outputFileName);
		
		Map <String, Map <String, Integer>> testResults = 
				new HashMap <String, Map <String, Integer>> ();
		testResults = results.parseResults();
		
		Iterator iterator = testResults.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = testResults.get(key).toString();
			System.out.println(key + " " + value);
		}
	}

}
