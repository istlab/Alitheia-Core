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

import java.io.File;
import java.io.FilenameFilter;

/**
 * Folder class is for accessing the folder with the given path
 * 
 */

public class Folder {
	/**
	 * path of the folder
	 */
	private String path;

	/**
	 * The decorated file
	 */
	private File f;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            A path to the folder
	 */
	public Folder(String path) {
		f = null;
		this.path = path;

		if (path == null)
			throw new NullPointerException("Path is null");

		f = new File(path);

	}

	/**
	 * Checks if the folder exists and is directory
	 * 
	 * @return True iff folder exists and is directory, false in any other case
	 */
	public boolean exists() {
		return f.exists() && f.isDirectory();
	}

	/**
	 * Generates a list of files in folder, from Filename Filter
	 * 
	 * @param fnFilter
	 * @return A list of files in folder
	 */
	public String[] listFiles(FilenameFilter fnFilter) {
		return f.list(fnFilter);
	}

	/**
	 * Generates a list of files in folder
	 * 
	 * @return A list of files in folder
	 */
	public String[] listFiles() {
		return f.list();
	}

	/**
	 * Generates a list of files with the given extension in folder
	 * 
	 * @param ext
	 *            Extension of the files
	 * @return A list of files in folder with the given extension
	 */
	public String[] listFilesExt(String ext) {
		final String s = ext;
		return f.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(s);
			}
		});
	}

}
