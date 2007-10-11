package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Properties;

import eu.sqooss.impl.service.messaging.senders.smtp.utils.Base64;

public class PlainSASL implements SASL {

  public String getID() {
    return "PLAIN";
  }

  public String getResponse(Properties props, String serverResponse) {
    return new String(Base64.encode((""+((char)0)+props.getProperty(Constants.USER)+((char)0)+props.getProperty(Constants.PASS)).getBytes()));
  }

}
