/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.service.parser;

import org.w3c.dom.Document;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

/**
 * The <code>Parser</code> service provides a high level parsing
 * facility. It utilizes the CLMT parsing API.
 * 
 * Each <i>ProjectFile</i> is automatically translated into its
 * XML-based representation.  
 * 
 * The output format is called IXR (Intermediate XML Representation)
 * and is the output format of CLMT. 
 * 
 * More information can be found in 
 * http://istlab.dmst.aueb.gr/content/rel_pages/sense_software-page.html
 *
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public interface Parser extends AlitheiaCoreService {
	/**
	 * Parse a specified ProjectFile
	 * 
	 * @param l The pf's language module that will be used
	 * @param pf The ProjectFile that will be parsed
	 * @return The produced XML document
	 * @throws ParsingException
	 */
	public Document parse(Language l, ProjectFile pf) throws ParsingException;
	
	/**
	 * Parse all the files for a specified ProjectVersion
	 * 
	 * @param l The pf's language module that will be used
	 * @param pv The ProjectVersion that will be parsed
	 * @return The produced XML document
	 * @throws ParsingException
	 */	
	public Document[] parse(Language l, ProjectVersion pv) throws ParsingException;
}
