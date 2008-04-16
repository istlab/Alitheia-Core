/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.metrics.skeleton;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.lib.result.Result;
import eu.sqooss.metrics.skeleton.Skeleton;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.util.Pair;


public class SkeletonImplementation extends AbstractMetric implements Skeleton {
    public SkeletonImplementation(BundleContext bc) {
        super(bc);        
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    this.getDescription(),
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public boolean remove() {

        return false;
    }

    public boolean update() {

        return remove() && install(); 
    }

    public Result getResult(ProjectFile a) {
        Result result = null;
        
        return result;
    }

    public void run(ProjectFile a) {
        SkeletonJob w = null;
        try {
            w = new SkeletonJob(this);

            ServiceReference serviceRef = null;
            serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
            Scheduler s = ((AlitheiaCore) bc.getService(serviceRef)).getScheduler();
            s.enqueue(w);
        } catch (Exception e) {
            log.error("Could not schedule "+ w.getClass().getName() + 
                    " for project file: " + ((ProjectFile)a).getFileName());
        }
    }

    public Collection<Pair<String,AlitheiaPlugin.ConfigurationTypes>>
        getConfigurationSchema() {
        // There is a reason why we do this in such a peculiar fashion:
        // in our experience doing this in a "quicker" fashion we
        // get bizzarre execution failures. So we build pairs
        // carefully, then add them to the collection.
        Pair<String,AlitheiaPlugin.ConfigurationTypes> p0 = new Pair<String,AlitheiaPlugin.ConfigurationTypes>( new String("funky-count"),AlitheiaPlugin.ConfigurationTypes.INTEGER);
        Pair<String,AlitheiaPlugin.ConfigurationTypes> p1 = new Pair<String,AlitheiaPlugin.ConfigurationTypes>( new String("is-funky"),AlitheiaPlugin.ConfigurationTypes.BOOLEAN);
        Pair<String,AlitheiaPlugin.ConfigurationTypes> p2 = new Pair<String,AlitheiaPlugin.ConfigurationTypes>( new String("funky-name"),AlitheiaPlugin.ConfigurationTypes.STRING);

        Collection<Pair<String,AlitheiaPlugin.ConfigurationTypes>> c = new ArrayList<Pair<String,AlitheiaPlugin.ConfigurationTypes>>(3);
        c.add(p0);
        c.add(p1);
        c.add(p2);

        return c;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

