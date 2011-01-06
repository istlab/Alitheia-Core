package eu.sqoooss.admin.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqoooss.admin.impl.AdminServiceImpl;
import eu.sqooss.admin.AdminAction;
import eu.sqooss.admin.AdminAction.AdminActionStatus;
import eu.sqooss.admin.AdminService;
import eu.sqooss.admin.actions.RunTimeInfo;

public class AdminServiceImplTest {

    static AdminService impl;

    @BeforeClass
    public static void setUp() {
        impl = new AdminServiceImpl();
    }

    @Test
    public void testAdminServiceImpl() {
        assertNotNull(impl);
    }

    @Test
    public void testRegisterAdminAction() {
        RunTimeInfo rti = new RunTimeInfo();
        impl.registerAdminAction(rti.getMnemonic(), RunTimeInfo.class);
        assertEquals(1, impl.getAdminActions().size());

        FailingAction fa = new FailingAction();
        impl.registerAdminAction(fa.getMnemonic(), FailingAction.class);
        assertEquals(2, impl.getAdminActions().size());

        SucceedingAction su = new SucceedingAction();
        impl.registerAdminAction(su.getMnemonic(), SucceedingAction.class);
        assertEquals(3, impl.getAdminActions().size());
    }

    @Test
    public void testCreate() {
        AdminAction aa = impl.create("blah");
        assertNull(aa);

        aa = impl.create("fail");
        assertNotNull(aa);
        
        assertEquals(AdminActionStatus.CREATED, aa.getStatus());
    }

    @Test
    public void testExecute() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAdminActions() {
        fail("Not yet implemented");
    }

    @Test
    public void testShow() {
        fail("Not yet implemented");
    }

    @Test
    public void testResult() {
        fail("Not yet implemented");
    }

    @Test
    public void testStatus() {
        fail("Not yet implemented");
    }

    @Test
    public void testError() {
        fail("Not yet implemented");
    }
}
