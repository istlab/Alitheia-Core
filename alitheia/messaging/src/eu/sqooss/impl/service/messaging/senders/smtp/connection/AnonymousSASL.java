package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Properties;

import eu.sqooss.impl.service.messaging.senders.smtp.utils.Base64;

public class AnonymousSASL implements SASL {

  public String getResponse(Properties props, String serverResponse) {
    return new String(Base64.encode(props.getProperty(Constants.USER).getBytes()));
  }

  public String getID() {
    return "ANONYMOUS";
  }

}
