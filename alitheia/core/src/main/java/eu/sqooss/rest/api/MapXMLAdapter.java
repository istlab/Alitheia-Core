package eu.sqooss.rest.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.sqooss.rest.api.wrappers.XMLMapEntry;


public final class MapXMLAdapter<K, V>  extends XmlAdapter<XMLMap<K, V>, Map<K, V>>{

	@Override
	public Map<K, V> unmarshal(XMLMap<K, V> v) throws Exception {
		System.out.println("unmarshal");
		return new HashMap<K, V>();
	}

	@Override
	public XMLMap<K, V> marshal(Map<K, V> v) throws Exception {
		XMLMap<K, V> m = new XMLMap<K, V>();
		
		Set<K> keySet = (Set<K>)v.keySet();
		
		for ( K k :  keySet )
			m.entry.add(new XMLMapEntry<K, V>(k, (V)v.get(k)));
		
		return m;
	}

}
