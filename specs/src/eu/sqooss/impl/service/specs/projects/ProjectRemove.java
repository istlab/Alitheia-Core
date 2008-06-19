package eu.sqooss.impl.service.specs.projects;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;

@RunWith(ConcordionRunner.class)
public class ProjectRemove
{
    public void addProject(String projectName)
    {
        new SpProject(projectName).create();
    }
    
    public void removeProject(String projectName)
    {
        new SpProject(projectName).delete();
    }
    
    public boolean isProjectListEmpty()
    {
        return SpProject.allProjects().isEmpty();
    }
}
