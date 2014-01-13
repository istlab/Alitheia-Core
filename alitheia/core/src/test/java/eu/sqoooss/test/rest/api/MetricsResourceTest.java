package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqoooss.test.rest.api.utils.TestUtils;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.MetricsResource;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.pa.PluginAdmin;

@PrepareForTest({ AlitheiaCore.class, Metric.class, MetricType.class, DAObject.class, Type.class, AlitheiaPlugin.class, PluginAdmin.class})
@RunWith(PowerMockRunner.class)
public class MetricsResourceTest {
	private DBService db;

	/************Auxiliar methods**************/
	private void httpRequestFireAndTestAssertations(String api_path, String r) throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				MetricsResource.class, api_path);
		System.out.println("Aqui: " + response.getContentAsString());
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals(r, response.getContentAsString());
	}
	

	@Before
	public void setUp() {
		AlitheiaCore core = PowerMockito.mock(AlitheiaCore.class);

		PowerMockito.mockStatic(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(core);

		db = PowerMockito.mock(DBService.class);
		Mockito.when(core.getDBService()).thenReturn(db);

	}

	@After
	public void tearDown() {
		db = null;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testGetMetrics() throws Exception {
		
		Metric m1 = PowerMockito.mock(Metric.class);
		Metric m2 = PowerMockito.mock(Metric.class);;
		List l = new ArrayList<Metric>();
		l.add(m1);
		l.add(m2);
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+ "<collection><metric><id>0</id></metric><metric><id>0</id></metric></collection>";
			
		Mockito.when(db.doHQL(Mockito.anyString())).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/metrics/", r);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testGetMetricTypes() throws Exception {
		MetricType mt1 = PowerMockito.mock(MetricType.class);
		MetricType mt2 = PowerMockito.mock(MetricType.class);
		List l = new ArrayList<MetricType>();
		l.add(mt1);
		l.add(mt2);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><metrictype><id>0</id></metrictype><metrictype><id>0</id></metrictype></collection>";
		
		Mockito.when(db.doHQL(Mockito.anyString())).thenReturn(l);
		httpRequestFireAndTestAssertations("api/metrics/types", r);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetMetricById() throws Exception {
		
		Metric m = new Metric();
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<metric><id>0</id></metric>";

		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(m);

		httpRequestFireAndTestAssertations("api/metrics/by-id/5", r);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetMetricResultNullMetric() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		
		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(null);		
		httpRequestFireAndTestAssertations("api/metrics/by-id/5/result/test", r);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetMetricResultWithMetric() throws Exception {
		
		Result r1 = new Result();
		Result r2 = new Result();
		List l = new ArrayList<Result>();
		l.add(r1);
		l.add(r2);
				
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		
		MetricsResource api = PowerMockito.mock(MetricsResource.class);
		Mockito.when(api.getResult(Mockito.any(Metric.class), Mockito.anyString())).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/metrics/by-id/5/result/test", r);
	}
	
	//TODO testGetResult
	@Test
	public void testGetResult() throws Exception {
	}
	
		
	@Test
	public void testGetMetricByMnem() throws Exception {
		Metric m = new Metric();
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<metric><id>0</id></metric>";
		
		PowerMockito.mockStatic(Metric.class);
		Mockito.when(Metric.getMetricByMnemonic(Mockito.anyString())).thenReturn(m);
		
		httpRequestFireAndTestAssertations("api/metrics/by-mnem/mnemonic", r);
	}
	
	@Test
	public void testGetMetricResultByMnemNullMetric() throws Exception {
				
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
	
		PowerMockito.mockStatic(Metric.class);
		Mockito.when(Metric.getMetricByMnemonic(Mockito.anyString())).thenReturn(null);
		
		httpRequestFireAndTestAssertations("api/metrics/by-mnem/mnemonic/result/test", r);
	}
	
	//FIXME
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetMetricResultByMnemWithMetric() throws Exception {
		Result r1 = new Result();
		Result r2 = new Result();
		List l = new ArrayList<Result>();
		l.add(r1);
		l.add(r2);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		
		
		/*PowerMockito.mockStatic(Metric.class);
		Metric m = new Metric();
		Mockito.when(Metric.getMetricByMnemonic(Mockito.anyString())).thenReturn(m);*/
		
		MetricsResource api = PowerMockito.mock(MetricsResource.class);
		Mockito.when(api.getResult(Mockito.any(Metric.class), Mockito.anyString())).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/metrics/by-mnem/mnemonic/result/test", r);
	}
	
	@Test
	public void testGetMetricByTypeNullMetric() throws Exception {
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
	
		PowerMockito.mockStatic(MetricType.class);
		PowerMockito.mockStatic(Type.class);
		Mockito.when(MetricType.getMetricType(Type.fromString(Mockito.anyString()))).thenReturn(null);
		
		httpRequestFireAndTestAssertations("api/metrics/by-type/test", r);
	}
	
	@Test
	public void testGetMetricByTypeWithMetric() throws Exception {
		Set<Metric> s = new HashSet<Metric>();
		Metric m1 = PowerMockito.mock(Metric.class);
		Metric m2 = PowerMockito.mock(Metric.class);
		s.add(m1);
		s.add(m2);
		
		MetricType mt = PowerMockito.mock(MetricType.class);
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><metric><id>0</id></metric><metric><id>0</id></metric></collection>";
	
		PowerMockito.mockStatic(MetricType.class);
		PowerMockito.mockStatic(Type.class);
		Mockito.when(MetricType.getMetricType(Type.fromString(Mockito.anyString()))).thenReturn(mt);
		
		Mockito.when(mt.getMetrics()).thenReturn(s);
		
		httpRequestFireAndTestAssertations("api/metrics/by-type/test", r);
	}
}