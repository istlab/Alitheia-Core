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

package eu.sqooss.impl.service.ohloh;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.updater.UpdaterBaseJob;
import eu.sqooss.service.util.FileUtils;
import eu.sqooss.service.util.XMLReader;
import eu.sqooss.service.util.Folder;

/**
 * Parses Ohloh account description files and stores them in the OhlohDeveloper
 * table, to be used for as an additional source of developer name-email matches
 * when updating our own Developer table.
 * 
 * @see <a href="https://www.ohloh.net/api/reference/account">Ohloh Account API</a>
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * @author Igor Levaja
 * @author Quinten Stokkink
 */
public class OhlohUpdater extends UpdaterBaseJob {

	/**
	 * The System property that stores the Ohloh path
	 */
	private static final String OHLOH_PATH = "eu.sqooss.updater.ohloh.path";

	/**
	 * The actual Ohloh path the Ohloh XML files are located in
	 */
	private String ohlohPath;

	/**
	 * Construct a new Ohloh updater.
	 */
	public OhlohUpdater() {
		ohlohPath = System.getProperty(OHLOH_PATH); 
	}

	@Override
	/**
	 * Updating developer information is a low priority process.
	 */
	public long priority() {
		return 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * For all the files in the Ohloh XML folder add the specified accounts
	 * in the file to our database (or update them if they already exist).
	 * 
	 * throws FileNotFoundException If the Ohloh XML folder could not be read
	 */
	protected void run() throws FileNotFoundException {
		Folder folder = openFolder();

		for (String file : folder.listFilesExt(".xml")) {
			dbs.startDBSession();

			Document document = docFromXMLFile(folder, file);
			if (document == null)
				continue;

			Element root = document.getRootElement();
			Iterator<Element> i = root.element("result").elementIterator("account");

			if (i == null || !i.hasNext()) {
				logger.warn("Cannot find <account> element in file " + document.getPath());
			} else {
				while (i.hasNext())
					addAccount(i.next());
			}

			dbs.commitDBSession();
		}
	}
	/**
	 * Adding a single account
	 * @param account Element that is account
	 */
	private void addAccount(Element account) {
		String id = getString(account.element("id"));
		String uname = getString(account.element("name"));
		String mailhash = getString(account.element("email_sha1"));

		OhlohDeveloper od = OhlohDeveloper.getByOhlohId(id);
		if (od != null) { //Exists, update fields to track updates
			od.setEmailHash(mailhash);
			od.setTimestamp(new Date());
			od.setUname(uname);
		} else {
			od = new OhlohDeveloper(uname, mailhash, id);
			dbs.addRecord(od);
		}
	}

	/**
	 * Construct a Document from an XML file in a certain folder.
	 * 
	 * @param folder The folder the file resides in
	 * @param file The file identifier relative to the folder
	 * @return The parsed Document DOM-tree corresponding to the file
	 */
	private Document docFromXMLFile(Folder folder, String file){
		String absFolder = folder.getAbsolutePath();
		String xmlFilePath = FileUtils.appendPath(absFolder, file);
		XMLReader xmlReader = new XMLReader(xmlFilePath);

		Document document = null;
		try {
			document = xmlReader.parse();
		} catch (FileNotFoundException fex) {
			logger.warn("Cannot read file " + absFolder + fex.toString());
		} catch (DocumentException e) {
			logger.warn("Cannot parse Ohloh file " + absFolder + " " + e.getMessage());
		}

		return document;
	}

	/**
	 * Private method for opening directory
	 * @return new Object
	 * @throws FileNotFoundException
	 */
	private Folder openFolder() throws FileNotFoundException {
		Folder folder = null;

		try {
			folder = new Folder(ohlohPath); 

			if (!folder.exists()) {
				logger.error("Path" + ohlohPath
						+ " does not exist or is not a directory");
				throw new FileNotFoundException("Cannot find Ohloh XML files");
			}
		}
		catch(NullPointerException n) {
			logger.error("Cannot continue without a valid path to look into");
			throw new FileNotFoundException("Cannot find Ohloh XML files");
		}

		return folder;
	}

	/**
	 * Return the String value of some Element
	 * 
	 * @param element The element to apply getStringValue() to
	 * @return element.getStringValue() if element is not null, "" otherwise
	 */
	private String getString(Element element) {
		if (element != null)
			return element.getStringValue();
		return "";
	}

	@Override
	/**
	 * Return this job
	 */
	public Job getJob() {
		return this;
	}
}
