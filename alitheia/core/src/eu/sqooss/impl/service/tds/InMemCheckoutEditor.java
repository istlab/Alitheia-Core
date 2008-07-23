package eu.sqooss.impl.service.tds;

import java.io.OutputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.SCMNode;
import eu.sqooss.service.tds.SCMNodeType;

public class InMemCheckoutEditor implements ISVNEditor {
    
    private SCMNode root;
    protected long targetRevision;
    public static Logger logger;
    
    public InMemCheckoutEditor(SCMNode dir, long r) {
        this.targetRevision = r;
        root = dir;
        logger.debug("In memory checkout editor created for r." + r + 
                " in " + dir.getPath());
    }
    
    public void applyTextDelta(String path, String checksum) {
        logger.debug("applyTextDelta " + path);
    }
    
    public OutputStream textDeltaChunk(String path, SVNDiffWindow w)
            throws SVNException {
        logger.debug("textDeltaChunk" + path);
        return null;
    }

    public void textDeltaEnd(String path) {
        logger.debug("textDeltaEnd"  + path);
        return;
    }
    
    public void deleteEntry(String path, long revision) {
        logger.debug("deleteEntry " + path);
    }
    
    public void addFile(String path, String sourcePath, long sourceRevision) {
        logger.debug("addFile " + path);
        if (root != null) {
            SCMNode node = new SCMNode("/" + path, SCMNodeType.FILE, targetRevision);
            root.appendChild(node);
        } else {
            logger.debug("Tried to checkout to nowhere...");
        }
    }
    
    public void addDir(String path, String sourcePath, long sourceRevision) {
        logger.debug("addDirectory " + path);
        if (root != null ) {
            SCMNode node = new SCMNode("/" + path, SCMNodeType.DIR, targetRevision);
            root.appendChild(node);
        } 
    }

    public void abortEdit() throws SVNException {
        logger.debug("abortEdit");
        
    }

    public void absentDir(String arg0) throws SVNException {
        logger.debug("absentDir");
        
    }

    public void absentFile(String arg0) throws SVNException {
        logger.debug("absentFile");
        
    }

    public void changeDirProperty(String arg0, String arg1) throws SVNException {
        logger.debug("changeDirProperty");
        
    }

    public void changeFileProperty(String arg0, String arg1, String arg2)
            throws SVNException {
        logger.debug("changeFileProperty");
    }

    public void closeDir() throws SVNException {
        logger.debug("closeDir");
        
    }

    public SVNCommitInfo closeEdit() throws SVNException {
        logger.debug("closeEdit");
        return null;
    }

    public void closeFile(String arg0, String arg1) throws SVNException {
        logger.debug("closeFile");
    }

    public void openDir(String arg0, long arg1) throws SVNException {
        logger.debug("openDir");
    }

    public void openFile(String arg0, long arg1) throws SVNException {
        logger.debug("openFile");
    }

    public void openRoot(long arg0) throws SVNException {
        logger.debug("openRoot");
    }

    public void targetRevision(long arg0) throws SVNException {
        logger.debug("targetRevision");
    }
}
