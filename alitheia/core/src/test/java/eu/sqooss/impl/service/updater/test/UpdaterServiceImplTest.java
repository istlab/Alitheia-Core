package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.annotation.Annotation;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class UpdaterServiceImplTest {

	private UpdaterServiceImpl impl;
	private Logger mockedLogger;
	
	@Test
    public void testRegisterUpdaterServiceNotDuplicate() {
		// -- When
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		impl.registerUpdaterService(MetadataUpdaterExtension2.class);
		// -- Then
		//2 info messages from startUp()
		//2 info messages from a correctly added services
		Mockito.verify(mockedLogger, Mockito.times(4)).info(Mockito.anyString());
    }
	
	@Test
    public void testRegisterUpdaterServiceDuplicate() {
		// -- When
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		// -- Then
		//2 info messages from startUp()
		//1 info message from a correctly added service
		//1 error message from an incorrectly added service
		Mockito.verify(mockedLogger, Mockito.times(3)).info(Mockito.anyString());
		Mockito.verify(mockedLogger).error(Mockito.anyString());
		
    }
	
	@Test
    public void testRegisterUpdaterServiceWithAnnotation() {
		// -- When
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		// -- Then
		//2 info messages from startUp()
		//1 info message from a correctly added service
		Mockito.verify(mockedLogger, Mockito.times(3)).info(Mockito.anyString());
    }
	
	@Test
    public void testRegisterUpdaterServiceWithoutAnnotation() {
		// -- When
		impl.registerUpdaterService(EmptyMetadataUpdaterExtension.class);
		// -- Then
		Mockito.verify(mockedLogger).error(Mockito.anyString());
    }
	
	@Test(expected = NullPointerException.class)
	/**
	 * TODO - This is actually a bug that requires fixing, we
	 * would expect startUp() to return false if it didn't start
	 * correctly. Instead it errors out with a NullPointerException.
	 */
	public void testStartUpWithoutLoggerError(){
		// -- Given
		impl.setInitParams(null, null);
		// -- When
		boolean b = impl.startUp();
		// -- Then
		assertEquals(false, b);
	}
	
	@Before
    public void setUp() {
		impl = new UpdaterServiceImpl();
		mockedLogger = Mockito.mock(Logger.class);
		impl.setInitParams(null, mockedLogger);
		mockStatic(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(Mockito.mock(AlitheiaCore.class));
		impl.startUp();
    }
 
    @After
    public void tearDown() {

    }
	
    private interface EmptyMetadataUpdaterExtension extends MetadataUpdater{}

    @Updater(
    		mnem = "TESTUPDATER"
    )
    private interface MetadataUpdaterExtension extends MetadataUpdater{}
    
    @Updater(
    		mnem = "TESTUPDATER2"
    )
    private interface MetadataUpdaterExtension2 extends MetadataUpdater{}
}
