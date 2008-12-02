/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  *                Athens, Greece.
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
** That copyright notice makes sense for SQO-OSS paricipants but
** not for everyone. For the Squeleton plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the Squeleton
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string 'metrics'
** because this is hard-coded in parts of the Alitheia core.
*/
package eu.sqooss.metrics.success;

import java.util.List;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

import org.osgi.framework.BundleContext;

/* These are imports of standard Alitheia core services and types.
** You are going to need these anyway; some others that you might
** need are the FDS and other Metric interfaces, as well as more
** DAO types from the database service.
*/
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.StoredProject;
//import weka.core.Instance;

/*
** The Squeleton class is the bit that actually implements the metrics
** in this plug-in. It must extend AbstractMetric (so that it can be
** called by the various metrics drivers) and implement at least one of
** the four *Metric interfaces; by doing so it registers itself as 
** willing to respond to changes of the type corresponding to the
** *Metric interfaces it implements.
**
** The Squeleton example class implements ProjectFileMetric, which 
** means it will respond to changes in files in project revisions
** (i.e. it will do a computation for each file changed in each
** SVN revision).
*/ 
public class Success extends AbstractMetric implements StoredProjectMetric {
    private static String MNEMONIC_METRIC = "SUCCESS";
    private static String METRIC_DEPENDENCY_MNEMONIC = "Wc.loc";
    
    public Success(BundleContext bc) {
        super(bc);        
 
        // Tells the metric activator when to call this plug-in; this
        // should be called for each activation type we support, so
        // for each *Metric interface we implement.
        super.addActivationType(StoredProject.class);
        
        // Tells the UI what it metric is calculated against. This
        // should be called for each (sub)metric in the plug-in.
        // Squeleton has only one metric. The class should be the
        // DAO that activates the specific metric.
        super.addMetricActivationType(MNEMONIC_METRIC, StoredProject.class);
        
        // Add a dependency to another plug-in. If this depencency is not
        // satisfied (i.e. there is no installed plug-in that exports
        // the provided mnemonic), then the metric will fail to 
        // install and run.
        //super.addDependency(METRIC_DEPENDENCY_MNEMONIC);
    }
    
    public boolean install() {
        //This should always be called to run various init tasks
       //log.warn("success is being installed");
        boolean result = super.install();
        
        if (result) {
            result &= super.addSupportedMetrics(
                    this.getDescription(),
                    MNEMONIC_METRIC,
                    MetricType.Type.PROJECT_WIDE);
        }
        //log.warn("success got installed");
        return result;
    }

    public List<ResultEntry> getResult(StoredProject a, Metric m) {
        // Return a list of ResultEntries by querying the DB for the 
        // measurements implement by the supported metric and calculated 
        // for the specific project file.
        
        HashMap<String,Object> properties = new HashMap<String,Object>();
    	properties.put("metric",m);
    	properties.put("storedProject",a);
    	List<StoredProjectMeasurement> l = 
    		db.findObjectsByProperties(StoredProjectMeasurement.class,properties);
    	//log.warn("here 2 :"+l.size());
        List<ResultEntry> res=new ArrayList<ResultEntry>();
        
        for(int i=0;i<l.size();i++){
            //log.warn("here 3 :"+l.get(i).getResult());
            res.add(new ResultEntry(l.get(i).getResult(),ResultEntry.MIME_TYPE_TEXT_PLAIN,MNEMONIC_METRIC));
        }
        return  res;
    }
    
    public void run(StoredProject a) {
        // 1. Get stuff related to the provided project file
        try{
        //log.warn("trying to find object");
        FreshMeatProject fm=FreshMeatProject.DownloadProjectInfo(a.getName());
        //log.warn("propably found it");
        weka.core.converters.ArffLoader al=new weka.core.converters.ArffLoader();
        //log.warn("try to get  dataset");
        al.setSource(new BufferedInputStream(new FileInputStream("dataset.arff")));
        //log.warn("create dataset");
        weka.core.Instances data=al.getDataSet();
        weka.core.Instance s=new weka.core.Instance(9);
        s.setDataset(data);
        s.setValue(0,fm.getVitalityScore());
        s.setValue(1,fm.getPopularityScore());
        s.setValue(2,fm.getRating());
        s.setMissing(3);
        s.setValue(4,fm.getSubscriptions());
        s.setMissing(5);
        s.setMissing(6);
        s.setValue(7,fm.getDaysSinceLaunch());
        s.setValue(8,1.0);
        ObjectInputStream obis=new ObjectInputStream(new FileInputStream("classifier.svm"));
        weka.classifiers.functions.LibSVM svmc=(weka.classifiers.functions.LibSVM) obis.readObject();
        // 2. Calculate one or more numbers
        String clas=svmc.classifyInstance(s)>0?"Successful":"Not Successfful";
       
        
        // 3. Store a result to the database
       StoredProjectMeasurement r = new StoredProjectMeasurement();
       r.setMetric(Metric.getMetricByMnemonic(MNEMONIC_METRIC));
       r.setStoredProject(a);
       r.setResult(clas);
       db.addRecord(r);
       log.warn("Stored result " + clas + " for <" +a.getName() + ">");
        }
        catch(Exception e){
             log.warn(e.toString());
        }
    }

    
}

// vi: ai nosi sw=4 ts=4 expandtab

