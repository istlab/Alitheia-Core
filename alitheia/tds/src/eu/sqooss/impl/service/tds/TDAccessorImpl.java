/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.tds;

import java.io.File;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.SCMAccessor;

public class TDAccessorImpl extends NamedAccessorImpl implements TDAccessor {
    private String bts;
    private String mail;
    private String scm;
    private BTSAccessorImpl btsAccessor = null;
    private SCMAccessorImpl scmAccessor = null;
    private MailAccessorImpl mailAccessor = null;

    public static Logger logger;

    /**
     * Put a configuration value into this accessor. From the
     * full configuration key @p s (of the form project.subkey)
     * extract the subkey and store the value @p v (probably a
     * URL) for this accessor.
     */
    public void put( String s, String v ) {
        String subKey = s.substring(s.indexOf(".")+1);
        if ("scm".equals(subKey)) {
            scm = v;
        } else if ("bts".equals(subKey)) {
            bts = v;
        } else if ("mail".equals(subKey)) {
            mail = v;
        } else {
            logger.warning("Bad configuration key <" + s + "> in TDS config.");
        }
    }

    public TDAccessorImpl( long id, String name, String bts, String mail, String scm ) {
        super(id,name);
        this.bts = bts;
        this.mail = mail;
        this.scm = scm;
    }


    // Interface functions
    public BTSAccessor getBTSAccessor() {
        if (btsAccessor == null) {
            btsAccessor = new BTSAccessorImpl(getId(),getName());
        }
        return btsAccessor;
    }

    public MailAccessor getMailAccessor() {
        if (mailAccessor == null) {
            mailAccessor = new MailAccessorImpl( getId(),getName(),
                new File("/var/spool/mail") );
        }
        return mailAccessor;
    }

    public SCMAccessor getSCMAccessor() {
        if (scmAccessor == null) {
            scmAccessor = new SCMAccessorImpl( getId(),getName(), scm );
        }
        return scmAccessor;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

