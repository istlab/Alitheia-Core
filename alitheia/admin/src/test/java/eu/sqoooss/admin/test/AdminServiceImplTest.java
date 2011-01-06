package eu.sqoooss.admin.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqoooss.admin.impl.AdminServiceImpl;
import eu.sqoooss.admin.impl.AdminServiceImpl.ActionContainer;
import eu.sqooss.admin.AdminAction;
import eu.sqooss.admin.AdminAction.AdminActionStatus;
import eu.sqooss.admin.actions.RunTimeInfo;

public class AdminServiceImplTest {

    static AdminServiceImpl impl;

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
    public void testGetAdminActions() {
        Set<AdminAction> actions = impl.getAdminActions();
        for (AdminAction aa : actions)
            assertNotNull (aa);
    }
    
    @Test
    public void testCreate() {
        AdminAction aa = impl.create("blah");
        assertNull(aa);

        aa = impl.create("fail");
        assertNotNull(aa);
        ActionContainer ac = impl.liveactions().get(1L);
        assertNotNull(ac);
        assertEquals(-1, ac.end);
        
        assertEquals(AdminActionStatus.CREATED, aa.getStatus());
        assertNull(aa.errors());
        assertNull(aa.results());
    }

    @Test
    public void testShow() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testExecute() {
        
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
