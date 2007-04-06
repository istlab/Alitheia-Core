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

package eu.sqooss.plugin.cccc;

import java.io.*;

import eu.sqooss.plugin.ExternalExecutor;

/**
 * Implements the Executor that handles the CCCC tool execution.
 */
public class CCCCExecutor extends ExternalExecutor {

	public CCCCExecutor(String cmd) {
		super(cmd);
	}

	/* (non-Javadoc)
	 * @see eu.sqooss.plugin.ExternalExecutor#execute(java.io.File)
	 */
	@Override
	public InputStream execute(File file) {
		StringBuilder target = new StringBuilder();
		//target.append(cmd);
		String outputPath = System.getProperty("java.io.tmpdir");
		if ( !outputPath.endsWith(System.getProperty("file.separator")) )
			   outputPath += System.getProperty("file.separator");
		outputPath += "cccc";
		outputPath += System.getProperty("file.separator");
		target.append(" --outdir=");
		target.append(outputPath);
		
//		if(file.isDirectory()) {
//			for(File f: file.listFiles()) {
//				target.append(" " + f.toString());
//			}
//		}
//		else
		target.append(" " + file.toString());
		
		try {
			Process p = Runtime.getRuntime().exec(target.toString());
			p.waitFor();
			FileInputStream result = new FileInputStream(outputPath + "cccc.xml");
			return result;
		} catch(Exception e) {
			return null;
		}
	}
	
}
