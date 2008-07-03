package eu.sqooss.impl.service.specs.projects.scm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpProject;
import eu.sqooss.impl.service.dsl.SpRevision;

@RunWith(ConcordionRunner.class)
public class SourceTree
{
    public void addProject(String projectName, String scmPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.repository = "file://"+new File(scmPath).getCanonicalPath();
        project.create();
    }
    
    public ArrayList<String> getFiles(String projectName, String revNumber, String folderName) throws Exception
    {
        SpProject project = new SpProject(projectName);
        SpRevision rev = project.revisions().get(project.revisions().size()-Integer.parseInt(revNumber));
        
        ArrayList<String> files = rev.files();
        ArrayList<String> result = new ArrayList<String>();
        
        for (String file : files) {
            if (file.startsWith(folderName+"/")) {
                result.add(file.substring(folderName.length()+1));
            }
        }
        
        return result;
    }
    
}
