package eu.sqooss.service.admin.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Properties;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminActionBase;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;

/**
 * Action that adds a new project to the project database. 
 * 
 * <h2>Arguments</h2>
 * 
 * <table>
 * <th>
 *  <td>Argument</td>
 *  <td>Required?</td>
 *  <td>Explanation</td>
 * </th>
 * 
 * <tr>
 *  <td>name</td>
 *  <td>*</td>
 *  <td>The name to use to reference the project</td>
 * </tr>
 * <tr>
 *  <td>scm</td>
 *  <td>*</td>
 *  <td>The URL for the project's SCM system</td>
 * </tr>
 * <tr>
 *  <td>dir</td>
 *  <td></td>
 *  <td>Directory that contains the project, in the Alitheia Core expected layout. 
 *      If the action finds this argument it ignores all others.</td>
 * </tr>
 * <tr>
 *  <td>update</td>
 *  <td></td>
 *  <td>Its presence controls whether a data update will be triggered after the project has been
 *  succesfully installed.</td>
 * </tr>
 * </table>
 * 
 * @author gousiosg
 *
 */
public class AddProject extends AdminActionBase {

	public static final String MNEMONIC = "addpr";
	
    Properties p;
    
    @Override
    public String mnemonic() {
        return MNEMONIC;
    }

    @Override
    public String descr() {
        return "Adds a project to the database";
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        String name = null, bts = null, scm = null, mail = null, contact = null, web = null;
        DBService db = AlitheiaCore.getInstance().getDBService();
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        
        if (args.containsKey("dir")) { 
            addProjectDir(args.get("dir").toString());
            
            name = p.getProperty(ConfigOption.PROJECT_NAME.getName());
            bts = p.getProperty(ConfigOption.PROJECT_BTS_URL.getName());
            scm = p.getProperty(ConfigOption.PROJECT_SCM_URL.getName());
            mail = p.getProperty(ConfigOption.PROJECT_ML_URL.getName());
            contact = p.getProperty(ConfigOption.PROJECT_CONTACT.getName());
            web = p.getProperty(ConfigOption.PROJECT_WEBSITE.getName());
        } else {
            name = (args.get("name") == null)?"":args.get("name").toString();
            bts = (args.get("bts") == null)?"":args.get("bts").toString();
            scm = (args.get("scm") == null)?"":args.get("scm").toString();
            mail = (args.get("mail") == null)?"":args.get("mail").toString();
            contact = (args.get("contact") == null)?"":args.get("contact").toString();
            web = (args.get("web") == null)?"":args.get("web").toString();
            
            p = new Properties();
            p.put(ConfigOption.PROJECT_NAME.getName(), name);
            p.put(ConfigOption.PROJECT_WEBSITE.getName(), web);
            p.put(ConfigOption.PROJECT_CONTACT.getName(), contact);
            p.put(ConfigOption.PROJECT_BTS_URL.getName(), bts);
            p.put(ConfigOption.PROJECT_ML_URL.getName(), mail);
            p.put(ConfigOption.PROJECT_SCM_URL.getName(), scm);
        }

        // Avoid missing-entirely kinds of parameters.
        if ( (name == null) || (scm == null) ) {
            error("missing.param", "Missing required parameter " + ((name == null)?"name":"scm"));
        }

        // Avoid adding projects with empty names or SVN.
        if (name.trim().length() == 0 || scm.trim().length() == 0) {
            error("missing.param", "Missing required parameter " + ((name.length() == 0)?"name":"scm"));
        }

        /* Run a few checks before actually storing the project */
        // 1. Duplicate project
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("name",  name);
        if (!db.findObjectsByProperties(StoredProject.class, props).isEmpty()) {
            error("project.exists", "A project with the same name already exists");
        }

        // 2. Check for data handlers Add accessor and try to access project resources
        if (!tds.isURLSupported(scm)) {
            error("tds.unsupported.url", "No appropriate accessor for repository URI: "+ scm);
        }
        
        if (mail != null &&  !mail.isEmpty() && !tds.isURLSupported(mail)) {
             error("tds.unsupported.url", "No appropriate accessor for repository URI: "+ mail);
        }
        
        if (bts != null && !bts.isEmpty() && !tds.isURLSupported(bts)) {
            error("tds.unsupported.url", "No appropriate accessor for repository URI: "+ bts);
        }

        tds.addAccessor(Integer.MAX_VALUE, name, bts, mail, scm);
        
        if (tds.getAccessor(Integer.MAX_VALUE) == null)
        	warn("tds", "Error retrieving accessor for project: " + name);
        
        ProjectAccessor a = tds.getAccessor(Integer.MAX_VALUE);
        
        try {
        	debug("Testing SCM accessor for project: " + name);
            a.getSCMAccessor().getHeadRevision();
            debug("SCM accessor OK. SCM Head: " + 
            		a.getSCMAccessor().getHeadRevision().getUniqueId());
            
            debug("Testing BTS accessor for project: " + name);
            BTSAccessor ba = a.getBTSAccessor();
            if (ba == null) {
                warn("tds.bts.url", "Bug Accessor failed initialization for URI:" + bts);
            } else {
            	debug("BTS accessor OK.");
            }

            debug("Testing mail accessor for project: " + name);
            MailAccessor ma = a.getMailAccessor();
            if (ma == null) {
                warn("tds.mail.maildir", "Mail accessor failed initialization for URI:" + bts);
            } else {
            	debug("Mail accessor OK.");
            }
        } catch (InvalidRepositoryException ire) {
            error("tds.scm.url", "No appropriate accessor for repository URI: "+ scm);
        } catch (InvalidAccessorException iae) {
            error("tds", "Invalid accessor: "+ iae.getAccessorURI());
        } catch (Exception e) {
        	error("tds", "Accessor failed: "+ e.getMessage());
        } finally {
            tds.releaseAccessor(a);
        }
        
        StoredProject sp = new StoredProject(name);
        //The project is now ready to be added 
        db.addRecord(sp);
        
        //Store all known properties to the database
        for (ConfigOption co : ConfigOption.values()) {
            String s = p.getProperty(co.getName());
            
            if (s == null)
                continue;
            
            String[] subopts = s.split(" ");
            
            for (String subopt : subopts) {
                if (subopt.trim().length() > 0)
                    sp.addConfig(co, subopt.trim());
            }
        }
       
        tds.addAccessor(sp.getId(), sp.getName(), sp.getBtsUrl(), sp.getMailUrl(), 
                sp.getScmUrl());

        sp.setClusternode(ClusterNode.thisNode());
        
        log("Added a new project <" + name + "> with ID " + sp.getId());
        
        if (args.get("update") != null)
            AlitheiaCore.getInstance().getUpdater().update(sp);
        
        finished("Project addded succesfully");
    }
    
    private void addProjectDir(String info) throws Exception {
        
        if (info == null || info.length() == 0) {
            error("missing.param", "Missing required parameter path: " + info);
        }
        
        File infoFile = new File(info);
        
        if (!infoFile.exists()) {
            error("missing.path", "The provided path does not exist");
        }
        
        File f = null;
        
        if (infoFile.isDirectory()) {
            File[] contents = infoFile.listFiles(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contentEquals("project.properties"))
                        return true;
                    return false;
                }
            });
        
            if (contents.length <= 0) {
                error("missing.project.properties", "The provided directory does not include a project.properties file");
            }
            
            f = contents[0];
            
        } else {
            if (!info.endsWith("project.properties")) {
                error("missing.project.properties", "The provided directory does not include a project.properties file");
            }
            f = infoFile;
        }

        p = new Properties();
        try {
            p.load(new FileInputStream(f));
        } catch (Exception e1) {
            error("not.properties", "The provided path is not a valid project.properties file");
        }

        if (p.getProperty(ConfigOption.PROJECT_NAME.getName()) == null)
            p.setProperty(ConfigOption.PROJECT_NAME.getName(), f.getParentFile().getName());

        String parent = f.getParentFile().getAbsolutePath();
        debug("Project dir URI: " + f.getParentFile().toURI());
        
        p.setProperty(ConfigOption.PROJECT_BTS_URL.getName(),
                "bugzilla-xml://" + parent + File.separator + "bugs");
        p.setProperty(ConfigOption.PROJECT_ML_URL.getName(), 
                "maildir://" + parent + File.separator + "mail");

        for (File file: infoFile.listFiles()) {
            if (!file.isDirectory())
                continue;
            
            if(file.getName().equals("svn")) {
                p.setProperty(ConfigOption.PROJECT_SCM_URL.getName(),
                        "svn-file:///" + parent + File.separator +"svn");
            } else if (file.getName().equals("git")) {
                p.setProperty(ConfigOption.PROJECT_SCM_URL.getName(),
                        "git-file:///" + parent + File.separator + "git");
            }
        }
    }
}
