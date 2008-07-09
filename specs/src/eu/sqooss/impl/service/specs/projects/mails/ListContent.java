package eu.sqooss.impl.service.specs.projects.mails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpMailingList;
import eu.sqooss.impl.service.dsl.SpProject;

@RunWith(ConcordionRunner.class)
public class ListContent
{
    public void addProject(String projectName, String mailPath) throws IOException
    {
        SpProject project = new SpProject(projectName);
        project.mail = "file://"+new File(mailPath).getCanonicalPath();
        project.create();
    }
    
    private SpMailingList findList(String projectName, String listName)
    {
        ArrayList<SpMailingList> lists = new SpProject(projectName).mailingLists();
        
        for (SpMailingList list : lists) {
            if (list.name.equals(listName)) {
                return list;
            }
        }
        
        return null;        
    }
    
    public long getMailCount(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        SpMailingList list = findList(projectName, listName);
        
        return list.allMessages().size();
    }
    
    public List<SpMailingList.Message> getMails(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        return findList(projectName, listName).allMessages();
    }

    public void markFirstMailAsSeen(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        findList(projectName, listName).allMessages().get(0).markAsSeen();
    }
    
    public long getUnreadCount(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        return findList(projectName, listName).unreadMessages().size();
    }
    
    public List<SpMailingList.Message> getUnreadMails(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        return findList(projectName, listName).unreadMessages();
    }

    public List<SpMailingList.Message> getSeenMails(String projectName, String listName) throws FileNotFoundException, MessagingException
    {
        return findList(projectName, listName).seenMessages();
    }
}
