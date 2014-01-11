package eu.sqooss.rest.api.wrappers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="map_entry")
public class JaxbMapEntry<K, V> {
	
	public JaxbMapEntry(){}
	
	public JaxbMapEntry(K k, V v) {
		this.key = k;
		this.value = v;
	}
	
    @XmlElement(name="key")
    private K key; 

    @XmlElement(name="value")
    private V value;
}
