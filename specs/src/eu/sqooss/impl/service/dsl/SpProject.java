package eu.sqooss.impl.service.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;
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
        mail = project.getMail();
        bugs = project.getBugs();
        repository = project.getRepository();
        
        db.commitDBSession();
    }

    public void create() {
        db.startDBSession();
        StoredProject project = new StoredProject();

        project.setName(name);
        project.setBugs(bugs);
        project.setMail(mail);
        project.setRepository(repository);
        
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
        SCMAccessor scm = tds.getAccessor(id).getSCMAccessor();
        
        ProjectRevision rev = new ProjectRevision(scm.getHeadRevision());
        while (rev!=null && rev.isValid() && rev.getSVNRevision()>0) {
            scm.resolveProjectRevision(rev);
            result.add(new SpRevision(this, rev));
            rev = rev.prev();
        }

        tds.releaseAccessor(tds.getAccessor(id));
        return result;
    }
}
