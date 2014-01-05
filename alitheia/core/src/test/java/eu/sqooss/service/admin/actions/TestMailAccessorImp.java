package eu.sqooss.service.admin.actions;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.MailAccessor;

public class TestMailAccessorImp implements MailAccessor {
    private static List<URI> supportedSchemes = new ArrayList<URI>();

    static {
        try {
            supportedSchemes.add(new URI("test-mail-acc://random"));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<URI> getSupportedURLSchemes() {
        return supportedSchemes;
    }

    @Override
    public void init(URI dataURL, String projectName) throws AccessorException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        return "TestMailAccessor";
    }

    @Override
    public String getRawMessage(String listname, String msgFileName)
            throws IllegalArgumentException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MimeMessage getMimeMessage(String listname, String msgFileName)
            throws IllegalArgumentException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMessages(String listname)
            throws FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getNewMessages(String listname)
            throws FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMessages(String listName, Date d1, Date d2)
            throws IllegalArgumentException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean markMessageAsSeen(String listname, String msgFileName)
            throws IllegalArgumentException, FileNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getMailingLists() {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

}
