package eu.sqooss.impl.service.specs.projects;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;

@RunWith(ConcordionRunner.class)
public class ProjectAdd
{
    public void addProject(String projectName)
    {
        new SpProject(projectName).create();
    }
    
    public ArrayList<SpProject> getProjects()
    {
        return SpProject.allProjects();
    }
}
