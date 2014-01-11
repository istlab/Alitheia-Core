package eu.sqooss.rest.api.wrappers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="map_entry")
public class XMLMapEntry<K, V> {
	
	public XMLMapEntry(){}
	
	public XMLMapEntry(K k, V v) {
		this.key = k;
		this.value = v;
	}
	
    @XmlElement(name="key")
    private K key; 

    @XmlElement(name="value")
    private V value;
}
