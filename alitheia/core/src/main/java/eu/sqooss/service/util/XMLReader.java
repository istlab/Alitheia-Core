/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * XMLReader is a utility class for reading XML files.
 * It uses a SAXReader back-end to facilitate turning
 * XML files into Document DOM trees. 
 */
public class XMLReader {

	/**
	 * The decorated (xml reader) SAXReader
	 */
	private SAXReader reader;
	
	/**
	 * The file we will be reading from
	 */
	private File file;
	
	/**
	 * Set up an XMLReader using a file path
	 * 
	 * @param path A path to the file to be parsed
	 */
	public XMLReader(String path){
		file = new File(path);
		initSAXReader();
	}
	
	/**
	 * Set up an XMLReader for a certain file
	 * 
	 * @param f The file to be parsed
	 */
	public XMLReader(File f){
		file = f;
		initSAXReader();
	}
	
	/**
	 * Initialize the SAXReader back-end.
	 * It uses a dummy entity resolver to avoid downloading 
	 * the bugzilla DTD from the web on parsing a bug.
	 */
	private void initSAXReader(){
		reader = new SAXReader(false);
		reader.setValidation(false);
        reader.setEntityResolver(createDummyResolver());
        reader.setIncludeExternalDTDDeclarations(false);
        reader.setIncludeInternalDTDDeclarations(false);
        reader.setStripWhitespaceText(true);
	}
	
	/**
	 * Create a dummy entity resolver that does not connect
	 * to bugzilla DTD.
	 * 
	 * @return An EntityResolver that does nothing
	 */
	private EntityResolver createDummyResolver(){
		return new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                InputStream in = new ByteArrayInputStream("".getBytes());
                return new InputSource(in);
            }
        };
	}
	
	/**
	 * Get the File that will be parsed.
	 * 
	 * @return The file to be parsed
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Change the file to be parsed.
	 * 
	 * @param file The file that will be parsed instead
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Parse the previously set file.
	 * 
	 * @return The Document DOM-tree representing the file
	 * @throws FileNotFoundException If the file could not be read (for any reason)
	 * @throws DocumentException If the XML syntax is incorrect
	 */
	public Document parse() throws FileNotFoundException, DocumentException{
		if (file == null)
			throw new FileNotFoundException("Path is null");
		
		FileReader fr = new FileReader(file);
		return reader.read(fr);
	}
}
