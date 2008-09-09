/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 Diomidis Spinellis <dds@aueb.gr>
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.testability.Testability;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;

public class TestabilityImplementation extends AbstractMetric implements Testability {

    private FDSService fds;

    private static final String MNEMONIC_NCASES   = "Testability.ncases";

    public TestabilityImplementation(BundleContext bc) {
        super(bc);

        // Tells the metric activator when to call this metric
        super.addActivationType(ProjectFile.class);

        // Tells the UI what metric this is calculated against
        super.addMetricActivationType(MNEMONIC_NCASES, ProjectFile.class);

        // Obtain file descriptors
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());


        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    this.getDescription(),
                    "TESTABILITY",
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        //Return a list of ResultEntries by querying the DB for the measurements
        //implement by the supported metric and calculated for the specific
        //project file
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        filter.put("metric", m);
        List<ProjectFileMeasurement> measurement =
            db.findObjectsByProperties(ProjectFileMeasurement.class, filter);
    	return convertFileMeasurements(measurement,m.getMnemonic());
    }

    /** Concrete testability scanners that we support. */
    private static HashMap <String, LinkedList<TestabilityScanner> > allScanners =
            new HashMap<String, LinkedList<TestabilityScanner> >();
    static {
        LinkedList<TestabilityScanner> langScanners =
                new LinkedList<TestabilityScanner>();
        // Add more Java scanners here
        langScanners.add(new JUnitMetrics());
        allScanners.put(".java", langScanners);
        allScanners.put(".JAVA", langScanners);

        langScanners.clear();
        // Add more C++ scanners here
        langScanners.add(new CppUnitMetrics());
        langScanners.add(new NUnitMetrics());
        allScanners.put(".cpp", langScanners);
        allScanners.put(".CPP", langScanners);
        allScanners.put(".cc", langScanners);
        allScanners.put(".CC", langScanners);

        langScanners.clear();
        // Add more C# scanners here
        langScanners.add(new NUnitMetrics());
        allScanners.put(".cs", langScanners);
        allScanners.put(".CS", langScanners);
    }

    public void run(ProjectFile pf) {
        //1. Get stuff related to the provided project file
        //2. Calculate one or more numbers
        //3. Store a result to the database

        // We do not support directories and binary files
        if (pf.getIsDirectory() ||
                FileTypeMatcher.getFileType(pf.getName()).equals(
                FileTypeMatcher.FileType.BIN))
            return;

        String extension = FileTypeMatcher.getFileExtension(pf.getName());
        LinkedList<TestabilityScanner> scanners = allScanners.get(extension);
        // Metric doesn't support this type of file
        if (extension == null)
            return;

        // Create an input stream from the project file's content
        InputStream in = fds.getFileContents(pf);
        if (in == null)
            return;
        int numTestCases = 0;
        log.info(this.getClass().getName() + " Measuring: "
                + pf.getFileName());
        try {
            LineNumberReader lnr =
                new LineNumberReader(new InputStreamReader(in));

            // Measure test cases, using each scanner
            for (TestabilityScanner s : scanners) {
                lnr.mark(1024 * 1024);
                s.scan(lnr);
                numTestCases += s.getTestCases();
                lnr.reset();
            }

            lnr.close();

            // Store the results
            Metric metric = Metric.getMetricByMnemonic(MNEMONIC_NCASES);
            ProjectFileMeasurement ncases = new ProjectFileMeasurement(
                    metric,pf,String.valueOf(numTestCases));
            db.addRecord(ncases);
            markEvaluation(metric, pf);
        } catch (IOException e) {
            log.error(this.getClass().getName() + " IO Error <" + e
                    + "> while measuring: " + pf.getFileName());

        }
    }


}

// vi: ai nosi sw=4 ts=4 expandtab
