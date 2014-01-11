package eu.sqooss.rest.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.rest.api.wrappers.XMLMapEntry;


@XmlRootElement(name="XMLMap")
public class XMLMap<K, V> {
	
	@XmlElement
	List<XMLMapEntry<K, V>> entry;
	
	public XMLMap() {
		this.entry = new ArrayList<XMLMapEntry<K, V>>();
	}
	
}
