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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.util.FileUtils;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.Metric;


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

        if (pv.getFiles(Pattern.compile("pom.xml$"), ProjectVersion.MASK_FILES).isEmpty()) {
            log.info("Skipping version " + pv + " as no pom.xml " +
                    "file could be found");
            return;
        }

        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        OnDiskCheckout odc = null;
        try {
            odc = fds.getCheckout(pv, "/trunk");
            File checkout = odc.getRoot();

            File pom = FileUtils.findBreadthFirst(checkout, Pattern.compile("pom.xml"));

            if (pom == null) {
                log.warn(pv +" No pom.xml found in checkout?!");
                return;
            }

            Runtime run = Runtime.getRuntime();

            ProcessBuilder pb = new ProcessBuilder("mvn", "install", "-DskipTests=true");
            pb.directory(pom.getParentFile());
            pb.redirectErrorStream(true);
            Process pr = pb.start();

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String out = pv.getProject().getName() + "-" + pv.getRevisionId() +
                    "-" + pv.getId() + "-out.txt";
            File f = new File(out);
            FileWriter fw = new FileWriter(f);
            copyOutput(buf, fw);

            if (pr.exitValue() != 0) {
                log.warn("Build with maven failed. See file:" + out);
            } else {
                f.delete();
            }

            List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));

            Set<String> pkgs = getPkgs(pv.getFiles(Pattern.compile("src/main/java/"),
                    ProjectVersion.MASK_FILES));
            for (String pkg: pkgs)
                System.err.println(pkg);

        } catch (CheckoutException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (odc != null)
                fds.releaseCheckout(odc);
        }
    }

    public Set<String> getPkgs(List<ProjectFile> files) {
        Set<String> pkgs = new HashSet<String>();
        Pattern p = Pattern.compile("src/main/java/(.*\\.java)");

        for(ProjectFile f: files) {
            Matcher m = p.matcher(f.getFileName());
            if (m.find()) {
                pkgs.add(FileUtils.dirname(m.group(1)).replace('/','.'));
            }
        }
        return pkgs;
    }

    public void copyOutput(Reader input, Writer output)
            throws IOException
    {
        char[] buf = new char[8192];
        while (true)
        {
            int length = input.read(buf);
            if (length < 0)
                break;
            output.write(buf, 0, length);
            output.flush();
        }

        try { input.close(); } catch (IOException e) {e.printStackTrace();}
        try { output.close(); } catch (IOException e) {e.printStackTrace();}
    }

}

// vi: ai nosi sw=4 ts=4 expandtab
