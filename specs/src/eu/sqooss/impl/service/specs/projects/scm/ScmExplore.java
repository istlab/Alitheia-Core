package eu.sqooss.impl.service.specs.projects.scm;

import java.io.File;
import java.io.IOException;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;

@RunWith(ConcordionRunner.class)
public class ScmExplore
{
    public void addProject(String projectName, String scmPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.repository = "file://"+new File(scmPath).getCanonicalPath();
        project.create();
    }
    
    public boolean projectHasHistory(String projectName) throws InvalidRepositoryException, InvalidProjectRevisionException
    {
        return !new SpProject(projectName).revisions().isEmpty();
    }
}
