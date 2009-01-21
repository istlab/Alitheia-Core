package eu.sqooss.impl.service.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpProject implements SpEntity {
    private DBService db = SpecsActivator.alitheiaCore.getDBService();

    long id = -1;
    boolean persistent = false;
    
    public String name;
    public String mail = null;
    public String bugs = null;
    public String repository = null;
    
    public static ArrayList<SpProject> allProjects() {
        DBService db = SpecsActivator.alitheiaCore.getDBService();
        ArrayList<SpProject> result = new ArrayList<SpProject>();
        
        db.startDBSession();
        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        
        for (StoredProject project : projects) {
            result.add(new SpProject(project.getName(), project.getId()));
        }
        db.commitDBSession();
        
        return result;
    }
    
    public SpProject(String n) {
        name = n;
        load();
    }
    
    private SpProject(String n, long i) {
        name = n;
        id = i;
        persistent = true;
    }
    
    public void load() {
        db.startDBSession();
        HashMap<String,Object> properties = new HashMap<String,Object>();
        properties.put("name", name);
        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, properties);

        if (projects.size()==0) {
            persistent = false;
            return;
        } else if (projects.size()>1) {
            throw new RuntimeException("Several projects with the name "+name+" found");
        }
        
        StoredProject project = projects.get(0);
        
        id = project.getId();
        name = project.getName();
        mail = project.getMailUrl();
        bugs = project.getBtsUrl();
        repository = project.getScmUrl();
        
        db.commitDBSession();
    }

    public void create() {
        db.startDBSession();
        StoredProject project = new StoredProject();

        project.setName(name);
        project.setBtsUrl(bugs);
        project.setMailUrl(mail);
        project.setScmUrl(repository);
        
        db.addRecord(project);
        db.commitDBSession();
        persistent = true;
    }

    public void delete() {
        db.startDBSession();
        db.deleteRecord(db.findObjectById(StoredProject.class, id));
        db.commitDBSession();
        persistent = false;
    }
    
    public ArrayList<SpRevision> revisions() throws InvalidRepositoryException, InvalidProjectRevisionException {
        ArrayList<SpRevision> result = new ArrayList<SpRevision>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(id, name, bugs, mail, repository);
        SCMAccessor scm = null;
		try {
			scm = tds.getAccessor(id).getSCMAccessor();
		} catch (InvalidAccessorException e) {
			return null;
		}
        
        Revision rev = scm.getHeadRevision();
        while (rev!=null 
                && rev.getUniqueId() != null && rev.getUniqueId() != "") {

            result.add(new SpRevision(this, rev));
            rev = scm.getPreviousRevision(rev);
        }

        tds.releaseAccessor(tds.getAccessor(id));
        return result;
    }

    public ArrayList<SpMailingList> mailingLists() {
        ArrayList<SpMailingList> result = new ArrayList<SpMailingList>();
        
        TDSService tds = SpecsActivator.alitheiaCore.getTDSService();
        tds.addAccessor(id, name, bugs, mail, repository);
        MailAccessor mail = null;
		try {
			mail = tds.getAccessor(id).getMailAccessor();
		} catch (InvalidAccessorException e) {
			return null;
		}

        List<String> names = mail.getMailingLists();
        for (String name : names) {
            result.add(new SpMailingList(this, name));
        }
        
        tds.releaseAccessor(tds.getAccessor(id));
        
        return result;
    }
}
