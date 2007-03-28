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

package eu.sqooss.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Formatter;

/**
 * The ExternalExecutor class executes an external
 * process based in its command line. The argument
 * is always a File class that contains a file or 
 * a directory.
 * 
 * For the command line concatenation we use the Formatter
 * class, that uses a printf like syntax. 
 * e.g. wc -l %s // for the word count invocation
 *
 * @see java.io.File
 * @see java.util.Formatter
 */
public class ExternalExecutor implements Executor {
    private String cmd;
    
    public ExternalExecutor(String cmd) {
	this.cmd = cmd;
    }

    public InputStream execute(File file) {
	Process p;
	try {
	    StringBuilder cmdLine = new StringBuilder();
	    Formatter f = new Formatter(cmdLine);
	    // TODO: fix the object cast here
	    f.format(cmd, file.toString());
	    p = Runtime.getRuntime().exec(cmdLine.toString());
	    return p.getInputStream();
	} catch (Exception e) {
	    // TODO: logging?
	    return null;
	}
    }
}
