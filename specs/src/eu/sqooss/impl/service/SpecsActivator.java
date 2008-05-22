/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SpecsActivator implements BundleActivator {

    private class SpecsStats {
        int runsCount;
        int failedCount;
        ArrayList<String> failedNames = new ArrayList<String>();
    }

    public void start(BundleContext bc) throws Exception {
        System.out.println("\n\n");
        System.out.println("Running specs...");

        final String specsRootPkg = "eu.sqooss.impl.service.specs";
        final String specsRootPath = specsRootPkg.replace('.', '/');

        System.out.println("Start processing specs from "+specsRootPkg);
        System.out.println("");

        Enumeration<?> paths = bc.getBundle().findEntries(specsRootPath, "*.class", true);
        SpecsStats stats = new SpecsStats();

        while (paths.hasMoreElements()) {
            String path = ((URL)paths.nextElement()).getPath();
            int i = path.indexOf("specs/");
            path = path.substring(i+6, path.length()-6);

            if (path.contains("$")) continue; //skip inner classes

            String className = "eu.sqooss.impl.service.specs."+path.replace('/', '.');
            Class<?> c = bc.getBundle().loadClass(className);

            System.out.println("Running "+className);
            Result r = JUnitCore.runClasses(c);

            stats.runsCount++;
            if (r.getFailureCount()>0) {
                stats.failedCount++;
                stats.failedNames.add(className);
            }
        }

        if (stats.runsCount==0) {
            System.out.println("NO SPECS FOUND!");
        } else {
            int successPercent = 100 - (stats.failedCount*100)/stats.runsCount;
            System.out.println(""+successPercent+"% specs passed, "+stats.failedCount+" failed out of "+stats.runsCount);

            if (stats.failedCount>0) {
                System.out.println("");
                System.out.println("The following specs FAILED:");
                for (String className : stats.failedNames) {
                    System.out.println("\t"+className);
                }
            }
        }

        bc.getBundle(0).stop();
    }

    public void stop(BundleContext bc) throws Exception {
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

