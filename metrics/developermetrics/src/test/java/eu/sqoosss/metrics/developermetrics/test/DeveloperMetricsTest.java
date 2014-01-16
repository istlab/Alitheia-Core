package eu.sqoosss.metrics.developermetrics.test;

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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.developermetrics.Developermetrics;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class, Metric.class, Developermetrics.class, DBService.class, Directory.class})
public class DeveloperMetricsTest {
 @Mock(name = "db") private DBService dbMock; 
 @InjectMocks private Developermetrics metricsInstance;

 @Before public void initMocks() 
 	{
        MockitoAnnotations.initMocks(this);
    }

 	@Test 
   	public void runWithProjectVersionAsParamTest() throws Exception   {

	   //Setup the metric class for testing
	   PowerMockito.mockStatic(Metric.class);
	   
	   //mock the getmetric method
	   Mockito.when(Metric.getMetricByMnemonic(anyString())).thenReturn(mock(Metric.class));
 
       //spy on the metricsInstance to be able to mock private methods
       Developermetrics spy = PowerMockito.spy(metricsInstance);
       
       //Mock the private method commSize of Developermetrics
       PowerMockito.doReturn(1L).when(spy, "commSize", any(ProjectVersion.class), anyLong());
       
       //Mock the addRecord method from the DBService
       PowerMockito.when(dbMock.addRecord(any(DAObject.class))).thenReturn(true);
       
       //Verify that none of the methods have been called at the start of the test
       PowerMockito.verifyPrivate(spy, times(0)).invoke("commSize", any(ProjectVersion.class), anyLong());            
       PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));

       //Verify that none  the Metric class method has not been called before the start of the test
       PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
  	 
       //Execute the method to be tested
       spy.run(mock(ProjectVersion.class));
       
       //Check if the three getMetricCalls have been performed
       PowerMockito.verifyPrivate(Metric.class,times(3)).invoke("getMetricByMnemonic", anyString());
       
       
       //Get the static strings from the class for verification of the metric calls
       String MNEM_TEAMSIZE1  = Whitebox.<String>getInternalState(Developermetrics.class, "MNEM_TEAMSIZE1");
       String MNEM_TEAMSIZE3 = Whitebox.<String>getInternalState(Developermetrics.class, "MNEM_TEAMSIZE3");
       String MNEM_TEAMSIZE6 = Whitebox.<String>getInternalState(Developermetrics.class, "MNEM_TEAMSIZE6");
     		
       //Check if these calls were done with the right parameters
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEM_TEAMSIZE1));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEM_TEAMSIZE3));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEM_TEAMSIZE6));
       
       //check if three commSize calls have occurred
       PowerMockito.verifyPrivate(spy, times(3)).invoke("commSize", any(ProjectVersion.class), anyLong()); 

       long oneMonth = (long)(30 * 24 * 60 * 60 * 1000L);
       long threeMonths = (long)(90 * 24 * 60 * 60 * 1000L);
       long sixMonths = (long)(180 * 24 * 60 * 60 * 1000L);
     
       //Check if these three calls had the three different month parameters: one, three and six months
       PowerMockito.verifyPrivate(spy, times(1)).invoke("commSize", any(ProjectVersion.class), eq(oneMonth));  
       PowerMockito.verifyPrivate(spy, times(1)).invoke("commSize", any(ProjectVersion.class), eq(threeMonths));  
       PowerMockito.verifyPrivate(spy, times(1)).invoke("commSize", any(ProjectVersion.class), eq(sixMonths));  
       
       //Check if indeed three records are added to the database
       PowerMockito.verifyPrivate(dbMock,times(3)).invoke("addRecord", any(DAObject.class));     
   }
 	
	@Test 
   	public void runWithProjectFileAsParamWithNoDirectoryTest() throws Exception   {

	   //Setup the metric class for testing
	   PowerMockito.mockStatic(Metric.class);
	   
	   //mock the getmetric method
	   Mockito.when(Metric.getMetricByMnemonic(anyString())).thenReturn(mock(Metric.class));
 
       //spy on the metricsInstance to be able to mock private methods
       Developermetrics spy = PowerMockito.spy(metricsInstance);
       
       //Mock the private method commSize of Developermetrics
       PowerMockito.doReturn(1L).when(spy, "commSize", any(ProjectVersion.class), anyLong());
       
       //Mock the addRecord method from the DBService
       PowerMockito.when(dbMock.addRecord(any(DAObject.class))).thenReturn(true);
       
       //Verify that none of the methods have been called at the start of the test
       PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));

       //Verify that none  the Metric class method has not been called before the start of the test
       PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
  	 
       
		//Setup the projectVersion for testing purposes
		ProjectVersion v = new ProjectVersion();
		v.setTimestamp(1000);
		v.setProject(mock(StoredProject.class));
		
		//Setup the project file for testing purposes
		ProjectFile file = new ProjectFile();
		file.setId(1000);
		file.setProjectVersion(v);
		
       //Execute the method to be tested
       spy.run(file);
       
       //Get the static strings from the class for verification of the metric calls
       String MNEM_EYEBALL = Whitebox.<String>getInternalState(Developermetrics.class, "MNEM_EYEBALL");
     		
       //Check if the getMetricByMnemonic call was done with the right parameter
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEM_EYEBALL));
       
       //Check if indeed three records are added to the database
       PowerMockito.verifyPrivate(dbMock,times(1)).invoke("addRecord", any(DAObject.class));     
   }
	
	@Test 
   	public void runWithProjectFileAsParamWithADirectoryTest() throws Exception   {

	   //Setup the metric class for testing
	   PowerMockito.mockStatic(Metric.class);
	   
	   //mock the getmetric method
	   Mockito.when(Metric.getMetricByMnemonic(anyString())).thenReturn(mock(Metric.class));
 
       //spy on the metricsInstance to be able to mock private methods
       Developermetrics spy = PowerMockito.spy(metricsInstance);
       
       //Mock the private method commSize of Developermetrics
       PowerMockito.doReturn(1L).when(spy, "commSize", any(ProjectVersion.class), anyLong());
       
       //Mock the addRecord method from the DBService
       PowerMockito.when(dbMock.addRecord(any(DAObject.class))).thenReturn(true);
       
       //Verify that none of the methods have been called at the start of the test
       PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));

       //Verify that none  the Metric class method has not been called before the start of the test
       PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
  	 
       
		//Setup the projectVersion for testing purposes
		ProjectVersion v = mock(ProjectVersion.class);
		v.setTimestamp(1000);
		v.setProject(mock(StoredProject.class));
		PowerMockito.when(v.getVersionFiles()).thenReturn(null);
		Directory dir = new Directory();
		dir.setPath("path");
		//Setup the project file for testing purposes
		ProjectFile file = new ProjectFile();
		file.setId(1000);
		file.setDir(dir);
		file.setProjectVersion(v);
		file.setIsDirectory(true);
		
		 //Setup the metric class for testing
		   PowerMockito.mockStatic(Directory.class);
		   
		   //mock the getmetric method
		   Mockito.when(Directory.getDirectory(anyString(), eq(false))).thenReturn(dir);

       //Execute the method to be tested
       spy.run(file);
       
       //Get the static strings from the class for verification of the metric calls
       int MASK_FILES =  Whitebox.<Integer>getInternalState(ProjectVersion.class, "MASK_FILES");
       String MNEM_EYEBALL_MOD = Whitebox.<String>getInternalState(Developermetrics.class, "MNEM_EYEBALL_MOD");
     		
       //Check if the getMetricByMnemonic call was done with the right parameter
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq(MNEM_EYEBALL_MOD));
       
       //Check if indeed three records are added to the database
       PowerMockito.verifyPrivate(dbMock,times(1)).invoke("addRecord", any(DAObject.class));     
   }
	
	@Test 
	public void testCommSizeMethod() throws Exception
	{
		//Setup the projectVersion for testing purposes
		ProjectVersion v = new ProjectVersion();
		v.setTimestamp(1000);
		v.setProject(mock(StoredProject.class));
		
		//Setup the expected list
		List<Long> expected = new ArrayList<Long>();
		expected.add(500L);
		
		//Get the static query string value in order to verify the call
		String expectedQueryString  = Whitebox.<String>getInternalState(Developermetrics.class, "activeLast");
		//mock the database invocation
		doReturn(expected).when(dbMock).doHQL(eq(expectedQueryString),Matchers.<Map<String,Object>>any());

		//Invoke the method
		long answer = Whitebox.<Long> invokeMethod(metricsInstance,	"commSize", v, 200L);
		
		//Verify if the expected value is the gotten value
		assertEquals((long)expected.get(0), answer);
	}
	
	@Test 
	public void testfileEyeballsMethod() throws Exception
	{
		
		//Setup the projectVersion for testing purposes
		ProjectVersion v = new ProjectVersion();
		v.setTimestamp(1000);
		v.setProject(mock(StoredProject.class));
		
		//Setup the project file for testing purposes
		ProjectFile file = new ProjectFile();
		file.setId(1000);
		file.setProjectVersion(v);
		
		//Setup the expected list
		List<Developer> expected = new ArrayList<Developer>();

		//Get the static query string value in order to verify the call
		String expectedQueryString  = Whitebox.<String>getInternalState(Developermetrics.class, "fileEyeballs");
		//mock the database invocation
		doReturn(expected).when(dbMock).doHQL(eq(expectedQueryString),Matchers.<Map<String,Object>>any());

		//Invoke the method
		List<Developer> answer  = Whitebox.<List<Developer>> invokeMethod(metricsInstance,	"fileEyeballs", file);
		
		//Verify if the expected value is the gotten value
		assertEquals(expected, answer);
	}
	
	
}
