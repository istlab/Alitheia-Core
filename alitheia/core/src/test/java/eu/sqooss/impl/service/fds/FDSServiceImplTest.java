package eu.sqooss.impl.service.fds;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.fds.FDSServiceImpl;

public class FDSServiceImplTest {

    static FDSServiceImpl impl;

    @BeforeClass
    public static void setUp() {
        impl = new FDSServiceImpl();
    }

    @Test
    public void testFDSServiceImpl() {
        assertNotNull(impl);
    }

//     @Test
//     public void testRegisterAdminAction() {
//         RunTimeInfo rti = new RunTimeInfo();
//         impl.registerAdminAction(rti.mnemonic(), RunTimeInfo.class);
//         assertEquals(1, impl.getAdminActions().size());
// 
//         FailingAction fa = new FailingAction();
//         impl.registerAdminAction(fa.mnemonic(), FailingAction.class);
//         assertEquals(2, impl.getAdminActions().size());
// 
//         SucceedingAction su = new SucceedingAction();
//         impl.registerAdminAction(su.mnemonic(), SucceedingAction.class);
//         assertEquals(3, impl.getAdminActions().size());
//     }
// 
//     @Test
//     public void testGetAdminActions() {
//         Set<AdminAction> actions = impl.getAdminActions();
//         for (AdminAction aa : actions)
//             assertNotNull (aa);
//     }
//     
//     @Test
//     public void testCreate() {
//         AdminAction fail = impl.create("blah");
//         assertNull(fail);
// 
//         fail = impl.create("fail");
//         assertNotNull(fail);
//         ActionContainer ac = impl.liveactions().get(1L);
//         assertNotNull(ac);
//         assertEquals(-1, ac.end);
// 
//         assertEquals(AdminActionStatus.CREATED, fail.status());
//         assertNull(fail.errors());
//         assertNull(fail.results());
//         failid = fail.id();
//     }
}
