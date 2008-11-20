/*
 * Copyright 2008 - Organization for Free and Open Source Software,
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
package eu.sqooss.impl.service.parser;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.clmt.configuration.Properties;

import org.clmt.io.CLMTFile;

import org.clmt.languages.LanguageException;

import org.clmt.languages.c.LangC;
import org.clmt.languages.java.LangJava;

import org.w3c.dom.Document;

import eu.sqooss.service.logging.Logger;

import eu.sqooss.service.parser.Language;
import eu.sqooss.service.parser.Parser;
import eu.sqooss.service.parser.ParsingException;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

/**
 * Implementation of Parser interface
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @see eu.sqooss.servive.parser.Parser
 */
public class ParserImpl implements Parser {
	private Logger logger;
	
	public ParserImpl(Logger logger) { 
		this.logger = logger;
	}


	public Document parse(Language l, ProjectFile pf) throws ParsingException {
		Document d = null;
		
		if (l == Language.JAVA) {
			d = parseJava(pf);
		} else if (l == Language.C) {				
			d = parseC(pf);
		} else if(l == Language.CPLUPLUS) {
			throw new ParsingException("C++ is not supported yet!");
		}
		
		if(d == null) {
			logger.warn("Parser (" + l + "): cannot parse ProjectFile with id = " + pf.getId());
			
			return null;
		}
		
		return d;
	}


	
	public Document[] parse(Language l, ProjectVersion pv) throws ParsingException {
		List<Document> results = new ArrayList<Document>();
		Set<ProjectFile> pfs = pv.getFilesForVersion();
			
		for ( ProjectFile pf : pfs ) {
			Document d = parse(l, pf);
			
			if(d == null) { continue; }
			
			results.add(d);
		}
		
		if(results.size() == 0) {
			throw new ParsingException(
					"Parser (" + l + "): Parsing completely failed for ProjectVersion with id " + pv.getId());
		}
		
		return results.toArray(new Document[] {});
	}

	/**
	 * Internal parser method for Java 
	 */
	private Document parseJava(ProjectFile pf) {
		LangJava lj = new LangJava();
		
		try {
			return lj.toIXR(new ParserCLMTFile(pf), new Properties());
		} catch (LanguageException le) {
			logger.warn("Parser (JAVA): Parsing failed for file with id " + pf.getId());
			logger.warn("Reason: " + le.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Internal parser method for C
	 */
	private Document parseC(ProjectFile pf) {
		LangC lc = new LangC();

		try {
			return lc.toIXR(new ParserCLMTFile(pf), new Properties());
		} catch (LanguageException le) {
			logger.warn("Parser (C): Parsing failed for file with id " + pf.getId());
			logger.warn("Reason: " + le.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Parser Dummy implementation for CLMTFile
	 * Remember, the parser works only with CLMTFile(s). 
	 */
	class ParserCLMTFile extends CLMTFile {
		private ProjectFile pf;
		
		ParserCLMTFile(ProjectFile pf) {
			this.pf = pf;
		}

		public boolean delete() {
			return false;
		}

		public boolean exists() {
			return true;
		}

		public String getAbsolutePath() {
			return pf.getFileName();
		}

		public InputStream getInputStream() {
			// TODO: find how can i get the InputStream
			return null;
		}

		public String getName() {
			return pf.getName();
		}

		public OutputStream getOutputStream() {
			// Always return null
			return null;
		}

		public boolean isDirectory() {
			return false;
		}

		public boolean isFile() {
			return true;
		}

		public CLMTFile[] listFiles() {
			// Return an empty CLMTFile
			return new CLMTFile[] {};
		}

		public boolean mkdirs() {
			return false;
		}

		public CLMTFile newFile(String arg0) {
			// Return always null
			return null;
		}		
	}
}
