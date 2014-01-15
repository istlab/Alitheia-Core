package eu.sqooss.impl.service.updater.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class UpdaterServiceImplTest {

	private UpdaterServiceImpl impl;
	private Logger mockedLogger;
	private AlitheiaCore core;
	
	@Test
    public void testGetUpdatersByStage() throws InvalidAccessorException, URISyntaxException {
		// -- Given
		impl.registerUpdaterService(MetadataUpdaterExtension2.class);
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		TDSService tdss = Mockito.mock(TDSService.class);
		ProjectAccessor pa = Mockito.mock(ProjectAccessor.class);
		Mockito.when(core.getTDSService()).thenReturn(tdss);
		Mockito.when(tdss.getAccessor(Mockito.anyLong())).thenReturn(pa);
		Mockito.when(pa.getSCMAccessor()).thenReturn(Mockito.mock(SCMAccessor.class));
		Mockito.when(pa.getBTSAccessor()).thenReturn(Mockito.mock(BTSAccessor.class));
		Mockito.when(pa.getMailAccessor()).thenReturn(Mockito.mock(MailAccessor.class));
		// -- When
		Set<Updater> updaters = impl.getUpdaters(Mockito.mock(StoredProject.class), UpdaterStage.PARSE);
		// -- Then
		assertEquals(1, updaters.size());
		assertEquals(MetadataUpdaterExtension.class.getAnnotation(Updater.class), updaters.iterator().next());
    }
	
	@Test
    public void testGetUpdatersReturnsURIs() throws InvalidAccessorException, URISyntaxException {
		// -- Given
		impl.registerUpdaterService(MetadataUpdaterExtension2.class);
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		TDSService tdss = Mockito.mock(TDSService.class);
		ProjectAccessor pa = Mockito.mock(ProjectAccessor.class);
		SCMAccessor scma = Mockito.mock(SCMAccessor.class);
		Mockito.when(core.getTDSService()).thenReturn(tdss);
		Mockito.when(tdss.getAccessor(Mockito.anyLong())).thenReturn(pa);
		Mockito.when(pa.getSCMAccessor()).thenReturn(scma);
		Mockito.when(pa.getBTSAccessor()).thenReturn(Mockito.mock(BTSAccessor.class));
		Mockito.when(pa.getMailAccessor()).thenReturn(Mockito.mock(MailAccessor.class));
		Mockito.when(scma.getSupportedURLSchemes()).thenAnswer(new Answer<List<URI>>() {
				     public List<URI> answer(InvocationOnMock invocation) throws Throwable {
				    	 ArrayList<URI> list = new ArrayList<URI>();
				    	 list.add(new URI("testscheme","a","b"));
				         return list;
				     }
			     });
		// -- When
		Set<Updater> updaters = impl.getUpdaters(Mockito.mock(StoredProject.class));
		// -- Then
		assertEquals(1, updaters.size());
		assertEquals(MetadataUpdaterExtension.class.getAnnotation(Updater.class), updaters.iterator().next());
    }
	
	@Test
    public void testGetUpdatersWithNoMailAccessor() throws InvalidAccessorException {
		// -- Given
		TDSService tdss = Mockito.mock(TDSService.class);
		ProjectAccessor pa = Mockito.mock(ProjectAccessor.class);
		Mockito.when(core.getTDSService()).thenReturn(tdss);
		Mockito.when(tdss.getAccessor(Mockito.anyLong())).thenReturn(pa);
		Mockito.when(pa.getSCMAccessor()).thenReturn(Mockito.mock(SCMAccessor.class));
		Mockito.when(pa.getBTSAccessor()).thenReturn(Mockito.mock(BTSAccessor.class));
		Mockito.when(pa.getMailAccessor()).thenThrow(Mockito.mock(InvalidAccessorException.class));
		// -- When
		impl.getUpdaters(Mockito.mock(StoredProject.class));
		// -- Then
		Mockito.verify(mockedLogger).warn(Mockito.anyString());
    }
	
	@Test
    public void testUpdateWithNonExistentProject() throws InvalidAccessorException {
		// -- Given
		TDSService tdss = Mockito.mock(TDSService.class);
		ProjectAccessor pa = Mockito.mock(ProjectAccessor.class);
		Mockito.when(core.getTDSService()).thenReturn(tdss);
		Mockito.when(tdss.getAccessor(Mockito.anyLong())).thenReturn(pa);
		Mockito.when(pa.getSCMAccessor()).thenReturn(Mockito.mock(SCMAccessor.class));
		Mockito.when(pa.getBTSAccessor()).thenReturn(Mockito.mock(BTSAccessor.class));
		Mockito.when(pa.getMailAccessor()).thenReturn(Mockito.mock(MailAccessor.class));
		// -- When
		boolean b = impl.update(Mockito.mock(StoredProject.class), MetadataUpdaterExtension.class.getAnnotation(Updater.class));
		// -- Then
		assertEquals(false, b);
    }
	
	@Test
    public void testUpdateWithoutUpdater() {
		// -- When
		boolean b = impl.update(null, MetadataUpdaterExtension.class.getAnnotation(Updater.class).mnem());
		// -- Then
		//1 warning for no updater being registered
		Mockito.verify(mockedLogger).warn(Mockito.anyString());
		assertEquals(false, b);
    }
	
	@Test
    public void testUnRegisterUpdaterService() {
		// -- Given
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		// -- When
		impl.unregisterUpdaterService(MetadataUpdaterExtension.class);
		// -- Then
		//2 info messages from startUp()
		//1 info message from a correctly registered service
		//1 info message from a correctly unregistered service
		Mockito.verify(mockedLogger, Mockito.times(4)).info(Mockito.anyString());
    }
	
	@Test
    public void testRegisterUpdaterServiceNotDuplicate() {
		// -- Given
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		// -- When
		impl.registerUpdaterService(MetadataUpdaterExtension2.class);
		// -- Then
		//2 info messages from startUp()
		//2 info messages from a correctly added services
		Mockito.verify(mockedLogger, Mockito.times(4)).info(Mockito.anyString());
    }
	
	@Test
    public void testRegisterUpdaterServiceDuplicate() {
		// -- Given
		impl.registerUpdaterService(MetadataUpdaterExtension.class);
		// -- When
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
	
	@Test
	public void testShutdown(){
		// -- When
		impl.shutDown();
		// -- Then
		//Nothing happens :)
	}
	
	@Before
    public void setUp() throws InvalidAccessorException {
		impl = new UpdaterServiceImpl();
		//Set up logger
		mockedLogger = Mockito.mock(Logger.class);
		impl.setInitParams(null, mockedLogger);
		//Mock AlitheiaCore
		mockStatic(AlitheiaCore.class);
		core = Mockito.mock(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(core);
		//Start the UpdaterService with the fake core
		impl.startUp();
    }
 
    @After
    public void tearDown() {

    }
	
    private interface EmptyMetadataUpdaterExtension extends MetadataUpdater{}

    @Updater(
    		mnem = "TESTUPDATER",
    		protocols = {"testscheme","testschemeb"},
    		stage = UpdaterStage.PARSE
    )
    private interface MetadataUpdaterExtension extends MetadataUpdater{}
    
    @Updater(
    		mnem = "TESTUPDATER2",
    	    protocols = {"testschemec","testschemed"},
    	    stage = UpdaterStage.IMPORT
    )
    private interface MetadataUpdaterExtension2 extends MetadataUpdater{}
}
