package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Properties;

import eu.sqooss.impl.service.messaging.senders.smtp.utils.Base64;

public class LoginSASL implements SASL {

    public String getID() {
        return "LOGIN";
    }

    public String getResponse(Properties props, String serverResponse) {
        String user = props.getProperty(Constants.USER);
        String pass = props.getProperty(Constants.PASS);
        if (serverResponse == null || serverResponse.equals("") || user == null || pass == null) {
            return null;
        }
        byte[] text = null;
        try {
            text = Base64.decode(serverResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String decodeServerResponse = new String(text);
        decodeServerResponse = decodeServerResponse.trim().toLowerCase();
        if (decodeServerResponse.equalsIgnoreCase("username:")) {
            return new String(Base64.encode(user.getBytes()));
        } else if (decodeServerResponse.equalsIgnoreCase("password:")) {
            return new String(Base64.encode(pass.getBytes()));
        }
        return new String(Base64.encode(("" + ((char)0) + user + ((char)0) + pass).getBytes()));
    }

}
