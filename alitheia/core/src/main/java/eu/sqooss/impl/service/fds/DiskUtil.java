/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.fds;

import java.io.File;

/**
 * This class contains common static functionality for manipulating
 * directory trees on-disk.
 */
public final class DiskUtil {
    /**
     * Remove all files within a directory d.
     * @param d directory to remove all files from
     * @return true iff it was possible to delete files
     */
    public static boolean rmStar(final File d) {
        if (d.exists() && d.isDirectory() && d.canWrite()) {
            File[] files = d.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Remove an entire directory tree d.
     * @param d directory to remove
     * @return true iff the directory was writable
     */
    public static boolean rmRf(final File d) {
        if (rmStar(d)) {
            // All files now gone
            File [] files = d.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    rmRf(f);
                }
            }
            d.delete();
            return d.exists();
        }
        return false;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

