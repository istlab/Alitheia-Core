package eu.sqooss.impl.metrics.wc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.db.Measurement;
import eu.sqooss.service.db.ProjectFile;

public class WcJob extends AbstractMetricJob {

    private ProjectFile pf;
    
    public WcJob(AbstractMetric owner, ProjectFile a) {
        super(owner);
        pf = a;
    }

    public void run() {
        Measurement m = new Measurement();
        
        File f = null;
        int lines = 0;
        //File f = fds.getFile(pf);
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            while(true) {
                lnr.readLine();
                lines++;
            }
        } catch (FileNotFoundException e) {
            log.error(this.getClass().getName() + " Cannot open file: " +
                    f.getAbsolutePath());
        } catch (IOException ignored) {
        }
        
        
        m.setProjectVersion(pf.getProjectVersion());
        
    }
}
