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

import java.io.File;
import java.util.HashMap;

import eu.sqooss.db.Plugin;
import eu.sqooss.db.ProjectFile;
import eu.sqooss.plugin.DefaultPlugin;
import eu.sqooss.plugin.Executor;
import eu.sqooss.plugin.OutputParser;
import eu.sqooss.plugin.PluginException;

/**
 * Implements a plugin that handles the execution and parsing of the results
 * of the C/C++ Code Counter tool {@link http://sourceforge.net/projects/cccc}
 *
 */
public class CCCCPlugin extends DefaultPlugin {

	private OutputParser op; //we hold a local reference to the output parser
	//in order to provide it with the target (module name).
	
	public CCCCPlugin(Plugin p, Executor e, OutputParser op) {
		super(p, e, op);
		this.op = op;
	}
	
	@Override
	public HashMap<String, String> run(ProjectFile file) throws PluginException {

		if(op instanceof CCCCOutputParser) {
			//set the target of the parser
			File f = new File(file.getName());
			String target = f.getName().substring(f.getPath().length());
			int pos = target.lastIndexOf(".");
			if(pos > 0)
				target = target.substring(0, pos);
			((CCCCOutputParser)op).setTarget(target);
		}
		
		return super.run(file);
	}

	
	
}
