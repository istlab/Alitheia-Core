package eu.sqoooss.admin.impl;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
    public void testCreate() {
        fail("Not yet implemented");
    }
    
}
