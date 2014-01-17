package eu.sqooss.service.db;

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
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.BugStatus.Status;
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
        assertEquals(0, sp.getConfigValues(configKey).size());
        assertEquals(null, sp.getConfigValue(configKey));
        
        // Add a configuration value
        sp.addConfigValue(configKey, configValue1);
        assertTrue(db.addRecord(sp));
        
        List<StoredProject> dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertEquals(configValue1, sp.getConfigValue(configKey));
        assertEquals(1, sp.getConfigValues(configKey).size());
        assertEquals(configValue1, sp.getConfigValues(configKey).iterator().next());
        
        // Add another configuration value
        sp.addConfigValue(configKey, configValue2);
        
        dbSpc = db.findObjectsByProperties(StoredProject.class, emptyMap);
        assertEquals(1, dbSpc.size());
        assertEquals(sp, dbSpc.get(0));
        
        sp = dbSpc.get(0);
        assertThat(sp.getConfigValue(configKey), either(is(configValue1)).or(is(configValue2)));
        assertEquals(2, sp.getConfigValues(configKey).size());
        assertTrue(sp.getConfigValues(configKey).contains(configValue1));
        assertTrue(sp.getConfigValues(configKey).contains(configValue2));
    }
    
    @Test
    public void testGetConfigValuesNonExistentKey() {
        StoredProject sp = new StoredProject("Test Project");
        
        assertEquals(0, sp.getConfigValues("NonExistentKey").size());
        assertEquals(null, sp.getConfigValue("NonExistentKey"));
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
        assertEquals(null, sp.getConfigValue(configOpt));
        
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
    public void setConfigValue() {
        StoredProject sp = new StoredProject("Test Project");
        ConfigOption configOpt = ConfigOption.PROJECT_SCM_URL;
        String configKey = configOpt.getName();
        String configValue1 = "Test value";
        String configValue2 = "Other value";
        
        // FIXME: If we don't store the project, many of the methods fail. This shouldn't be necessary.
        assertTrue(db.addRecord(sp));
        
        // There shouldn't be any values associated with this configuration
        assertEquals(null, sp.getConfigValue(configOpt));
        
        // Add a configuration value
        sp.setConfigValue(configKey, configValue1);
        assertEquals(configValue1, sp.getConfigValue(configKey));
        
        // Change configuration value
        sp.setConfigValue(configKey, configValue2);
        assertEquals(configValue2, sp.getConfigValue(configKey));
    }
    
    @Test
    public void testGetterSetters() {
        StoredProject sp = new StoredProject();
        
        final ProjectVersion version1 = new ProjectVersion();
        final ProjectVersion version2 = new ProjectVersion();
        version1.setCommitMsg("Version1");
        version2.setCommitMsg("Version2");
        final List<ProjectVersion> projectVersions = Arrays.asList(version1, version2);
        
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
        
        assertTrue(db.addRecord(clusterNode));
        assertTrue(db.addRecord(sp));
        
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
    
    @Test
    public void testGetProjectByName() {
        StoredProject sp = new StoredProject("Test Project");
        assertTrue(db.addRecord(sp));
        
        assertEquals(sp, StoredProject.getProjectByName(db, "Test Project"));
        
        assertEquals(null, StoredProject.getProjectByName(db, "Non existend project"));
    }
    
    @Test
    public void testProjectCount() {
        assertEquals(0, StoredProject.getProjectCount(db));
        
        StoredProject sp = new StoredProject("Test Project");
        assertTrue(db.addRecord(sp));
        assertEquals(1, StoredProject.getProjectCount(db));
        
        sp = new StoredProject("Other Project");
        assertTrue(db.addRecord(sp));
        assertEquals(2, StoredProject.getProjectCount(db));
    }
    
    @Test
    public void testGetVersionsCount() {
        StoredProject sp = new StoredProject("Test Project");
        assertEquals(0, sp.getVersionsCount());
        
        final ProjectVersion version1 = new ProjectVersion();
        final ProjectVersion version2 = new ProjectVersion();
        version1.setProject(sp);
        version2.setProject(sp);
        version1.setCommitMsg("Version1");
        version2.setCommitMsg("Version2");
        
        sp.getProjectVersions().add(version1);
        assertEquals(1, sp.getVersionsCount());
        
        sp.getProjectVersions().add(version2);
        assertEquals(2, sp.getVersionsCount());
    }
    
    @Test
    public void testGetMailsCount() {
        StoredProject sp = new StoredProject("Test Project");
        assertTrue(db.addRecord(sp));
        assertEquals(0, sp.getMailsCount(db));
        
        final MailingList mailingList1 = new MailingList();
        final MailingList mailingList2 = new MailingList();
        mailingList1.setStoredProject(sp);
        mailingList2.setStoredProject(sp);
        final MailMessage mailMessage1 = new MailMessage();
        final MailMessage mailMessage2 = new MailMessage();
        final MailMessage mailMessage3 = new MailMessage();
        mailMessage1.setSubject("message 1");
        mailMessage1.setList(mailingList1);
        mailMessage2.setSubject("message 2");
        mailMessage2.setList(mailingList1);
        mailMessage3.setSubject("message 3");
        mailMessage3.setList(mailingList2);
        mailingList1.setMessages(new HashSet<>(Arrays.asList(mailMessage1, mailMessage2)));
        mailingList2.setMessages(new HashSet<>(Arrays.asList(mailMessage3)));
        
        sp.getMailingLists().add(mailingList1);
        assertEquals(2, sp.getMailsCount(db));
        
        sp.getMailingLists().add(mailingList2);
        assertEquals(3, sp.getMailsCount(db));
    }
    
    @Test
    public void testGetBugsCount() {
        StoredProject sp = new StoredProject("Test Project");
        assertTrue(db.addRecord(sp));
        assertEquals(0, sp.getBugsCount(db));
        
        final BugStatus bugStatusNew = new BugStatus();
        final BugStatus bugStatusAssigned = new BugStatus();
        bugStatusNew.setBugStatus(Status.NEW);
        bugStatusAssigned.setBugStatus(Status.ASSIGNED);
        final Bug bug1 = new Bug();
        final Bug bug2 = new Bug();
        final Bug bug3 = new Bug();
        bug1.setProject(sp);
        bug2.setProject(sp);
        bug3.setProject(sp);
        bug1.setStatus(bugStatusNew);
        bug2.setStatus(bugStatusAssigned);
        bug3.setStatus(bugStatusNew);
        
        sp.getBugs().add(bug1);
        assertEquals(1, sp.getBugsCount(db));
        
        sp.getBugs().add(bug2);
        assertEquals(1, sp.getBugsCount(db));
        
        sp.getBugs().add(bug3);
        assertEquals(2, sp.getBugsCount(db));
    }
    
    @Test
    public void testIsEvaluated() {
        // TODO: needs testing
    }
}
