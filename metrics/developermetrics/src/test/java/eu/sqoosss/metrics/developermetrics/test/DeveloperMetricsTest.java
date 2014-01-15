package eu.sqoosss.metrics.developermetrics.test;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.metrics.developermetrics.Developermetrics;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;



@RunWith(PowerMockRunner.class)
@PrepareForTest({Metric.class, Developermetrics.class, DBService.class})
public class DeveloperMetricsTest {
 @Mock(name = "db") private DBService dbMock; 
 @InjectMocks private Developermetrics metricsInstance;

 @Before public void initMocks() {

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
       PowerMockito.verifyPrivate(dbMock,times(0)).invoke("addRecord", any(DAObject.class));

       //Verify that none  the Metric class method has not been called before the start of the test
       PowerMockito.verifyPrivate(Metric.class,times(0)).invoke("getMetricByMnemonic", anyString());
  	 
       //Execute the method to be tested
       spy.run(mock(ProjectVersion.class));
       
       //Check if the three getMetricCalls have been performed
       PowerMockito.verifyPrivate(Metric.class,times(3)).invoke("getMetricByMnemonic", anyString());
       
       //Check if these calls were done with the right parameters
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq("TEAMSIZE1"));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq("TEAMSIZE3"));
       PowerMockito.verifyPrivate(Metric.class,times(1)).invoke("getMetricByMnemonic", eq("TEAMSIZE6"));
       
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
}
