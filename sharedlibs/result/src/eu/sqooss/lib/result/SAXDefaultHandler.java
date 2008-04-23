/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.lib.result;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class has a package visibility. 
 */
class SAXDefaultHandler extends DefaultHandler {
    
    private String rowElemName;
    private String fieldElemName;
    private String mimeTypeElemName;
    private String valueElemName;
    private String mnemonicElemName;
    
    private String currentElemName;
    private StringBuffer currentMimeTypeElemVal;
    private StringBuffer currentValueElemVal;
    private StringBuffer currentMnemonicElemVal;
    
    private Result tableResult;
    private ArrayList<ResultEntry> currentRow;

    public SAXDefaultHandler(String rowElemName, String fieldElemName,
            String mimeTypeElemName, String valueElemName, 
            String mnemonicElemName) {
        this.rowElemName = rowElemName;
        this.fieldElemName = fieldElemName;
        this.mimeTypeElemName = mimeTypeElemName;
        this.valueElemName = valueElemName;
        this.mnemonicElemName = mnemonicElemName;
        
        currentMimeTypeElemVal = new StringBuffer();
        currentValueElemVal = new StringBuffer();
        currentMnemonicElemVal = new StringBuffer();
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        tableResult = new Result();
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        
        if (rowElemName.equals(name)) {
            currentRow = new ArrayList<ResultEntry>();
        }
        currentElemName = name;
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        
        if (mimeTypeElemName.equals(currentElemName)) {
            currentMimeTypeElemVal.append(new String(ch, start, length).trim());
        } else if (valueElemName.equals(currentElemName)) {
            currentValueElemVal.append(new String(ch, start, length).trim());
        } else if (mnemonicElemName.equals(currentElemName)) {
            currentMnemonicElemVal.append(new String(ch, start, length).trim());
        }
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        
        if (rowElemName.equals(name)) {
            tableResult.addResultRow(currentRow);
            currentRow = null;
        } else if (fieldElemName.equals(name)) {
            currentRow.add(ResultEntry.fromString(currentValueElemVal.toString(), 
                    currentMimeTypeElemVal.toString(),
                    currentMnemonicElemVal.toString()));
            currentValueElemVal.setLength(0);
            currentMimeTypeElemVal.setLength(0);
            currentMnemonicElemVal.setLength(0);
        }
    }

    /**
     * This method returns the last parsed WSResult or null if there isn't.
     * 
     * @return
     */
    public Result getParsedResult() {
        return tableResult;
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        super.error(e);
        throw e;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
