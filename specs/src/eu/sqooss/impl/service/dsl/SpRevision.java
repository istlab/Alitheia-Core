package eu.sqooss.impl.service.dsl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpRevision {
    private SpProject project;
    private ProjectRevision revision;
    
    SpRevision(SpProject p, ProjectRevision r) {
        project = p;
        revision = r;
    }

    public long number() {
        return revision.getSVNRevision();
    }
    
    public Date date() {
        return revision.getDate();
    }
    
    public String commitLog()
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        SCMAccessor scm = tds.getAccessor(project.id).getSCMAccessor();
        
        CommitLog log = scm.getCommitLog(revision.prev(), revision);
        
        tds.releaseAccessor(tds.getAccessor(project.id));
        
        return log.message(revision);
    }
    
    public ArrayList<Change> changes()
        throws InvalidRepositoryException,
               InvalidProjectRevisionException,
               FileNotFoundException {
        ArrayList<Change> result = new ArrayList<Change>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        SCMAccessor scm = tds.getAccessor(project.id).getSCMAccessor();

        Diff diff = scm.getDiff("/", revision.prev(), revision);

        TreeSet<String> files = new TreeSet<String>(diff.getChangedFiles());
        for (String file : files) {
            result.add(new SpRevision.Change(file, diff.getChangedFilesStatus().get(file).name().toLowerCase()));
        }
        
        tds.releaseAccessor(tds.getAccessor(project.id));

        return result;
    }
    
    public class Change {
        public String file;
        public String type;
        
        private Change(String f, String t) {
            file = f;
            type = t;
        }
    }
}
