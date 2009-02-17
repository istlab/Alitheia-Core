/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.updater;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.util.FileUtils;

/**
 * Parses Ohloh account description files and stores them in the OhlohDeveloper
 * table, to be used for as an additional source of developer name-email matches
 * when updating our own Developer table.
 * 
 * @see <a href="https://www.ohloh.net/api/reference/account">Ohloh Account API</a>
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class OhlohUpdater extends UpdaterBaseJob {
    
    private static final String OHLOH_PATH = "eu.sqooss.updater.ohloh.path";
    //private static final String OHLOH_PATH = "eu.sqooss.updater.ohloh.path";
    
    private String ohlohPath;
    
    public OhlohUpdater() {
        ohlohPath = System.getProperty(OHLOH_PATH); 
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    protected void run() throws Exception {
        File f = null;
        try {
            if (ohlohPath == null) {
                logger.error("Cannot continue without a valid path to look into");
                throw new FileNotFoundException("Cannot find Ohloh XML files");
            }

            f = new File(ohlohPath);
            if (!f.exists() || !f.isDirectory()) {
                logger.error("Path" + ohlohPath
                        + " does not exist or is not a directory");
                throw new FileNotFoundException("Cannot find Ohloh XML files");
            }
        } finally {
            //updater.removeUpdater(p, t);
        }
        
        String[] files = f.list();
        
        
        for (String file : files) {
            dbs.startDBSession();
            
            SAXReader reader = new SAXReader(false);
            Document document = null;
            
            //Dummy entity resolver to avoid downloading the bugzilla DTD from 
            //the web on parsing a bug
            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    InputStream in = new ByteArrayInputStream("".getBytes());
                    return new InputSource(in);
                }
            };
            
            reader.setValidation(false);
            reader.setEntityResolver(resolver);
            reader.setIncludeExternalDTDDeclarations(false);
            reader.setIncludeInternalDTDDeclarations(false);
            reader.setStripWhitespaceText(true);
            
            //Parse the file
            try {
                document = reader.read(new FileReader(
                        FileUtils.appendPath(f.getAbsolutePath(), file)));
            } catch (FileNotFoundException fex) {
                logger.warn("Cannot read file " + f.getAbsolutePath() + 
                        fex.toString());
                continue;
            } catch (DocumentException e) {
                logger.warn("Cannot parse Ohloh file " + f.getAbsolutePath() 
                        + " " + e.getMessage());
                continue;
            }
            
            Element result = (Element) document.getRootElement().elementIterator("result");
            
            if (result == null) {
                logger.warn("Cannot find <result> element in file " + document.getPath());
            }
            
            //result.get
            
            dbs.commitDBSession();
        }
    }
    
    private String getString(Element element) {
        if (element != null) {
            return element.getStringValue();
        } else {
            return "";
        }
    }

    @Override
    public Job getJob() {
        return this;
    }
}
