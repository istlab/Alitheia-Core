package eu.sqooss.rest.api.wrappers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="string")
public class JaxbString {

   private String value;

   public JaxbString(){}

   public JaxbString(String v){
       this.setValue(v);
   }

   public void setValue(String value) {
       this.value = value;
   }

   @XmlElement(name="value")
   public String getValue() {
       return value;
   }

}
