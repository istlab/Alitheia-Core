package eu.sqooss.service.admin.actions.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.BTSEntry;

public class TestBTSAccessorImp implements BTSAccessor {
    private static List<URI> supportedSchemes = new ArrayList<URI>();

    static {
        try {
            supportedSchemes.add(new URI("test-bts-acc://random"));
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
        return "TestBTSAccessor";
    }

    @Override
    public BTSEntry getBug(String bugID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getAllBugs() {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public List<String> getBugsNewerThan(Date d) {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

}
