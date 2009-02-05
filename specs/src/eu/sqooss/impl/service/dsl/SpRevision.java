package eu.sqooss.impl.service.dsl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpRevision {
    private SpProject project;
    private Revision revision;
    
    SpRevision(SpProject p, Revision r) {
        project = p;
        revision = r;
    }

    public String uniqueId() {
        return revision.getUniqueId();
    }
    
    public Date date() {
        return revision.getDate();
    }
    
    public String commitLog()
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        SCMAccessor scm = null;
		try {
			scm = tds.getAccessor(project.id).getSCMAccessor();
		} catch (InvalidAccessorException e) {
			return null;
		}
        
        CommitLog log = scm.getCommitLog(scm.getPreviousRevision(revision), revision);
        
        tds.releaseAccessor(tds.getAccessor(project.id));
        
        return log.message(revision);
    }
    
    public ArrayList<String> files()
        throws CheckoutException,
               IOException {
        ArrayList<String> result = new ArrayList<String>();
        //FIXME: TODO: Fix this
        FDSService fds = SpecsActivator.alitheiaCore.getFDSService();
        OnDiskCheckout checkout = fds.getCheckout(null);
        
        File root = checkout.getRoot();
        ArrayList<File> files = collectFilesRec(root);
        
        TreeSet<String> paths = new TreeSet<String>();
        for (File file : files) {
            paths.add(file.getCanonicalPath().substring(root.getCanonicalPath().length()+1));
        }
        
        fds.releaseCheckout(checkout);
        
        result.addAll(paths);
        
        return result;
    }
    
    private ArrayList<File> collectFilesRec(File root) {
        ArrayList<File> result = new ArrayList<File>();
        
        File[] files = root.listFiles();
        
        for (File file : files) {
            result.add(file);
            if (file.isDirectory()) {
                result.addAll(collectFilesRec(file));
            }
        }
        
        return result;
    }
    
    public ArrayList<Change> changes()
        throws InvalidRepositoryException,
               InvalidProjectRevisionException,
               FileNotFoundException {
        ArrayList<Change> result = new ArrayList<Change>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        SCMAccessor scm = null;
		try {
			scm = tds.getAccessor(project.id).getSCMAccessor();
		} catch (InvalidAccessorException e) {
			return null;
		}

        Diff diff = scm.getDiff("/", scm.getPreviousRevision(revision), revision);

       /* TreeSet<String> files = new TreeSet<String>(diff.getChangedFiles());
        for (String file : files) {
            result.add(new SpRevision.Change(file, diff.getChangedFilesStatus().get(file).name().toLowerCase()));
        }*/
        
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
