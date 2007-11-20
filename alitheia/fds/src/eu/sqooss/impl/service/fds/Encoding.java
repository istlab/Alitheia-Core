/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

// Need a package name
package eu.sqooss.impl.service.fds;

import java.util.Random;

public class Encoding {
    public static byte[] intToBytes(final int v) {
        byte[] b = new byte[4];
        b[0] = (byte) (v & 0xff);
        b[1] = (byte) ((v >> 8) & 0xff);
        b[2] = (byte) ((v >> 16) & 0xff);
        b[3] = (byte) ((v >> 24) & 0xff);
        return b;
    }

    private static final String hex = "0123456789abcdef";
    public static String getRandomCheckoutName(final int length, final Random r) {
        // Each character is 4 bits, calculate bytes
        int byteCount = (length + 1) / 2;
        // Get that many random bytes
        byte[] b = new byte[byteCount];
        r.nextBytes(b);
        return bytesToHexString(b);
    }

    public static String bytesToHexString(final byte[] b) {
        // Fill a char array with those bytes
        int length = b.length * 2;
        char[] c = new char[length];
        boolean useHighNibble = false;
        for (int i = 0; i < length; i++) {
            byte thisByte = b[i/2];
            if (useHighNibble) {
                thisByte = (byte) ((b[i/2] >> 4) & 0xf);
            } else {
                thisByte = (byte) (b[i/2] & 0xf);
            }
            c[i] = hex.charAt(thisByte);
            useHighNibble = !useHighNibble;
        }
        return new String(c);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

