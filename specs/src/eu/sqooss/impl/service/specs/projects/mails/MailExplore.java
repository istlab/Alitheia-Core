package eu.sqooss.impl.service.specs.projects.mails;

import java.io.File;
import java.io.IOException;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;

@RunWith(ConcordionRunner.class)
public class MailExplore
{
    public void addProject(String projectName, String mailPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.mail = "file://"+new File(mailPath).getCanonicalPath();
        System.out.println(project.mail);
        project.create();
    }
    
    public boolean projectHasLists(String projectName)
    {
        return !new SpProject(projectName).mailingLists().isEmpty();
    }
}
