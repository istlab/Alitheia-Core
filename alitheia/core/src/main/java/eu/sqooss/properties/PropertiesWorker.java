package eu.sqooss.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class PropertiesWorker {

	private final Properties properties;

	public PropertiesWorker() {
		this.properties = new Properties();
	}
	
	public void load(InputStream stream) throws IOException {
		this.properties.load(stream);
	}

	public void setProperty(PropertyKey key, String value) {
		this.setProperty(key.getKey(), value);
	}

	private void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	public String getProperty(PropertyKey key) {
		return this.getProperty(key.getKey());
	}

	private String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public Set<Entry<Object, Object>> entrySet() {
		return this.properties.entrySet();
	}
}
