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

package eu.sqooss.impl.metrics.krazy;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.util.Pair;


public class KrazyImplementation extends AbstractMetric implements ProjectFileMetric  {
    public KrazyImplementation(BundleContext bc) {
        super(bc);        
    }
    
    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "QString::null detection",
                    "Krazy.qsn",
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public boolean remove() {
        return super.remove();
    }

    public boolean update() {
        return remove() && install(); 
    }

    private static String CPPExtensions[] = {
        ".h",".cc",".cpp",".C"
    } ;
    
    public void run(ProjectFile pf) {
        boolean found = false;
        if (FileTypeMatcher.getFileType(pf.getName())
                .equals(FileTypeMatcher.FileType.SRC)) {
            String extension = FileTypeMatcher.getFileExtension(pf.getFileName());
            for(String s : CPPExtensions) {
                if (s.equals(extension)) {
                    found=true;
                    break;
                }
            }
        }
        if (!found) {
            return;
        }
        // So here we know we are dealing with a C++ source file
        // (for limited values of "know", and .h files may still be
        // C files in reality).
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        // TODO Auto-generated method stub
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

