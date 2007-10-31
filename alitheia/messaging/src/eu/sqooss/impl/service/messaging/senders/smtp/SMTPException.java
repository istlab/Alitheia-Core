package eu.sqooss.impl.service.messaging.senders.smtp;

import eu.sqooss.impl.service.messaging.senders.smtp.connection.ConnectionException;

public class SMTPException extends ConnectionException {
    private static final long serialVersionUID = 1L;

    public SMTPException(String arg0) {
        super(arg0);
        type = TYPE_SMTP;
    }

    public SMTPException(String arg0, int code) {
        this(arg0);
        errorCode = code;
    }

    public SMTPException(String arg0, int code, String errorMessage) {
        this(arg0, code);
        this.errorMessage = errorMessage;
    }
}
