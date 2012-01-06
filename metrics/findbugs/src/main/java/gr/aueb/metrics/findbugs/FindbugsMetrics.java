/*
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
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

/*
** That copyright notice makes sense for code residing in the 
** main SQO-OSS repository. For the FindbugsMetrics plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the FindbugsMetrics
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string '.metrics.'
** because this is how Alitheia Core discovers the metric plug-ins. 
*/
package gr.aueb.metrics.findbugs;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
import org.osgi.framework.BundleContext;

/* These are imports of standard Alitheia core services and types.
** You are going to need these anyway; some others that you might
** need are the FDS and other Metric interfaces, as well as more
** DAO types from the database service.
*/
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;


@MetricDeclarations(metrics = {
        @MetricDecl(mnemonic = "SECBUG", activators = {ProjectVersion.class},
                descr = "FindbugsMetrics Metric")
})
public class FindbugsMetrics extends AbstractMetric {

    public FindbugsMetrics(BundleContext bc) {
        super(bc);
    }

    public List<Result> getResult(ProjectVersion pv, Metric m) {
        return getResult(pv, ProjectVersionMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public void run(ProjectVersion pv) {

        if (pv.getFiles(Pattern.compile("/pom.xml")).isEmpty()) {
            log.info("Skipping version " + pv + " as no pom.xml " +
                    "file could be found");
            return;
        }

        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        OnDiskCheckout odc = null;
        try {
            odc = fds.getCheckout(pv, "/trunk");
            File checkout = odc.getRoot();

            Runtime run = Runtime.getRuntime();

            //Process pr = run.exec("ls", new String[1], checkout);
            Process pr = run.exec("ls");
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (CheckoutException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (odc != null)
                fds.releaseCheckout(odc);
        }
    }


}

// vi: ai nosi sw=4 ts=4 expandtab
