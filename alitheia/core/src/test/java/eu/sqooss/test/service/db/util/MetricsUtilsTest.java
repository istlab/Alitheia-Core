package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.MetricsUtils;

@RunWith(MockitoJUnitRunner.class)
public class MetricsUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private MetricsUtils mu;
	private Metric expectedMetric;
	private MetricType expectedMetricType;

	@Before
	public void setUp() {
		 this.mu = new MetricsUtils(this.dbService);
		 
		 this.expectedMetric = new Metric();
		 this.expectedMetricType = new MetricType(Type.BUG);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getMetricByMnemonicTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedMetric)));
		
		Metric actual = mu.getMetricByMnemonic("DERP");

		assertEquals(expectedMetric, actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getMetricByMnemonicEmptyTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		Metric actual = mu.getMetricByMnemonic("DERP");

		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getMetricTypeTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedMetricType)));
		
		MetricType actual = mu.getMetricType(Type.BUG);

		assertEquals(expectedMetricType, actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getMetricTypeEmptyTest() {
		when(   dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		MetricType actual = mu.getMetricType(Type.BUG);

		assertNull(actual);
	}
}
