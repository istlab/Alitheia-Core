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

package eu.sqooss.scl;

import org.apache.axis2.AxisFault;

/**
 * Thrown when the connection can't be established to the SQO-OSS's web services service or
 * web services service throws an exception.
 */
public class WSException extends Exception {
    
    private static final long serialVersionUID = 2075296057326742881L;

    /**
     * @see java.lang.Exception#Exception()
     */
    public WSException() {
        super();
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message)
     */
    public WSException(String message) {
        super(message);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.Throwable cause)
     */
    public WSException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message, java.lang.Throwable cause)
     */
    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        Throwable lastCause = getCause();
        if (lastCause == null) {
            return super.getMessage();
        } else {
            Throwable tmpThrowable = lastCause; 
            while ((tmpThrowable != null) &&
                    (tmpThrowable instanceof AxisFault)) {
                lastCause = tmpThrowable;
                tmpThrowable = tmpThrowable.getCause();
            }
            return lastCause.getMessage();
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
