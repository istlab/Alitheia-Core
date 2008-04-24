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

package eu.sqooss.impl.service.web.services.datatypes;

import eu.sqooss.service.abstractmetric.ResultEntry;

// TODO: Auto-generated Javadoc
/**
 * The Class WSResultEntry.
 */
public class WSResultEntry {
    /** The ResultEntry instance wrapped by this class */
    private ResultEntry result;

    /** Database Id of the metric that generated this result */
    private long metricID;

    /**
     * Instantiates a new WSResult entry, that wraps the ResultEntry
     * parameter.
     * 
     * @param result a ResultEntry instance
     */
    public WSResultEntry(ResultEntry result) {
        this.result = result;
    }

    /**
     * Gets the Id of the metric that generated this result.
     * 
     * @return the metric Id
     */
    public long getMetricID() {
        return metricID;
    }

    /**
     * Sets the Id of the metric that generated this result
     * 
     * @param metricID the metric Id
     */
    public void setMetricID(long metricID) {
        this.metricID = metricID;
    }

    /**
     * Gets the MIME type of the stored result.
     * 
     * @return The MIME type of the <code>ResultEntry</code>.
     */
    public String getMimeType() {
        return result.getMimeType();
    }

    /**
     * Gets the <code>byte[]</code> representation of the stored result.
     * 
     * @return The byte array representation of the <code>ResultEntry</code>.
     */
    public byte[] getByteArray() {
        return result.getByteArray();
    }

    /**
     * Gets the <code>int</code> value of the stored result.
     * 
     * @return The integer value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>int</code>
     */
    public int getInteger() throws IllegalStateException {
        return result.getInteger();
    }

    /**
     * Gets the <code>long</code> value of the stored result.
     * 
     * @return The long value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>long</code>
     */
    public long getLong() throws IllegalStateException {
        return result.getLong();
    }

    /**
     * Gets the <code>float</code> value of the stored result.
     * 
     * @return The float value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>float</code>
     */
    public float getFloat() throws IllegalStateException {
        return result.getFloat();
    }

    /**
     * Gets the <code>double</code> value of the stored result.
     * 
     * @return The double value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>double</code>
     */
    public double getDouble() throws IllegalStateException {
        return result.getDouble();
    }
    
    /**
     * Gets the <code>String</code> value of the stored result.
     * 
     * @return The string value of the <code>ResultEntry</code>
     * 
     * @throws IllegalStateException if not <code>String</code>
     */
    public String getString() throws IllegalStateException {
        return result.getString();
    }
    
    /**
     * Gets the object that internally stores the result's value.
     * 
     * @return The object that stores the <code>ResultEntry</code>'s value.
     */
    public Object getObject() {
        return result.getObject();
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
