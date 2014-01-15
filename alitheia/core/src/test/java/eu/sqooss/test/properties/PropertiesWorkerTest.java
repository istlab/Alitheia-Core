package eu.sqooss.test.properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.properties.PropertiesWorker;
import eu.sqooss.properties.PropertyKey;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesWorkerTest {

	private PropertiesWorker worker;
	@Mock private PropertyKey key;

	@Before
	public void setUp() {
		this.worker = new PropertiesWorker();
	}

	@Test
	public void testLoad() throws IOException {
		InputStream stream = mock(InputStream.class);
		this.worker.load(stream);
		
		verify(stream).read((byte[]) any());
	}
	
	@Test
	public void testSetGetProperty() {
		when(this.key.getKey()).thenReturn("foo");
		this.worker.setProperty(this.key, "bar");
		
		assertEquals("bar", this.worker.getProperty(this.key));
	}
	
	@Test
	public void testEntrySet() {
		when(this.key.getKey()).thenReturn("foo", "bar", "baz", "qux");
		this.worker.setProperty(this.key, "foo2");
		this.worker.setProperty(this.key, "bar2");
		this.worker.setProperty(this.key, "baz2");
		this.worker.setProperty(this.key, "qux2");
		
		Set<Entry<Object, Object>> result = this.worker.entrySet();
		for (Entry<Object, Object> entry : result) {
			assertEquals(entry.getValue(), entry.getKey() + "2");
		}
	}
}
