package eu.sqooss.impl.service.dsl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpMailingList {
    private SpProject project;
    public String name;
    
    SpMailingList(SpProject p, String n) {
        project = p;
        name = n;
    }
    
    private ArrayList<Message> messagesFromIds(MailAccessor mail, List<String> ids)
        throws FileNotFoundException, MessagingException {
        ArrayList<Message> result = new ArrayList<Message>();

        for (String id : ids) {
            MimeMessage mime = mail.getMimeMessage(name, id);
            
            Message msg = new Message(this);
            msg.id = id;
            msg.date = mime.getSentDate().toString();
            Address[] addresses = mime.getFrom();
            
            msg.from = "";
            for (Address address : addresses) {
                msg.from+= address.toString() +", ";
            }
            msg.from = msg.from.substring(0, msg.from.length()-2);
            
            msg.subject = mime.getSubject();
            
            result.add(msg);
        }

        return result;
    }
    
    public ArrayList<Message> allMessages() throws FileNotFoundException, MessagingException {
        ArrayList<Message> result = new ArrayList<Message>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        MailAccessor mail = tds.getAccessor(project.id).getMailAccessor();

        List<String> ids = mail.getMessages(name);
        
        result = messagesFromIds(mail, ids);
        
        tds.releaseAccessor(tds.getAccessor(project.id));

        return result;
    }

    public ArrayList<Message> unreadMessages() throws FileNotFoundException, MessagingException {
        ArrayList<Message> result = new ArrayList<Message>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        MailAccessor mail = tds.getAccessor(project.id).getMailAccessor();

        List<String> ids = mail.getNewMessages(name);
        
        result = messagesFromIds(mail, ids);
        
        tds.releaseAccessor(tds.getAccessor(project.id));

        return result;
    }
    
    public ArrayList<Message> seenMessages() throws FileNotFoundException, MessagingException {
        ArrayList<Message> result = new ArrayList<Message>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
        MailAccessor mail = tds.getAccessor(project.id).getMailAccessor();

        List<String> ids = mail.getMessages(name);
        List<String> seen_ids = mail.getNewMessages(name);
        for (String id : seen_ids) {
            if (ids.contains(id)) {
                ids.remove(id);
            }
        }
        
        result = messagesFromIds(mail, ids);
        
        tds.releaseAccessor(tds.getAccessor(project.id));

        return result;
    }
    
    public class Message {
        private SpMailingList parent;
        public String id;
        public String date;
        public String from;
        public String subject;

        public Message(SpMailingList p) {
            parent = p;
        }
        
        public void markAsSeen() throws FileNotFoundException {
            TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
            tds.addAccessor(project.id, project.name, project.bugs, project.mail, project.repository);
            MailAccessor mail = tds.getAccessor(parent.project.id).getMailAccessor();
            
            mail.markMessageAsSeen(parent.name, id);
            
            tds.releaseAccessor(tds.getAccessor(parent.project.id));
        }
    }
}
