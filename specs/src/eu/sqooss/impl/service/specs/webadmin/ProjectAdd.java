package eu.sqooss.impl.service.specs.webadmin;

import java.io.File;
import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

@RunWith(ConcordionRunner.class)
public class ProjectAdd
{
    private Selenium selenium = null;
    
    @Before
    public void setUp()
    {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox",
                                       "http://localhost:8088");
        selenium.start();
    }
    
    @After
    public void tearDown()
    {
        selenium.stop();
    }
    
    public void addProject(String projectName, String scmPath) throws Exception
    {
        String repository = "file://"+new File(scmPath).getCanonicalPath();
        
        selenium.open("http://localhost:8088");
        selenium.click("link=Projects");
        selenium.waitForPageToLoad("30000");
        selenium.click("//input[@value='Add project']");
        selenium.waitForPageToLoad("30000");
        selenium.type("projectName", projectName);
        selenium.type("projectHomepage", "http://foo.net");
        selenium.type("projectContact", "bar@foo.net");
        selenium.type("projectSCM", repository);
        selenium.click("//input[@value='Apply']");
        selenium.waitForPageToLoad("30000");
    }
    
    public ArrayList<Project> getProjects()
    {
        ArrayList<Project> result = new ArrayList<Project>();
        
        int idx = 1;
        String name = selenium.getTable("//form[@id='projects']/fieldset/table."+idx+".1");
        while (name!=null)
        {
            Project p = new Project();
            p.name = name;
            result.add(p);
            idx++;
            try {
                name = selenium.getTable("//form[@id='projects']/fieldset/table."+idx+".1");
            } catch (SeleniumException e) {
                name = null;
            }
        }
        
        return result;
    }
    
    class Project
    {
        public String name;
    }
}
