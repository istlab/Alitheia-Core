/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
 *
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
package eu.sqooss.service.util;

import java.io.File;
import java.io.ByteArrayOutputStream;

/**
 * This is a static utility class for various file manipulations.
 */
public class FileUtils {
    /**
     * Read the contents of a file and return them as a byte array.
     *
     * @param f File to read.
     * @return File contents as a byte array or null on error (such
     *      as empty file, no such file, or IO error).
     */
    public static byte[] fileContents(File f) {
        if (f==null) {
            // Bad parameters
            return null;
        }

        if (!(f.exists() && f.isFile() && f.canRead())) {
            // Can only handle existing files
            return null;
        }

        int ilength = 0;
        // Block for hiding the long variable length.
        try {
            long length = f.length();
            if (length > Integer.MAX_VALUE) {
                // Refuse to return more than a few GB of data
                return null;
            }
            ilength = (int)length;
        } finally {
            if (ilength < 1) {
                return null;
            }
        }

        try {
            java.io.InputStream i = new java.io.FileInputStream(f);
            ByteArrayOutputStream o = new ByteArrayOutputStream(ilength);

            // Read in chunks at a time.
            // TODO: optimize this away and create one byte array of the
            // right length already.
            byte[] chunk = new byte[(ilength > 16384) ? 16384 : ilength];
            int r;
            while ( (r=i.read(chunk,0,chunk.length)) >= 0) {
                o.write(chunk,0,r);
            }

            return o.toByteArray();
        } catch (java.io.IOException e) {
            // Just give up.
            return null;
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

