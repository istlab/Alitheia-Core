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

package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Properties;

import eu.sqooss.impl.service.messaging.senders.smtp.utils.Base64;

public class LoginSASL implements SASL {

    public String getID() {
        return "LOGIN";
    }

    public String getResponse(Properties props, String serverResponse) {
        String user = props.getProperty(Constants.USER);
        String pass = props.getProperty(Constants.PASS);
        if (serverResponse == null || serverResponse.equals("") || user == null || pass == null) {
            return null;
        }
        byte[] text = null;
        try {
            text = Base64.decode(serverResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String decodeServerResponse = new String(text);
        decodeServerResponse = decodeServerResponse.trim().toLowerCase();
        if (decodeServerResponse.equalsIgnoreCase("username:")) {
            return new String(Base64.encode(user.getBytes()));
        } else if (decodeServerResponse.equalsIgnoreCase("password:")) {
            return new String(Base64.encode(pass.getBytes()));
        }
        return new String(Base64.encode(("" + ((char)0) + user + ((char)0) + pass).getBytes()));
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
