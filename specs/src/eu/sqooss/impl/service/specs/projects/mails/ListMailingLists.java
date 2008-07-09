package eu.sqooss.impl.service.specs.projects.mails;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpMailingList;
import eu.sqooss.impl.service.dsl.SpProject;

@RunWith(ConcordionRunner.class)
public class ListMailingLists
{
    public void addProject(String projectName, String mailPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.mail = "file://"+new File(mailPath).getCanonicalPath();
        project.create();
    }
    
    public long getMailingListsCount(String projectName)
    {
        return new SpProject(projectName).mailingLists().size();
    }
    
    public List<SpMailingList> getMailingLists(String projectName)
    {
        return new SpProject(projectName).mailingLists();
    }
}
