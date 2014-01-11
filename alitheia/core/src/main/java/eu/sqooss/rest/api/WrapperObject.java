package eu.sqooss.rest.api;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
@XmlRootElement(name="WrapperObject")
public class WrapperObject<T> {
	
	@XmlElement
	T data;
	
	public WrapperObject(){};
	
	public WrapperObject(T data){
		this.data = data;
	}
	
}
**/

