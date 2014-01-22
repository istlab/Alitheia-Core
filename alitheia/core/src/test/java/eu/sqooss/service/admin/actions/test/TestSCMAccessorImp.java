package eu.sqooss.service.admin.actions.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.AnnotatedLine;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNode;
import eu.sqooss.service.tds.SCMNodeType;

public class TestSCMAccessorImp implements SCMAccessor {
    private static List<URI> supportedSchemes = new ArrayList<URI>();

    static {
        try {
            supportedSchemes.add(new URI("test-scm-acc://random"));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<URI> getSupportedURLSchemes() {
        return supportedSchemes;
    }

    @Override
    public void init(URI dataURL, String projectName)
            throws AccessorException {
        // Don't do anything
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "TestSCMAccessor";
    }

    @Override
    public Revision newRevision(Date d) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Revision newRevision(String uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Revision getHeadRevision() throws InvalidRepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Revision getFirstRevision() throws InvalidRepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Revision getNextRevision(Revision r)
            throws InvalidProjectRevisionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Revision getPreviousRevision(Revision r)
            throws InvalidProjectRevisionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValidRevision(Revision r) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void getCheckout(String repoPath, Revision revision, File localPath)
            throws InvalidProjectRevisionException, InvalidRepositoryException,
            FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateCheckout(String repoPath, Revision src, Revision dst,
            File localPath) throws InvalidProjectRevisionException,
            InvalidRepositoryException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFile(String repoPath, Revision revision, File localPath)
            throws InvalidProjectRevisionException, InvalidRepositoryException,
            FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFile(String repoPath, Revision revision, OutputStream stream)
            throws InvalidProjectRevisionException, InvalidRepositoryException,
            FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Diff getDiff(String repoPath, Revision r1, Revision r2)
            throws InvalidProjectRevisionException, InvalidRepositoryException,
            FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SCMNodeType getNodeType(String repoPath, Revision r)
            throws InvalidRepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SCMNode> listDirectory(SCMNode dir)
            throws InvalidRepositoryException, InvalidProjectRevisionException {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public SCMNode getNode(String path, Revision r)
            throws InvalidRepositoryException, InvalidProjectRevisionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AnnotatedLine> getNodeAnnotations(SCMNode s) {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

}