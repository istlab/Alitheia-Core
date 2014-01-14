package eu.sqooss.service.admin.actions.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.service.admin.AdminAction.AdminActionStatus;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.test.TestDAObject;

public class TestAddProject extends TestDAObject {
    TDSService tds;

    @Before
    public void setUpTest() {
        tds = new TDSServiceImpl(db, l);
        tds.startUp();
    }

    @After
    public void tearDownTest() {
        tds.shutDown();
    }

    /** Test to make sure the mnemonic isn't changed by accident */
    @Test
    public void testMnemonic() {
        AddProject action = new AddProject(db, tds);
        assertEquals("addpr", action.mnemonic());
    }

    @Test
    public void noAccessors() throws Exception {
        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds.unsupported.url"));
        }
    }

    @Test
    public void UnknownSCMAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds.unsupported.url"));
        }
    }

    @Test
    public void unknownMailAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds.unsupported.url"));
        }
    }

    @Test
    public void unknownBTSAccessor() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bugzilla-path");
        action.addArg("scm", "test-scm-acc://svn-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds.unsupported.url"));
        }
    }

    @Test
    public void testNoNameArgument() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        assertEquals(AdminActionStatus.CREATED, action.status());

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("missing.param"));
        }
    }

    @Test
    public void testNoScmArgument() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        assertEquals(AdminActionStatus.CREATED, action.status());

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("missing.param"));
        }
    }

    @Test
    public void testIncorrectNameArgument() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        for(String name : Arrays.asList(null, "", "   ")) {
            AddProject action = new AddProject(db, tds);

            action.addArg("name", name);
            action.addArg("bts", "test-bts-acc://bts-path");
            action.addArg("scm", "test-scm-acc://scm-path");
            action.addArg("mail", "test-mail-acc://mail-path");
            action.addArg("contact", "maintainer@isp.com");
            action.addArg("web", "awesome.project.org");

            assertEquals(AdminActionStatus.CREATED, action.status());

            try {
                action.execute();
                fail("Should never be reached, exception should have been thrown!");
            } catch (Exception e) {
                assertEquals(AdminActionStatus.ERROR, action.status());
                assertEquals(1, action.errors().size());
                assertTrue(action.errors().containsKey("missing.param"));
            }
        }
    }

    @Test
    public void testIncorrectScmArgument() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        for(String scm : Arrays.asList(null, "", "   ")) {
            AddProject action = new AddProject(db, tds);

            action.addArg("name", "Test Name");
            action.addArg("bts", "test-bts-acc://bts-path");
            action.addArg("scm", scm);
            action.addArg("mail", "test-mail-acc://mail-path");
            action.addArg("contact", "maintainer@isp.com");
            action.addArg("web", "awesome.project.org");

            assertEquals(AdminActionStatus.CREATED, action.status());

            try {
                action.execute();
                fail("Should never be reached, exception should have been thrown!");
            } catch (Exception e) {
                assertEquals(AdminActionStatus.ERROR, action.status());
                assertEquals(1, action.errors().size());
                assertTrue(action.errors().containsKey("missing.param"));
            }
        }
    }

    @Test
    public void testAllArgs() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        action.execute();
        assertEquals(AdminActionStatus.FINISHED, action.status());

        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        assertEquals(1, projects.size());

        StoredProject sp = projects.get(0);
        assertEquals("Test Name", sp.getName());
        assertEquals("test-bts-acc://bts-path", sp.getBtsUrl());
        assertEquals("test-scm-acc://scm-path", sp.getScmUrl());
        assertEquals("test-mail-acc://mail-path", sp.getMailUrl());
        assertEquals("maintainer@isp.com", sp.getContactUrl());
        assertEquals("awesome.project.org", sp.getWebsiteUrl());

        ProjectAccessor acc = tds.getAccessor(sp.getId());
        assertEquals("Test Name", acc.getName());
        assertEquals(sp.getId(), (long)acc.getId());
    }

    @Test
    public void testMultipleAddsEntries() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Super Super Project");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer1@isp.com maintainer2@isp.com");
        action.addArg("web", "awesome.project.org super.awesome.project.org");

        action.execute();
        assertEquals(AdminActionStatus.FINISHED, action.status());

        action = new AddProject(db, tds);

        action.addArg("name", "Project 2");
        action.addArg("bts", "test-bts-acc://bts-path2");
        action.addArg("scm", "test-scm-acc://scm-path2");
        action.addArg("mail", "test-mail-acc://mail-path2");
        action.addArg("contact", "other-maintainer@isp.com");
        action.addArg("web", "other-project.org");

        action.execute();
        assertEquals(AdminActionStatus.FINISHED, action.status());

        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        assertEquals(2, projects.size());

        StoredProject sp = projects.get(0);
        assertEquals("Super Super Project", sp.getName());
        assertEquals("test-bts-acc://bts-path", sp.getBtsUrl());
        assertEquals("test-scm-acc://scm-path", sp.getScmUrl());
        assertEquals("test-mail-acc://mail-path", sp.getMailUrl());
        assertThat(sp.getContactUrl(), JUnitMatchers.either(is("maintainer1@isp.com")).or(is("maintainer2@isp.com")));
        assertThat(sp.getWebsiteUrl(), JUnitMatchers.either(is("awesome.project.org")).or(is("super.awesome.project.org")));

        ProjectAccessor acc = tds.getAccessor(sp.getId());
        assertEquals("Super Super Project", acc.getName());
        assertEquals(sp.getId(), (long)acc.getId());

        sp = projects.get(1);
        assertEquals("Project 2", sp.getName());
        assertEquals("test-bts-acc://bts-path2", sp.getBtsUrl());
        assertEquals("test-scm-acc://scm-path2", sp.getScmUrl());
        assertEquals("test-mail-acc://mail-path2", sp.getMailUrl());
        assertEquals("other-maintainer@isp.com", sp.getContactUrl());
        assertEquals("other-project.org", sp.getWebsiteUrl());

        acc = tds.getAccessor(sp.getId());
        assertEquals("Project 2", acc.getName());
        assertEquals(sp.getId(), (long)acc.getId());
    }

    @Test
    public void testDuplicateEntry() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Super Project");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer1@isp.com maintainer2@isp.com");
        action.addArg("web", "awesome.project.org super.awesome.project.org");

        action.execute();
        assertEquals(AdminActionStatus.FINISHED, action.status());

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("project.exists"));
        }

        action = new AddProject(db, tds);

        action.addArg("name", "Super Project");
        action.addArg("bts", "test-bts-acc://other-bts-path");
        action.addArg("scm", "test-scm-acc://other-scm-path");
        action.addArg("mail", "test-mail-acc://other-mail-path");
        action.addArg("contact", "new-maintainer@isp.com");
        action.addArg("web", "www.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("project.exists"));
        }
    }

    @Test
    public void testMissingBTSURL() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-mail-acc"}, TestMailAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("mail", "test-mail-acc://mail-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds"));
        }
        /*
        assertEquals(AdminActionStatus.FINISHED, action.status());

        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        assertEquals(1, projects.size());

        StoredProject sp = projects.get(0);
        assertEquals("Test Name", sp.getName());
        assertEquals("test-scm-acc://scm-path", sp.getScmUrl());
        assertEquals("test-mail-acc://mail-path", sp.getMailUrl());
        assertEquals("maintainer@isp.com", sp.getContactUrl());
        assertEquals("awesome.project.org", sp.getWebsiteUrl());
        */
    }

    @Test
    public void testMissingMailURL() throws Exception {
        tds.registerPlugin(new String[]{"test-scm-acc"}, TestSCMAccessorImp.class);
        tds.registerPlugin(new String[]{"test-bts-acc"}, TestBTSAccessorImp.class);

        AddProject action = new AddProject(db, tds);

        action.addArg("name", "Test Name");
        action.addArg("bts", "test-bts-acc://bts-path");
        action.addArg("scm", "test-scm-acc://scm-path");
        action.addArg("contact", "maintainer@isp.com");
        action.addArg("web", "awesome.project.org");

        try {
            action.execute();
            fail("Should never be reached, exception should have been thrown!");
        } catch (Exception e) {
            assertEquals(AdminActionStatus.ERROR, action.status());
            assertEquals(1, action.errors().size());
            assertTrue(action.errors().containsKey("tds"));
        }
        /*
        assertEquals(AdminActionStatus.FINISHED, action.status());

        List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, new HashMap<String,Object>());
        assertEquals(1, projects.size());

        StoredProject sp = projects.get(0);
        assertEquals("Test Name", sp.getName());
        assertEquals("test-bts-acc://bts-path", sp.getBtsUrl());
        assertEquals("test-scm-acc://scm-path", sp.getScmUrl());
        assertEquals("maintainer@isp.com", sp.getContactUrl());
        assertEquals("awesome.project.org", sp.getWebsiteUrl());
        */
    }
}
