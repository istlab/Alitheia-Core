/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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
import java.io.IOException;

import eu.sqooss.service.logging.Logger;

/**
 * This class contains common static functionality for manipulating
 * directory trees on-disk.
 */
public final class DiskUtil {
    /** Constructor. This is a utility class, no constructor wanted. */
    private DiskUtil() {
    }

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

    /**
     * Create a bunch of junk files underneath a given directory.
     * Recurse into any directories created this way (and create
     * fewer files and directories in the subdirs, so that the
     * recursion terminates).
     *
     * @param r root under which to create junk.
     * @param mf maximum number of files to create under r.
     * @param md maximum number of directories to create under r.
     *
     * @return number of files + directories created under r.
     */
    private static int createTestFiles(final File r,
        final int mf, final int md) {
        // Horribly contused way of getting a random integer in
        // the range 0..mf-1, but alternatives seem to require
        // creating our own Random object.
        int numberOfFiles = (int) Math.round(Math.floor(
            Math.random() * (double) mf));
        int numberOfSubDirs = (int) Math.round(Math.floor(
            Math.random() * (double) md));
        int count = 0;

        for (int i = 0; i < numberOfFiles; ++i) {
            File f = new File(r, new Double(Math.random()).toString());
            try {
                if (f.createNewFile()) {
                    ++count;
                }
            } catch (IOException e) {
                // Ignore the exception
                count += 0;
            }
        }
        for (int i = 0; i < numberOfSubDirs; ++i) {
            File f = new File(r, new Double(Math.random()).toString());
            if (f.mkdir()) {
                ++count;
                count += createTestFiles(f, mf / 2, md - 1);
            }
        }
        return count;
    }

    /**
     * For the self test, maximum number of subdirectories under the
     * top-level directory. Maximum number of subdirectories per
     * level decreases exponentially.
     */
    private static final int STARTING_MAX_SUBDIRS = 8;
    /**
     * For the self-test, maximum number of files in any directory.
     * This should be more than log(STARTING_MAX_SUBDIRS) for the
     * createTestFiles() code to work properly.
     */
    private static final int STARTING_MAX_FILES = 16;
    /**
     * Perform a self-test on the diskutils class by creating
     * a bunch of directories and then deleting them again
     * with the rm*() methods.
     *
     * @param logger the logger to which test results are printed.
     */
    public static void selfTest(final Logger logger) {
        logger.info("Self-test for class DiskUtil.");
        logger.info("Creating test directories ...");

        /* We are going to create at most maxfiles files and
         * maxsubdirs sub-directories at this level. These numbers
         * are halved at each descent, so it terminates.
         */
        final File toplevel = new File("/tmp/DiskUtilsTest");
        if (!toplevel.mkdirs()) {
            logger.warning("Could not create self-test toplevel.");
            return;
        }

        int total = createTestFiles(toplevel,
            STARTING_MAX_SUBDIRS, STARTING_MAX_FILES);
        try {
            // This just ensures that there is at least one file
            if (new File(toplevel, "README").createNewFile()) {
                ++total;
            }
        } catch (IOException e) {
            // Just ignore it
            total += 0;
        }

        logger.info("Created " + total + " files and directories for test.");

        rmStar(toplevel);
        // Now there should be no files left in there
        File[] files = toplevel.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                logger.warning("Failed to remove " + f);
            }
        }

        rmRf(toplevel);
        if (toplevel.exists()) {
            logger.warning("Failed to rm -rf " + toplevel);
        } else {
            logger.info("Successfully removed " + toplevel);
        }

        logger.info("End self-test for class DiskUtil.");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

