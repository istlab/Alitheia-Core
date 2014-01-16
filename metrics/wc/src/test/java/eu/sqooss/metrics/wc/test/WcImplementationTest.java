package eu.sqooss.metrics.wc.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.metrics.wc.WcImplementation;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DBService.class, Metric.class, WcImplementation.class, FileTypeMatcher.class, ProjectVersion.class, StoredProject.class, ProjectFileState.class})
public class WcImplementationTest {
 @Mock(name = "db") private DBService dbMock; 
 @Mock(name = "ftm") private FileTypeMatcher ftmMock;
 @Mock(name = "fds") private FDSService fdsMock;
 @Mock(name = "log") private Logger logMock; 
 
 
 @InjectMocks private WcImplementation metricsInstance;

 @Before public void initMocks() 
 	{
        MockitoAnnotations.initMocks(this);
    }

 	@Test 
   	public void runWithProjectFileAsParamTest() throws Exception   {

	   //Setup the metric class for testing
	   PowerMockito.mockStatic(Metric.class);
	   PowerMockito.mockStatic(WcImplementation.class);
	   
	   
	   //mock the getMetricByMnemonic method
	   Mockito.when(Metric.getMetricByMnemonic(anyString())).thenReturn(mock(Metric.class));
 
       //spy on the metricsInstance to be able to mock private methods
	   WcImplementation spy = PowerMockito.spy(metricsInstance);
       
       //Mock the addRecord method from the DBService
       PowerMockito.when(dbMock.addRecord(any(DAObject.class))).thenReturn(true);
       //Mock the FiletypMatcher gettype method
       PowerMockito.when(ftmMock.getFileType(anyString())).thenReturn(FileTypeMatcher.FileType.DOC);
       //Mock the FDSService getFileContents method
       PowerMockito.when(fdsMock.getFileContents(any(ProjectFile.class))).thenReturn(mock(InputStream.class));
       
       //Mock the static processStreamclass from WcImplementation
       int[] expected = new int[4];
       expected[0] = 1;
       expected[1] = 1;
       expected[2] = 1;
       expected[3] = 1;   
       PowerMockito.when(WcImplementation.processStream(anyString(), any(InputStream.class))).thenReturn(expected);
       
       //Verify that none of the methods have been called at the start of the test           
       PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));
       PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
  	 
       //Execute the method to be tested
       spy.run(mock(ProjectFile.class));
       
       
       //Get the static strings from the class for verification of the metric calls
       String MNEMONIC_WC_LONB  = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_LONB");
       String MNEMONIC_WC_LOCOM = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_LOCOM");
       String MNEMONIC_WC_WORDS = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_WORDS");
       String MNEMONIC_WC_LOC = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_LOC");
      
       //Check if the  getMetricCalls have been performed
       PowerMockito.verifyPrivate(Metric.class,times(4)).invoke("getMetricByMnemonic", anyString());

       //Check if these calls were done with the right parameters
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_LONB));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_LOCOM));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_WORDS));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_LOC));
       
       //Check if indeed all records are added to the database
       PowerMockito.verifyPrivate(dbMock,times(4)).invoke("addRecord", any(DAObject.class));   
   }
 	
 	@Test 
   	public void runWithProjectVersionAsParamTest() throws Exception   {

 	   //Setup the metric class for testing
 	   PowerMockito.mockStatic(Metric.class);
 	   PowerMockito.mockStatic(WcImplementation.class);
 	   PowerMockito.mockStatic(ProjectVersion.class);
	   PowerMockito.mockStatic(ProjectFileState.class);
 	   //mock the getMetricByMnemonic method
 	   Mockito.when(Metric.getMetricByMnemonic(anyString())).thenReturn(mock(Metric.class));

        //spy on the metricsInstance to be able to mock private methods
 	   WcImplementation spy = PowerMockito.spy(metricsInstance);
        
        //Mock the addRecord method from the DBService
        PowerMockito.when(dbMock.addRecord(any(DAObject.class))).thenReturn(true);
        //Mock the FiletypMatcher gettype method
        PowerMockito.when(ftmMock.getFileType(anyString())).thenReturn(FileTypeMatcher.FileType.DOC);
        //Mock the FDSService getFileContents method
        PowerMockito.when(fdsMock.getFileContents(any(ProjectFile.class))).thenReturn(mock(InputStream.class));
        
        //Mock the static processStreamclass from WcImplementation
        int[] expected = new int[4];
        expected[0] = 1;
        expected[1] = 1;
        expected[2] = 1;
        expected[3] = 1;   
        PowerMockito.when(WcImplementation.processStream(anyString(), any(InputStream.class))).thenReturn(expected);
        
        //Verify that none of the methods have been called at the start of the test           
        PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));
        PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
   	 
        
        //Mock the projectversion param 
        StoredProject mockedProject = mock(StoredProject.class);
        ProjectVersion version = mock(ProjectVersion.class);
        PowerMockito.when(version.getSequence()).thenReturn(100L);
        PowerMockito.when(version.getProject()).thenReturn(mockedProject);
        PowerMockito.when(mockedProject.getId()).thenReturn(100L);
        Mockito.when(ProjectVersion.getLastProjectVersion(any(StoredProject.class))).thenReturn(version);
        //Execute the method to be tested
        spy.run(version);
        
        
        //Get the static strings from the class for verification of the metric calls
        String MNEMONIC_WC_PV_NODF  = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_NODF");
        String MNEMONIC_WC_PV_NOF = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_NOF");
        String MNEMONIC_WC_PV_NOSF = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_NOSF");
        String MNEMONIC_WC_PV_TL = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_TL");
        String MNEMONIC_WC_PV_TLDOC = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_TLDOC");
        String MNEMONIC_WC_PV_TLOC = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_TLOC");
        String MNEMONIC_WC_PV_TLOCOM = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_PV_TLOCOM");
        String MNEMONIC_WC_LOCOM = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_LOCOM");
        String MNEMONIC_WC_LOC = Whitebox.<String>getInternalState(WcImplementation.class, "MNEMONIC_WC_LOC");
        
        
        //Check if the  getMetricCalls have been performed
        PowerMockito.verifyPrivate(Metric.class,times(9)).invoke("getMetricByMnemonic", anyString());

        //Check if these calls were done with the right parameters
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_NODF));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_NOF));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_NOSF));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_TL));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_TLDOC));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_TLOC));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_PV_TLOCOM));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_LOCOM));
        PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEMONIC_WC_LOC ));
        
        //Check if indeed all records are added to the database
        PowerMockito.verifyPrivate(dbMock,times(7)).invoke("addRecord", any(DAObject.class)); 
   }
 	 	
	
}
