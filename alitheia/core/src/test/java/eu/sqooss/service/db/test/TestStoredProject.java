package eu.sqooss.service.db.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.test.TestDAObject;

public class TestStoredProject extends TestDAObject {
    static final Map<String, Object> emptyMap = Collections.emptyMap();
    
    @Test
    public void addGetConfigValueFromConfigName() {
        StoredProject sp = new StoredProject("Test Project");
        ConfigOption configOpt = ConfigOption.PROJECT_SCM_URL;
        String configKey = configOpt.getName();
        String configValue1 = "Test value";
        String configValue2 = "Other value";
        
        // FIXME: If we don't store the project, many of the methods fail. This shouldn't be necessary.
        assertTrue(db.addRecord(sp));
        
        // There shouldn't be any values associated with this configuration
        assertEquals(0, sp.getConfigValues(db, configKey).size());
        
        // Add a configuration value
        sp.addConfigValue(db, configKey, configValue1);
        assertTrue(db.addRecord(sp));
        
        List<StoredProject> dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertEquals(configValue1, sp.getConfigValue(db, configKey));
        assertEquals(1, sp.getConfigValues(db, configKey).size());
        assertEquals(configValue1, sp.getConfigValues(db, configKey).get(0));
        
        // Add another configuration value
        sp.addConfigValue(db, configKey, configValue2);
        
        dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertThat(sp.getConfigValue(db, configKey), either(is(configValue1)).or(is(configValue2)));
        assertEquals(2, sp.getConfigValues(db, configKey).size());
        assertTrue(sp.getConfigValues(db, configKey).contains(configValue1));
        assertTrue(sp.getConfigValues(db, configKey).contains(configValue2));
    }
    
    @Test
    public void addGetConfigValueFromConfigOption() {
        StoredProject sp = new StoredProject("Test Project");
        ConfigOption configOpt = ConfigOption.PROJECT_SCM_URL;
        String configValue1 = "Test value";
        String configValue2 = "Other value";
        
        // FIXME: If we don't store the project, many of the methods fail. This shouldn't be necessary.
        assertTrue(db.addRecord(sp));
        
        // There shouldn't be any values associated with this configuration
        assertEquals(0, sp.getConfigValues(configOpt).size());
        
        // Add a configuration value
        sp.addConfig(configOpt, configValue1);
        assertTrue(db.addRecord(sp));
        
        List<StoredProject> dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertEquals(configValue1, sp.getConfigValue(configOpt));
        assertEquals(1, sp.getConfigValues(configOpt).size());
        assertEquals(configValue1, sp.getConfigValues(configOpt).iterator().next());
        
        // Add another configuration value
        sp.addConfig(configOpt, configValue2);
        
        dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertThat(sp.getConfigValue(configOpt), either(is(configValue1)).or(is(configValue2)));
        assertEquals(2, sp.getConfigValues(configOpt).size());
        assertTrue(sp.getConfigValues(configOpt).contains(configValue1));
        assertTrue(sp.getConfigValues(configOpt).contains(configValue2));
    }
    
    @Test
    public void testGetterSetters() {
        StoredProject sp = new StoredProject();
        
        final List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion(sp), new ProjectVersion(sp));
        
        // Initialise developers
        final Developer dev1 = new Developer();
        final Developer dev2 = new Developer();
        dev1.setName("Dev1");
        dev2.setName("Dev2");
        final Set<Developer> developers = new HashSet<>();
        developers.addAll(Arrays.asList(dev1, dev2));
        
        final MailingList list1 = new MailingList();
        final MailingList list2 = new MailingList();
        list1.setListId("list1");
        list2.setListId("list2");
        final Set<MailingList> mailingLists = new HashSet<>();
        mailingLists.addAll(Arrays.asList(list1, list2));
        
        final StoredProjectMeasurement meas1 = new StoredProjectMeasurement();
        final StoredProjectMeasurement meas2 = new StoredProjectMeasurement();
        meas1.setResult("failure");
        meas2.setResult("success");
        final Set<StoredProjectMeasurement> measurements = new HashSet<>();
        measurements.addAll(Arrays.asList(meas1, meas2));
        
        final Branch branch1 = new Branch(sp, "branch1");
        final Branch branch2 = new Branch(sp, "branch2");
        final Set<Branch> branches = new HashSet<>();
        branches.addAll(Arrays.asList(branch1, branch2));
        
        final Bug bug1 = new Bug();
        final Bug bug2 = new Bug();
        bug1.setBugID("bugID1");
        bug2.setBugID("bugID2");
        final Set<Bug> bugs = new HashSet<>();
        bugs.addAll(Arrays.asList(bug1, bug2));
        
        final ClusterNode clusterNode = new ClusterNode("test server");
        
        sp.setName("Project Name");
        sp.setWebsiteUrl("www.test.org");
        sp.setContactUrl("https://contact.test.org");
        sp.setBtsUrl("bts://url");
        sp.setScmUrl("scmurl");
        sp.setMailUrl("mail://url");
        sp.setProjectVersions(projectVersions);
        sp.setDevelopers(developers);
        sp.setMailingLists(mailingLists);
        sp.setMeasurements(measurements);
        sp.setClusternode(clusterNode);
        sp.setBranches(branches);
        sp.setBugs(bugs);
        
        db.addRecord(clusterNode);
        db.addRecord(sp);
        
        List<StoredProject> dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        sp = dbSpc.get(0);
        
        assertEquals("Project Name", sp.getName());
        assertEquals("www.test.org", sp.getWebsiteUrl());
        assertEquals("https://contact.test.org", sp.getContactUrl());
        assertEquals("bts://url", sp.getBtsUrl());
        assertEquals("scmurl", sp.getScmUrl());
        assertEquals("mail://url", sp.getMailUrl());
        assertEquals(projectVersions, sp.getProjectVersions());
        assertEquals(developers, sp.getDevelopers());
        assertEquals(mailingLists, sp.getMailingLists());
        assertEquals(measurements, sp.getMeasurements());
        assertEquals(clusterNode, sp.getClusternode());
        assertEquals(branches, sp.getBranches());
        assertEquals(bugs, sp.getBugs());
    }
}
