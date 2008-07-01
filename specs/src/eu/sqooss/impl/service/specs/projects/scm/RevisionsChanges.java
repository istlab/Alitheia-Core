package eu.sqooss.impl.service.specs.projects.scm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;
import eu.sqooss.impl.service.dsl.SpRevision;

@RunWith(ConcordionRunner.class)
public class RevisionsChanges
{
    public void addProject(String projectName, String scmPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.repository = "file://"+new File(scmPath).getCanonicalPath();
        project.create();
    }
    
    public String getCommitLog(String projectName, String revNumber) throws Exception
    {
        SpProject project = new SpProject(projectName);
        SpRevision rev = project.revisions().get(project.revisions().size()-Integer.parseInt(revNumber));
        return rev.commitLog();
    }
    
    public ArrayList<SpRevision.Change> getChanges(String projectName, String revNumber) throws Exception
    {
        SpProject project = new SpProject(projectName);
        SpRevision rev = project.revisions().get(project.revisions().size()-Integer.parseInt(revNumber));
        return rev.changes();
    }
    
}
