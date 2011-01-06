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
    static long failid;
    static long successid;

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
        AdminAction fail = impl.create("blah");
        assertNull(fail);

        fail = impl.create("fail");
        assertNotNull(fail);
        ActionContainer ac = impl.liveactions().get(1L);
        assertNotNull(ac);
        assertEquals(-1, ac.end);

        assertEquals(AdminActionStatus.CREATED, fail.getStatus());
        assertNull(fail.errors());
        assertNull(fail.results());
        failid = fail.id();
    }
    
    @Test
    public void testExecute() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testShow() {
        AdminAction aa = impl.show(failid);
        assertNotNull(aa);
    }
    
    @Test
    public void testGC() {
        fail("Not yet implemented");
    }
}
