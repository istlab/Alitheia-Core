package eu.sqooss.impl.service.specs.projects.scm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;

@RunWith(ConcordionRunner.class)
public class ListRevisions
{
    public void addProject(String projectName, String scmPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.repository = "file://"+new File(scmPath).getCanonicalPath();
        project.create();
    }
    
    public long getRevisionCount(String projectName)throws InvalidRepositoryException, InvalidProjectRevisionException
    {
        return new SpProject(projectName).revisions().size();
    }
    
    public List<ProjectRevision> getLastFiveRevisions(String projectName) throws InvalidRepositoryException, InvalidProjectRevisionException
    {
        return new SpProject(projectName).revisions().subList(0, 5);
    }
}
