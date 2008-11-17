/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * Written by Diomidis Spinellis.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,
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

package eu.sqooss.impl.metrics.testability;

import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

/** Measure and obtain CppUnit testability metrics. */
class CppUnitMetrics implements TestabilityScanner {

    /** Number of test cases. */
    private int numTestCases;

    /** Number of test cases. */
    public int getTestCases() {
        return numTestCases;
    }

    /** Test case methods. */
    private final static Pattern testCaseMethods =
                Pattern.compile(
                "\\WCPPUNIT_(ASSERT|ASSERT_MESSAGE|FAIL|ASSERT_EQUAL|" +
                "ASSERT_EQUAL_MESSAGE|ASSERT_DOUBLES_EQUAL|" +
                "ASSERT_DOUBLES_EQUAL_MESSAGE|ASSERT_THROW|" +
                "ASSERT_THROW_MESSAGE|ASSERT_NO_THROW|" +
                "ASSERT_NO_THROW_MESSAGE|ASSERT_ASSERTION_FAIL|" +
                "ASSERT_ASSERTION_FAIL_MESSAGE|ASSERT_ASSERTION_PASS|" +
                "ASSERT_ASSERTION_PASS_MESSAGE)\\W");

    /** Scan the specified file. */
    public void scan(LineNumberReader r) throws IOException {
        numTestCases = 0;
        String line = null;
        while ((line = r.readLine()) != null) {
            Matcher m = testCaseMethods.matcher(line);
            if (m.find())
                numTestCases++;
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
