package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Enumeration;
import java.util.Hashtable;

public class DefaultSASLFactory implements SASLFactory {

  Hashtable recognizedSecurityMethods = new Hashtable();

  public DefaultSASLFactory(){
    SASL tmp = new CramMD5SASL();
    recognizedSecurityMethods.put(tmp.getID(),tmp);
    tmp = new LoginSASL();
    recognizedSecurityMethods.put(tmp.getID(),tmp);
    tmp = new PlainSASL();
    recognizedSecurityMethods.put(tmp.getID(),tmp);
    tmp = new AnonymousSASL();
    recognizedSecurityMethods.put(tmp.getID(),tmp);
  }
  

  public SASL[] getAvailableSASLs() {
	  SASL names[] = new SASL[recognizedSecurityMethods.size()];
	  Enumeration enumeration = recognizedSecurityMethods.elements();
	  int count = 0;
	  while (enumeration.hasMoreElements()) {
		  names[count++] = (SASL)enumeration.nextElement();
	  }
	  return names;
  }
  
  public SASL getSASL(String id) {
    return (SASL)recognizedSecurityMethods.get(id);
  }

}
